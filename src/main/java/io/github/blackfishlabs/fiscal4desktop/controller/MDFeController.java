package io.github.blackfishlabs.fiscal4desktop.controller;

import br.indie.fiscal4j.mdfe3.classes.consultaRecibo.MDFeConsultaReciboRetorno;
import br.indie.fiscal4j.mdfe3.classes.consultanaoencerrados.MDFeConsultaNaoEncerradosRetorno;
import br.indie.fiscal4j.mdfe3.classes.lote.envio.MDFEnvioLote;
import br.indie.fiscal4j.mdfe3.classes.lote.envio.MDFEnvioLoteRetornoDados;
import br.indie.fiscal4j.mdfe3.classes.nota.MDFInfoSuplementar;
import br.indie.fiscal4j.mdfe3.classes.nota.MDFe;
import br.indie.fiscal4j.mdfe3.classes.nota.consulta.MDFeNotaConsultaRetorno;
import br.indie.fiscal4j.mdfe3.classes.nota.evento.MDFeProtocoloEvento;
import br.indie.fiscal4j.mdfe3.classes.nota.evento.MDFeRetorno;
import br.indie.fiscal4j.mdfe3.utils.MDFGeraQRCode;
import io.github.blackfishlabs.fiscal4desktop.common.helper.DateHelper;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FileHelper;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalConstantHelper;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalHelper;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.FiscalMDFeCancellationDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.FiscalMDFeClosingDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.FiscalMDFeSendDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.FiscalMDFeStatusNotClosingDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.translator.*;
import io.github.blackfishlabs.fiscal4desktop.domain.domain.FiscalMDFeStatusDomain;
import io.github.blackfishlabs.fiscal4desktop.domain.model.MDFeEntity;
import io.github.blackfishlabs.fiscal4desktop.domain.repository.MDFeRepository;
import io.github.blackfishlabs.fiscal4desktop.infra.MDFeConfiguration;
import io.github.blackfishlabs.fiscal4desktop.service.MDFeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.isNull;

@Slf4j
@Service
public class MDFeController {

    @Autowired
    private MDFeRepository mdFeRepository;

    public String send(FiscalMDFeSendDTO sendDTO) throws Exception {
        MDFeService service = new MDFeService();
        FiscalMDFeDocumentTranslator translator = new FiscalMDFeDocumentTranslator();
        MDFeConfiguration configuration = new MDFeConfiguration(sendDTO.getEmitter(), sendDTO.getPassword());

        MDFEnvioLote mdfEnvioLote = translator.fromDTO(sendDTO.getFiscalMDFeDocumentDTO());

        MDFe mdfe = mdfEnvioLote.getMdfe();
        MDFInfoSuplementar mdfInfoSuplementar = new MDFInfoSuplementar();
        mdfInfoSuplementar.setQrCodMDFe(new MDFGeraQRCode(mdfe, configuration).getQRCode());
        mdfe.setMdfInfoSuplementar(mdfInfoSuplementar);
        mdfEnvioLote.setMdfe(mdfe);

        MDFEnvioLoteRetornoDados send = service.send(configuration, mdfEnvioLote);

        checkArgument(send.getRetorno().getStatus().equals("103"),
                send.getRetorno().getStatus().concat(" - ").
                        concat(send.getRetorno().getMotivo()));

        log.info("Manifesto recebido pelo recibo: ".concat(send.getRetorno().getInfoRecebimento().getNumeroRecibo()));

        sendDTO.setKey(send.getRetorno().getInfoRecebimento().getNumeroRecibo());
        FiscalMDFeStatusTranslator fiscalMDFeStatusTranslator = new FiscalMDFeStatusTranslator();

        // Latency for async search
        Thread.sleep(5000);
        MDFeConsultaReciboRetorno status = service.status(fiscalMDFeStatusTranslator.fromDTO(sendDTO));
        log.info("Manifesto Retorno: ".concat(status.toString()));

        if (isNull(status.getMdfProtocolo()))
            throw new RuntimeException(status.getMotivo());

        checkArgument(status.getMdfProtocolo().getProtocoloInfo().getStatus().equals("100"),
                status.getMdfProtocolo().getProtocoloInfo().getStatus().concat(" - ").
                        concat(status.getMdfProtocolo().getProtocoloInfo().getMotivo()));

        String xmlPath = FiscalProperties.getInstance().getDirXML()
                .concat(FiscalConstantHelper.MDFE_PATH)
                .concat(DateHelper.toDirFormat(new Date()))
                .concat("/")
                .concat(status.getMdfProtocolo().getProtocoloInfo().getChave())
                .concat(".xml");

        FileHelper.exportXml(FiscalHelper.getMDFeProcessed(status, send.getLoteAssinado().getMdfe()).toString(), xmlPath);
        FileHelper.exportFilesPDFOnly(FiscalHelper.getMDFeProcessed(status, send.getLoteAssinado().getMdfe()));

        saveDocInDatabase(status, send);

        return fiscalMDFeStatusTranslator.response(status);
    }

