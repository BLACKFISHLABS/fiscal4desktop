package io.github.blackfishlabs.fiscal4desktop.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalHelper;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import io.github.blackfishlabs.fiscal4desktop.controller.MDFeController;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mdfe")
public class MDFeAPI {

    @Autowired
    private MDFeController mdFeController;

    private static final Logger LOGGER = LoggerFactory.getLogger(MDFeAPI.class);

    @PostMapping(value = "/emitir", produces = "text/plain;charset=UTF-8", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> sendMDFe(@RequestParam("mdfe") String mdfe,
                                           @RequestParam("emitente") String emitter,
                                           @RequestParam("senha") String password) {
        try {
            LOGGER.info("Call sendMDFe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            FiscalMDFeSendDTO dto = new FiscalMDFeSendDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);

            String json = FiscalHelper.validation(mdfe);
            LOGGER.info("JSON para envio: " + json);

            ObjectMapper mapper = new ObjectMapper();
            FiscalMDFeDocumentDTO fiscalDocumentDTO = mapper.readValue(json, FiscalMDFeDocumentDTO.class);
            dto.setFiscalMDFeDocumentDTO(fiscalDocumentDTO);

            return ResponseEntity.status(200).body(mdFeController.send(dto));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(500).body("Exception: ".concat(ex.getMessage()));
        }
    }

    @PostMapping(value = "/cancelar", produces = "text/plain;charset=UTF-8", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> cancelMDFe(@RequestParam("emitente") String emitter,
                                             @RequestParam("senha") String password,
                                             @RequestParam("chave") String key,
                                             @RequestParam("justificativa") String justification) {
        try {
            LOGGER.info("Call cancelMDFe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            FiscalMDFeCancellationDTO dto = new FiscalMDFeCancellationDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setKey(key);
            dto.setJustification(justification);

            return ResponseEntity.status(200).body(mdFeController.cancel(dto));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(500).body("Exception: ".concat(ex.getMessage()));
        }
    }

    @PostMapping(value = "/naoEncerradas", produces = "text/plain;charset=UTF-8", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> statusNotClosing(@RequestParam("emitente") String emitter,
                                                   @RequestParam("senha") String password) {
        try {
            LOGGER.info("Call statusNotClosing() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            FiscalMDFeStatusNotClosingDTO dto = new FiscalMDFeStatusNotClosingDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setCnpj(emitter);

            return ResponseEntity.status(200).body(mdFeController.statusNotClosing(dto));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(500).body("Exception: ".concat(ex.getMessage()));
        }
    }

    @PostMapping(value = "/encerrar", produces = "text/plain;charset=UTF-8", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> closing(@RequestParam("emitente") String emitter,
                                          @RequestParam("senha") String password,
                                          @RequestParam("chave") String key,
                                          @RequestParam("protocolo") String protocol,
                                          @RequestParam("codigoMunicipio") String code,
                                          @RequestParam("dataEncerramento") String closing,
                                          @RequestParam("UF") String uf) {
        try {
            LOGGER.info("Call closing() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            FiscalMDFeClosingDTO dto = new FiscalMDFeClosingDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setClosing(closing);
            dto.setCode(code);
            dto.setUF(uf);
            dto.setKey(key);
            dto.setNumber(protocol);

            return ResponseEntity.status(200).body(mdFeController.closing(dto));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(500).body("Exception: ".concat(ex.getMessage()));
        }
    }
}
