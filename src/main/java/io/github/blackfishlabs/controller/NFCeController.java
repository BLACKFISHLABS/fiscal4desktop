package io.github.blackfishlabs.controller;

import br.indie.fiscal4j.danfe.DFParser;
import br.indie.fiscal4j.nfe400.classes.evento.NFEnviaEventoRetorno;
import br.indie.fiscal4j.nfe400.classes.evento.NFEventoRetorno;
import br.indie.fiscal4j.nfe400.classes.evento.NFInfoEventoRetorno;
import br.indie.fiscal4j.nfe400.classes.evento.inutilizacao.NFRetornoEventoInutilizacao;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvio;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvioRetornoDados;
import br.indie.fiscal4j.nfe400.classes.nota.NFNota;
import br.indie.fiscal4j.nfe400.classes.nota.consulta.NFNotaConsultaRetorno;
import br.indie.fiscal4j.nfe400.classes.nota.consulta.NFProtocoloEvento;
import io.github.blackfishlabs.common.helper.DateHelper;
import io.github.blackfishlabs.common.helper.FileHelper;
import io.github.blackfishlabs.common.helper.FiscalConstantHelper;
import io.github.blackfishlabs.common.helper.FiscalHelper;
import io.github.blackfishlabs.common.properties.FiscalProperties;
import io.github.blackfishlabs.controller.dto.FiscalDisablementDTO;
import io.github.blackfishlabs.controller.dto.FiscalEventCancellationDTO;
import io.github.blackfishlabs.controller.dto.FiscalSendDTO;
import io.github.blackfishlabs.controller.dto.FiscalStatusDocumentDTO;
import io.github.blackfishlabs.controller.translator.*;
import io.github.blackfishlabs.domain.model.NFCeEntity;
import io.github.blackfishlabs.domain.repository.ContingencyRepository;
import io.github.blackfishlabs.domain.repository.NFCeRepository;
import io.github.blackfishlabs.infra.NFeConfiguration;
import io.github.blackfishlabs.service.NFeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