    public String statusNotClosing(FiscalMDFeStatusNotClosingDTO dto) throws Exception {
        MDFeService service = new MDFeService();
        FiscalMDFeStatusNotClosingTranslator translator = new FiscalMDFeStatusNotClosingTranslator();

        MDFeConsultaNaoEncerradosRetorno consultaNaoEncerradosRetorno = service.statusNotClosing(translator.fromDTO(dto));

        if (isNull(consultaNaoEncerradosRetorno.getInfMDFe()))
            throw new RuntimeException("Não há manifestos para encerramentos!");

        return translator.response(consultaNaoEncerradosRetorno);
    }

    public String closing(FiscalMDFeClosingDTO dto) throws Exception {
        MDFeService service = new MDFeService();
        FiscalMDFeClosingTranslator translator = new FiscalMDFeClosingTranslator();

        MDFeRetorno closing = service.closing(translator.fromDTO(dto));

        checkArgument(closing.getEventoRetorno().getCodigoStatus().equals(135),
                closing.getEventoRetorno().getCodigoStatus().toString().concat(" - ").
                        concat(closing.getEventoRetorno().getMotivo()));
        log.info("Encerramento de manifesto emitida pelo protocolo: ".concat(closing.getEventoRetorno().getNumeroProtocolo()));

        return translator.response(closing);
    }

    public String cancel(FiscalMDFeCancellationDTO dto) throws Exception {
        MDFeService service = new MDFeService();
        FiscalMDFeCancelTranslator translator = new FiscalMDFeCancelTranslator();

        List<MDFeEntity> filter = mdFeRepository.search(dto.getKey(), dto.getEmitter());
        Optional<MDFeEntity> mdFeEntity = filter.stream().findFirst();

        if (mdFeEntity.isPresent()) {
            dto.setProtocol(mdFeEntity.get().getProtocol());
            MDFeRetorno cancel = service.cancel(translator.fromDTO(dto));

            checkArgument(cancel.getEventoRetorno().getCodigoStatus().equals(135),
                    cancel.getEventoRetorno().getCodigoStatus().toString().concat(" - ").
                            concat(cancel.getEventoRetorno().getMotivo()));
            log.info("Cancelamento de manifesto emitida pelo protocolo: ".concat(cancel.getEventoRetorno().getNumeroProtocolo()));

            FiscalMDFeStatusDomain domain = new FiscalMDFeStatusDomain();
            domain.setConfiguration(new MDFeConfiguration(dto.getEmitter(), dto.getPassword()));
            domain.setKey(dto.getKey());
            MDFeNotaConsultaRetorno status = service.statusMDFe(domain);

            Optional<MDFeProtocoloEvento> proc = status.getProtocoloEvento().stream().findFirst();

            if (proc.isPresent()) {
                final String procEventNFe = proc.get().toString();

                try {
                    String path = FiscalConstantHelper.MDFE_PATH;
                    path = path.concat(FiscalConstantHelper.CANCEL_PATH);

                    String xmlPath = FiscalProperties.getInstance().getDirXML()
                            .concat(path)
                            .concat(DateHelper.toDirFormat(new Date()))
                            .concat("/")
                            .concat(domain.getKey())
                            .concat("-canc.xml");

                    FileHelper.exportXml(procEventNFe, xmlPath);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }

                mdFeEntity.get().setXmlCancel(procEventNFe);
                mdFeEntity.get().setProtocolCancel(cancel.getEventoRetorno().getNumeroProtocolo());
                mdFeRepository.save(mdFeEntity.get());
            }

            return translator.response(cancel);
        } else
            throw new RuntimeException(String.format("Manifesto %s não encontrada no banco de dados local", dto.getKey()));
    }

    private void saveDocInDatabase(MDFeConsultaReciboRetorno result, MDFEnvioLoteRetornoDados send) {
        MDFeTranslator translator = new MDFeTranslator();

        new Thread(() -> {
            try {
                log.info("Salvando documentos no Banco de Dados");
                mdFeRepository.save(translator.toEntity(result, send));
            } catch (Exception e) {
                log.error("Erro ao gravar no banco de dados!");
                log.error(e.getMessage());
            }
        }).start();
    }
}
