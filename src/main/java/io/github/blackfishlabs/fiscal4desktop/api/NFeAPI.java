package io.github.blackfishlabs.fiscal4desktop.api;

import br.indie.fiscal4j.DFModelo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalHelper;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import io.github.blackfishlabs.fiscal4desktop.controller.NFeController;
import io.github.blackfishlabs.fiscal4desktop.controller.StatusWebServiceController;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/nfe")
public class NFeAPI {

    @Autowired
    private NFeController nFeController;

    @PostMapping(value = "/emitir", produces = "text/plain;charset=UTF-8", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> sendNFe(@RequestParam("nfe") String nfe,
                                          @RequestParam("emitente") String emitter,
                                          @RequestParam("senha") String password) {
        try {
            log.info("Call sendNFe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            FiscalSendDTO dto = new FiscalSendDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);

            String json = FiscalHelper.validationData(nfe);
            log.info("JSON para envio: " + json);

            ObjectMapper mapper = new ObjectMapper();
            FiscalDocumentDTO fiscalDocumentDTO = mapper.readValue(json, FiscalDocumentDTO.class);
            dto.setFiscalDocumentDTO(fiscalDocumentDTO);

            StatusWebServiceController statusController = new StatusWebServiceController();
            FiscalStatusWebServiceDTO status = new FiscalStatusWebServiceDTO();
            status.setEmitter(emitter);
            status.setPassword(password);
            status.setUf(FiscalProperties.getInstance().getUF());
            status.setModel(DFModelo.NFE.getCodigo());

            if (statusController.getStatusWebServiceCode(status))
                return ResponseEntity.status(200).body(nFeController.send(dto));

            return ResponseEntity.status(500).body("Não há CONEXÃO com a INTERNET ou há INDISPONIBILIDADE DA SEFAZ.");
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.status(500).body("Exception: ".concat(ex.getMessage()));
        }
    }

    @PostMapping(value = "/cancelar", produces = "text/plain;charset=UTF-8", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> cancelNFe(@RequestParam("emitente") String emitter,
                                            @RequestParam("senha") String password,
                                            @RequestParam("chave") String key,
                                            @RequestParam("justificativa") String justification) {
        try {
            log.info("Call cancelNFe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            FiscalEventCancellationDTO dto = new FiscalEventCancellationDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setKey(key);
            dto.setJustification(justification);

            return ResponseEntity.status(200).body(nFeController.cancel(dto));
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
            log.info("Call disablementNFe() method");
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
            dto.setModel(DFModelo.NFE.getCodigo());

            return ResponseEntity.status(200).body(nFeController.disablement(dto));
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.status(500).body("Exception: ".concat(ex.getMessage()));
        }
    }

    @PostMapping(value = "/status", produces = "text/plain;charset=UTF-8", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> status(@RequestParam("chave") String key,
                                         @RequestParam("emitente") String emitter,
                                         @RequestParam("senha") String password) {

        try {
            log.info("Call status() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            FiscalStatusDocumentDTO dto = new FiscalStatusDocumentDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setKey(key);

            return ResponseEntity.status(200).body(nFeController.status(dto));
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.status(500).body("Exception: ".concat(ex.getMessage()));
        }
    }
}
