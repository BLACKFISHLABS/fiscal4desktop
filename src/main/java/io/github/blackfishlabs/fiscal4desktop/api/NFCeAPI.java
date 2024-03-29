package io.github.blackfishlabs.fiscal4desktop.api;

import br.indie.fiscal4j.DFModelo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalHelper;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import io.github.blackfishlabs.fiscal4desktop.controller.NFCeController;
import io.github.blackfishlabs.fiscal4desktop.controller.StatusWebServiceController;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/nfce")
public class NFCeAPI {

    @Autowired
    private NFCeController nfCeController;

    @PostMapping(value = "/emitir", produces = "text/plain;charset=UTF-8", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> sendNFCe(@RequestParam("nfce") String nfce,
                                           @RequestParam("emitente") String emitter,
                                           @RequestParam("senha") String password) {

        try {
            log.info("Call sendNFCe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            FiscalSendDTO dto = new FiscalSendDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);

            String json = FiscalHelper.validationData(nfce);
            log.info("JSON para envio: " + json);

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
                log.info("Não há CONEXÃO com a INTERNET ou há INDISPONIBILIDADE DA SEFAZ. Entrada em contingência.");

                dto.setContingency(true);
                dto.getFiscalDocumentDTO().getIde().setTpEmis("9");
                dto.getFiscalDocumentDTO().getIde().setDhCont(new DateTime(DateTimeZone.UTC).toString("yyyy-MM-dd'T'HH:mm:ss"));
                dto.getFiscalDocumentDTO().getIde().setXJust("Contingência off-line da NFC-e");
            }

            return ResponseEntity.status(200).body(nfCeController.send(dto));
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.status(500).body("Exception: ".concat(ex.getMessage()));
        }
    }

    @PostMapping(value = "/cancelar", produces = "text/plain;charset=UTF-8", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> cancelNFCe(@RequestParam("emitente") String emitter,
                                             @RequestParam("senha") String password,
                                             @RequestParam("chave") String key,
                                             @RequestParam("justificativa") String justification) {
        try {
            log.info("Call cancelNFCe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            FiscalEventCancellationDTO dto = new FiscalEventCancellationDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setKey(key);
            dto.setJustification(justification);

            return ResponseEntity.status(200).body(nfCeController.cancel(dto));
        } catch (Exception ex) {
            log.error(ex.getMessage());
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
            log.info("Call disablementNFCe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            FiscalDisablementDTO dto = new FiscalDisablementDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setYear(year);
            dto.setSeries(series);
            dto.setInitialNumber(initialNumber);
            dto.setFinalNumber(finalNumber);
            dto.setJustification(justification);
            dto.setModel(DFModelo.NFCE.getCodigo());

            return ResponseEntity.status(200).body(nfCeController.disablement(dto));
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.status(500).body("Exception: ".concat(ex.getMessage()));
        }
    }

    @GetMapping(value = "/contingency")
    public ResponseEntity<String> contingency() {
        try {
            nfCeController.contingency();
            return ResponseEntity.status(200).body("Contingência Executada");
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.status(500).body("Exception: ".concat(ex.getMessage()));
        }
    }
}
