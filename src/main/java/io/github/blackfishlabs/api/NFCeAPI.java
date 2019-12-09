package io.github.blackfishlabs.api;

import br.indie.fiscal4j.DFModelo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.blackfishlabs.common.helper.FiscalHelper;
import io.github.blackfishlabs.common.properties.FiscalProperties;
import io.github.blackfishlabs.controller.NFCeController;
import io.github.blackfishlabs.controller.StatusWebServiceController;
import io.github.blackfishlabs.controller.dto.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/nfce")
public class NFCeAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(NFCeAPI.class);

    @PostMapping(value = "/emitir", produces = "text/plain;charset=UTF-8", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> sendNFCe(@RequestParam("nfce") String nfce,
                                           @RequestParam("emitente") String emitter,
                                           @RequestParam("senha") String password) {

        try {
            LOGGER.info("Call sendNFCe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            NFCeController controller = new NFCeController();

            FiscalSendDTO dto = new FiscalSendDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);

            String json = FiscalHelper.validation(nfce);
            LOGGER.info("JSON para envio: " + json);

            ObjectMapper mapper = new ObjectMapper();
            FiscalDocumentDTO fiscalDocumentDTO = mapper.readValue(json, FiscalDocumentDTO.class);
            dto.setFiscalDocumentDTO(fiscalDocumentDTO);

            StatusWebServiceController statusController = new StatusWebServiceController();
            FiscalStatusWebServiceDTO status = new FiscalStatusWebServiceDTO();
            status.setEmitter(emitter);
            status.setPassword(password);
            status.setUf(FiscalProperties.getInstance().getUF());
            status.setModel(DFModelo.NFCE.getCodigo());

            if (!statusController.getStatusWebServiceCode(status)) {
                LOGGER.info("Não há CONEXÃO com a INTERNET ou há INDISPONIBILIDADE DA SEFAZ. Entrada em contingência.");

                dto.setContingency(true);
                dto.getFiscalDocumentDTO().getIde().setTpEmis("9");
                dto.getFiscalDocumentDTO().getIde().setDhCont(new DateTime(DateTimeZone.UTC).toString("yyyy-MM-dd'T'HH:mm:ss"));
                dto.getFiscalDocumentDTO().getIde().setXJust("Contingência off-line da NFC-e");
            }

            return ResponseEntity.status(200).body(controller.send(dto));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(500).body("Exception: ".concat(ex.getMessage()));
        }
    }

    @PostMapping(value = "/cancelar", produces = "text/plain;charset=UTF-8", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> cancelNFCe(@RequestParam("emitente") String emitter,
                                             @RequestParam("senha") String password,
                                             @RequestParam("chave") String key,
                                             @RequestParam("justificativa") String justification) {
        try {
            LOGGER.info("Call cancelNFCe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            NFCeController controller = new NFCeController();

            FiscalEventCancellationDTO dto = new FiscalEventCancellationDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setKey(key);
            dto.setJustification(justification);

            return ResponseEntity.status(200).body(controller.cancel(dto));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(500).body("Exception: ".concat(ex.getMessage()));
        }
    }

    @PostMapping(value = "/inutilizar", produces = "text/plain;charset=UTF-8", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> disablementNFe(@RequestParam("emitente") String emitter,
                                                 @RequestParam("senha") String password,
                                                 @RequestParam("ano") String year,
                                                 @RequestParam("serie") String series,
                                                 @RequestParam("inicio") String initialNumber,
                                                 @RequestParam("final") String finalNumber,
                                                 @RequestParam("justificativa") String justification) {

        try {
            LOGGER.info("Call disablementNFCe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            NFCeController controller = new NFCeController();

            FiscalDisablementDTO dto = new FiscalDisablementDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setYear(year);
            dto.setSeries(series);
            dto.setInitialNumber(initialNumber);
            dto.setFinalNumber(finalNumber);
            dto.setJustification(justification);
            dto.setModel(DFModelo.NFCE.getCodigo());

            return ResponseEntity.status(200).body(controller.disablement(dto));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(500).body("Exception: ".concat(ex.getMessage()));
        }
    }
}
