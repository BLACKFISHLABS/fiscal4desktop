package io.github.blackfishlabs.controller;

import br.indie.fiscal4j.nfe400.classes.evento.NFEnviaEventoRetorno;
import br.indie.fiscal4j.nfe400.classes.evento.NFEventoRetorno;
import br.indie.fiscal4j.nfe400.classes.evento.NFInfoEventoRetorno;
import br.indie.fiscal4j.nfe400.classes.nota.consulta.NFNotaConsultaRetorno;
import br.indie.fiscal4j.nfe400.classes.nota.consulta.NFProtocoloEvento;
import io.github.blackfishlabs.common.helper.DateHelper;
import io.github.blackfishlabs.common.helper.FileHelper;
import io.github.blackfishlabs.common.helper.FiscalConstantHelper;
import io.github.blackfishlabs.common.properties.FiscalProperties;
import io.github.blackfishlabs.controller.dto.FiscalEventCCeDTO;
import io.github.blackfishlabs.controller.dto.FiscalStatusDocumentDTO;
import io.github.blackfishlabs.controller.translator.FiscalCCeTranslator;
import io.github.blackfishlabs.controller.translator.FiscalStatusDocumentTranslator;
import io.github.blackfishlabs.service.CCeService;
import io.github.blackfishlabs.service.NFeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

public class CCeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CCeController.class);

    public String sendCCe(FiscalEventCCeDTO dto) throws Exception {
        CCeService service = new CCeService();
        NFeService nFeService = new NFeService();

        FiscalCCeTranslator translator = new FiscalCCeTranslator();

        NFEnviaEventoRetorno cce = service.sendCCe(translator.fromDTO(dto));

        Optional<NFEventoRetorno> event = cce.getEventoRetorno().stream().findFirst();

        if (event.isPresent()) {
            NFInfoEventoRetorno info = event.get().getInfoEventoRetorno();

            checkArgument(info.getCodigoStatus().equals(135),
                    info.getCodigoStatus().toString().concat(" - ").
                            concat(info.getMotivo()));
            LOGGER.info("Carta de Correção emitida pelo protocolo: ".concat(event.get().getInfoEventoRetorno().getNumeroProtocolo()));

            FiscalStatusDocumentTranslator fiscalStatusDocumentTranslator = new FiscalStatusDocumentTranslator();

            FiscalStatusDocumentDTO fiscalStatusDocumentDTO = new FiscalStatusDocumentDTO();
            fiscalStatusDocumentDTO.setEmitter(dto.getEmitter());
            fiscalStatusDocumentDTO.setPassword(dto.getPassword());
            fiscalStatusDocumentDTO.setKey(dto.getKey());

            NFNotaConsultaRetorno status = nFeService.status(fiscalStatusDocumentTranslator.fromDTO(fiscalStatusDocumentDTO));
            Optional<NFProtocoloEvento> proc = status.getProtocoloEvento().stream().findFirst();

            if (proc.isPresent()) {
                final String procEventNFe = proc.get().toString();

                try {
                    String path = FiscalConstantHelper.CCE_PATH;
                    String xmlPath = FiscalProperties.getInstance().getDirXML()
                            .concat(path)
                            .concat(DateHelper.toDirFormat(new Date()))
                            .concat("/")
                            .concat(info.getChave())
                            .concat("-cce.xml");

                    FileHelper.exportXml(procEventNFe, xmlPath);
                    FileHelper.exportFilesPDFOnly(proc.get());
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }

        }

        return translator.response(cce);
    }
}