public class NFCeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NFCeController.class);

    @Autowired
    private NFCeRepository nfCeRepository;
    @Autowired
    private ContingencyRepository contingencyRepository;

    public String send(FiscalSendDTO sendDTO) throws Exception {

        NFeService service = new NFeService();
        FiscalDocumentTranslator translator = new FiscalDocumentTranslator();

        ContingencyTranslator contingencyTranslator = new ContingencyTranslator(sendDTO.getEmitter(), sendDTO.getPassword());

        NFeConfiguration configuration;

        if (sendDTO.getFiscalDocumentDTO().getIde().getTpAmb().equals("1")) {
            configuration = new NFeConfiguration(sendDTO.getEmitter(),
                    sendDTO.getPassword(),
                    sendDTO.getFiscalDocumentDTO().getIde().getCscP());
        } else {
            configuration = new NFeConfiguration(sendDTO.getEmitter(),
                    sendDTO.getPassword(),
                    sendDTO.getFiscalDocumentDTO().getIde().getCscH());
        }


        NFLoteEnvio nfLoteEnvio = translator.fromDTO(sendDTO.getFiscalDocumentDTO());

        if (!sendDTO.isContingency()) {
            NFLoteEnvioRetornoDados send = service.send(configuration, nfLoteEnvio);

            checkArgument(send.getRetorno().getStatus().equals("104"),
                    send.getRetorno().getStatus().concat(" - ").
                            concat(send.getRetorno().getMotivo()));

            LOGGER.info("Nota autorizada pelo protocolo: ".concat(send.getRetorno().getProtocoloInfo().getNumeroProtocolo()));

            FileHelper.saveFilesAndSendToEmailAttach(send);
            saveDocInDatabase(send);

            return translator.response(send);
        } else {
            String contingency = service.contingency(configuration, nfLoteEnvio);
            LOGGER.info("Nota assinada em contingência: ".concat(contingency));

            saveDocInDatabase(contingency, contingencyTranslator);

            NFLoteEnvio lotSender = new DFParser().loteParaObjeto(contingency);
            NFNota first = lotSender.getNotas().get(0);
            FileHelper.exportFilesPDFOnly(FiscalHelper.getNFProcessed(first));

            return translator.response(first);
        }
    }

    public String cancel(FiscalEventCancellationDTO dto) throws Exception {
        NFeService service = new NFeService();
        FiscalCancelTranslator translator = new FiscalCancelTranslator();

        List<NFCeEntity> filterNFCe = nfCeRepository.search(dto.getKey(), dto.getEmitter());
        Optional<NFCeEntity> nfCeEntity = filterNFCe.stream().findFirst();

        if (nfCeEntity.isPresent()) {
            dto.setProtocol(nfCeEntity.get().getProtocol());

            NFEnviaEventoRetorno cancel = service.cancel(translator.fromDTO(dto));
            Optional<NFEventoRetorno> event = cancel.getEventoRetorno().stream().findFirst();

            if (event.isPresent()) {
                NFInfoEventoRetorno info = event.get().getInfoEventoRetorno();

                checkArgument(info.getCodigoStatus().equals(135),
                        info.getCodigoStatus().toString().concat(" - ").
                                concat(info.getMotivo()));
                LOGGER.info("Nota cancelada pelo protocolo: ".concat(event.get().getInfoEventoRetorno().getNumeroProtocolo()));

                FiscalStatusDocumentTranslator fiscalStatusDocumentTranslator = new FiscalStatusDocumentTranslator();

                FiscalStatusDocumentDTO fiscalStatusDocumentDTO = new FiscalStatusDocumentDTO();
                fiscalStatusDocumentDTO.setEmitter(dto.getEmitter());
                fiscalStatusDocumentDTO.setPassword(dto.getPassword());
                fiscalStatusDocumentDTO.setKey(dto.getKey());

                NFNotaConsultaRetorno status = service.status(fiscalStatusDocumentTranslator.fromDTO(fiscalStatusDocumentDTO));
                Optional<NFProtocoloEvento> proc = status.getProtocoloEvento().stream().findFirst();

                if (proc.isPresent()) {
                    final String procEventNFe = proc.get().toString();
                    try {
                        String path = FiscalConstantHelper.NFCE_PATH;
                        path = path.concat(FiscalConstantHelper.CANCEL_PATH);

                        String xmlPath = FiscalProperties.getInstance().getDirXML()
                                .concat(path)
                                .concat(DateHelper.toDirFormat(new Date()))
                                .concat("/")
                                .concat(info.getChave())
                                .concat("-canc.xml");

                        FileHelper.exportXml(procEventNFe, xmlPath);
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage());
                    }

                    nfCeEntity.get().setXmlCancel(procEventNFe);
                    nfCeEntity.get().setProtocolCancel(event.get().getInfoEventoRetorno().getNumeroProtocolo());

                    nfCeRepository.save(nfCeEntity.get());

                    return translator.response(cancel);
                }
            }

        } else
            throw new RuntimeException(String.format("Nota %s não encontrada no banco de dados local", dto.getKey()));

        return "Não houve o cancelamento da nota: ".concat(dto.getKey());
    }

    public String disablement(FiscalDisablementDTO dto) throws Exception {
        NFeService service = new NFeService();
        FiscalDisablementTranslator translator = new FiscalDisablementTranslator();

        NFRetornoEventoInutilizacao disablement = service.disablement(translator.fromDTO(dto));
        checkArgument(disablement.getDados().getStatus().equals("102"),
                "NFCe: ".concat(disablement.getDados().getStatus().concat(" - ").
                        concat(disablement.getDados().getMotivo())));
        return translator.response(disablement);
    }

    private void saveDocInDatabase(String xml, ContingencyTranslator contingencyTranslator) {
        new Thread(() -> {
            try {
                LOGGER.info("Salvando documentos no Banco de Dados");

                contingencyRepository.save(contingencyTranslator.toEntity(xml));
            } catch (Exception e) {
                LOGGER.error("Erro ao gravar no banco de dados!");
                LOGGER.error(e.getMessage());
            }
        }).start();
    }

    private void saveDocInDatabase(NFLoteEnvioRetornoDados send) {
        NFCeTranslator translator = new NFCeTranslator();

        new Thread(() -> {
            try {
                LOGGER.info("Salvando documentos no Banco de Dados");
                Optional<NFNota> document = send.getLoteAssinado().getNotas().stream().findFirst();
                document.ifPresent(d -> nfCeRepository.save(translator.toEntity(send)));
            } catch (Exception e) {
                LOGGER.error("Erro ao gravar no banco de dados!");
                LOGGER.error(e.getMessage());
            }
        }).start();
    }

}
