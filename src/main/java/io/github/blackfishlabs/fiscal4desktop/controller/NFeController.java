package io.github.blackfishlabs.fiscal4desktop.controller;

import br.indie.fiscal4j.nfe.classes.distribuicao.NFDistribuicaoDocumentoZip;
import br.indie.fiscal4j.nfe.classes.distribuicao.NFDistribuicaoIntRetorno;
import br.indie.fiscal4j.nfe400.classes.evento.NFEnviaEventoRetorno;
import br.indie.fiscal4j.nfe400.classes.evento.NFEventoRetorno;
import br.indie.fiscal4j.nfe400.classes.evento.NFInfoEventoRetorno;
import br.indie.fiscal4j.nfe400.classes.evento.inutilizacao.NFRetornoEventoInutilizacao;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvio;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvioRetornoDados;
import br.indie.fiscal4j.nfe400.classes.nota.NFNota;
import br.indie.fiscal4j.nfe400.classes.nota.consulta.NFNotaConsultaRetorno;
import br.indie.fiscal4j.nfe400.classes.nota.consulta.NFProtocoloEvento;
import io.github.blackfishlabs.fiscal4desktop.common.helper.DateHelper;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FileHelper;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalConstantHelper;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.*;
import io.github.blackfishlabs.fiscal4desktop.controller.translator.*;
import io.github.blackfishlabs.fiscal4desktop.domain.model.NFeEntity;
import io.github.blackfishlabs.fiscal4desktop.domain.repository.NFeRepository;
import io.github.blackfishlabs.fiscal4desktop.infra.NFeConfiguration;
import io.github.blackfishlabs.fiscal4desktop.service.NFeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import static com.google.common.base.Preconditions.checkArgument;

@Service
public class NFeController {

    @Autowired
    private NFeRepository nFeRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(NFeController.class);

    public String send(FiscalSendDTO sendDTO) throws Exception {
        NFeService service = new NFeService();
        FiscalDocumentTranslator translator = new FiscalDocumentTranslator();
        NFeConfiguration configuration = new NFeConfiguration(sendDTO.getEmitter(), sendDTO.getPassword());

        NFLoteEnvio nfLoteEnvio = translator.fromDTO(sendDTO.getFiscalDocumentDTO());

        NFLoteEnvioRetornoDados send = service.send(configuration, nfLoteEnvio);

        checkArgument(send.getRetorno().getStatus().equals("104"),
                send.getRetorno().getStatus().concat(" - ").
                        concat(send.getRetorno().getMotivo()));

        LOGGER.info("Nota autorizada pelo protocolo: ".concat(send.getRetorno().getProtocoloInfo().getNumeroProtocolo()));

        FileHelper.saveFilesAndSendToEmailAttach(send);
        saveDocInDatabase(send);

        return translator.response(send);
    }

    public String cancel(FiscalEventCancellationDTO dto) throws Exception {
        NFeService service = new NFeService();
        FiscalCancelTranslator translator = new FiscalCancelTranslator();

        List<NFeEntity> filterNFe = nFeRepository.search(dto.getKey(), dto.getEmitter());
        Optional<NFeEntity> nFeEntity = filterNFe.stream().findFirst();

        if (nFeEntity.isPresent()) {
            dto.setProtocol(nFeEntity.get().getProtocol());

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
                        String path = FiscalConstantHelper.NFE_PATH;
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

                    nFeEntity.get().setXmlCancel(procEventNFe);
                    nFeEntity.get().setProtocolCancel(event.get().getInfoEventoRetorno().getNumeroProtocolo());
                    nFeRepository.save(nFeEntity.get());

                }
                return translator.response(cancel);
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
                "NFe: ".concat(disablement.getDados().getStatus().concat(" - ").
                        concat(disablement.getDados().getMotivo())));
        return translator.response(disablement);
    }

    public String status(FiscalStatusDocumentDTO dto) throws Exception {
        NFeService service = new NFeService();
        FiscalStatusDocumentTranslator translator = new FiscalStatusDocumentTranslator();

        NFNotaConsultaRetorno status = service.status(translator.fromDTO(dto));
        return translator.response(status);
    }

    public static String decodeGZipToXml(final String conteudoEncode) throws Exception {
        if (conteudoEncode == null || conteudoEncode.length() == 0) {
            return "";
        }
        final byte[] conteudo = Base64.getDecoder().decode(conteudoEncode);
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(conteudo))) {
            try (BufferedReader bf = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8))) {
                StringBuilder outStr = new StringBuilder();
                String line;
                while ((line = bf.readLine()) != null) {
                    outStr.append(line);
                }
                return outStr.toString();
            }
        }
    }

    public String queryDFe(FiscalQueryDistributionDFeDTO dto) throws Exception {
        NFeService service = new NFeService();
        FiscalQueryDistributionDFeTranslator translator = new FiscalQueryDistributionDFeTranslator();

        NFDistribuicaoIntRetorno response = service.queryDFe(translator.fromDTO(dto));
        List<NFDistribuicaoDocumentoZip> listaDoc = response.getLote().getDocZip();

        System.out.println("Encontrado " + listaDoc.size() + " Notas.");
        for (NFDistribuicaoDocumentoZip docZip : listaDoc) {
            System.out.println("Schema: " + docZip.getSchema());
            System.out.println("NSU:" + docZip.getNsu());
            System.out.println("XML: " + decodeGZipToXml(docZip.getValue()));
        }

        return translator.response(response);
    }

    private void saveDocInDatabase(NFLoteEnvioRetornoDados send) {
        NFeTranslator translator = new NFeTranslator();

        new Thread(() -> {
            try {
                LOGGER.info("Salvando documentos no Banco de Dados");
                Optional<NFNota> document = send.getLoteAssinado().getNotas().stream().findFirst();
                document.ifPresent(d -> nFeRepository.save(translator.toEntity(send)));
            } catch (Exception e) {
                LOGGER.error("Erro ao gravar no banco de dados!");
                LOGGER.error(e.getMessage());
            }
        }).start();
    }

}
