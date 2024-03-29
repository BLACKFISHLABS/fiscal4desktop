package io.github.blackfishlabs.fiscal4desktop.controller;

import br.indie.fiscal4j.nfe400.classes.evento.NFEnviaEventoRetorno;
import br.indie.fiscal4j.nfe400.classes.evento.NFEventoRetorno;
import br.indie.fiscal4j.nfe400.classes.evento.NFInfoEventoRetorno;
import br.indie.fiscal4j.nfe400.classes.nota.consulta.NFNotaConsultaRetorno;
import br.indie.fiscal4j.nfe400.classes.nota.consulta.NFProtocoloEvento;
import io.github.blackfishlabs.fiscal4desktop.common.helper.DateHelper;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FileHelper;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalConstantHelper;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.FiscalEventCCeDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.FiscalStatusDocumentDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.translator.FiscalCCeTranslator;
import io.github.blackfishlabs.fiscal4desktop.controller.translator.FiscalStatusDocumentTranslator;
import io.github.blackfishlabs.fiscal4desktop.service.CCeService;
import io.github.blackfishlabs.fiscal4desktop.service.NFeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.isNull;

@Slf4j
@Service
public class CCeController {

    public String sendCCe(FiscalEventCCeDTO dto) throws Exception {
        CCeService service = new CCeService();
        NFeService nFeService = new NFeService();
        FiscalCCeTranslator translator = new FiscalCCeTranslator();

        log.info(String.format("Chave da Nota %s >> buscando da sefaz...", dto.getKey()));
        FiscalStatusDocumentTranslator statusDocumentTranslator = new FiscalStatusDocumentTranslator();
        FiscalStatusDocumentDTO statusDocumentDTO = new FiscalStatusDocumentDTO();
        statusDocumentDTO.setEmitter(dto.getEmitter());
        statusDocumentDTO.setPassword(dto.getPassword());
        statusDocumentDTO.setKey(dto.getKey());

        NFNotaConsultaRetorno check = nFeService.status(statusDocumentTranslator.fromDTO(statusDocumentDTO));
        if (!isNull(check.getProtocoloEvento())) {
            Optional<NFProtocoloEvento> protocolEventCheck = check.getProtocoloEvento().stream().reduce((first, second) -> second);
            if (protocolEventCheck.isPresent()) {
                int sequential = protocolEventCheck.get().getEvento().getInfoEvento().getNumeroSequencialEvento();
                dto.setSeq(sequential + 1);
            }
        } else {
            dto.setSeq(1);
        }

        NFEnviaEventoRetorno cce = service.sendCCe(translator.fromDTO(dto));
        Optional<NFEventoRetorno> event = cce.getEventoRetorno().stream().findFirst();

        if (event.isPresent()) {
            NFInfoEventoRetorno info = event.get().getInfoEventoRetorno();

            checkArgument(info.getCodigoStatus().equals(135),
                    info.getCodigoStatus().toString().concat(" - ").
                            concat(info.getMotivo()));
            log.info("Carta de Correção emitida pelo protocolo: ".concat(event.get().getInfoEventoRetorno().getNumeroProtocolo()));

            FiscalStatusDocumentTranslator fiscalStatusDocumentTranslator = new FiscalStatusDocumentTranslator();
            FiscalStatusDocumentDTO fiscalStatusDocumentDTO = new FiscalStatusDocumentDTO();
            fiscalStatusDocumentDTO.setEmitter(dto.getEmitter());
            fiscalStatusDocumentDTO.setPassword(dto.getPassword());
            fiscalStatusDocumentDTO.setKey(dto.getKey());

            NFNotaConsultaRetorno status = nFeService.status(fiscalStatusDocumentTranslator.fromDTO(fiscalStatusDocumentDTO));
            Optional<NFProtocoloEvento> protocolEvent = status.getProtocoloEvento().stream().reduce((first, second) -> second);

            if (protocolEvent.isPresent()) {
                final String eventNFe = protocolEvent.get().toString();

                try {
                    String path = FiscalConstantHelper.CCE_PATH;
                    String xmlPath = FiscalProperties.getInstance().getDirXML()
                            .concat(path)
                            .concat(DateHelper.toDirFormat(new Date()))
                            .concat("/")
                            .concat(info.getChave() + "-" + info.getNumeroSequencialEvento())
                            .concat("-cce.xml");

                    FileHelper.exportXml(eventNFe, xmlPath);
                    FileHelper.exportFilesPDFOnly(protocolEvent.get());
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }

        }

        return translator.response(cce);
    }
}
