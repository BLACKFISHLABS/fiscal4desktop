package io.github.blackfishlabs.api;

import io.github.blackfishlabs.common.helper.FiscalHelper;
import io.github.blackfishlabs.common.properties.FiscalProperties;
import io.github.blackfishlabs.controller.CCeController;
import io.github.blackfishlabs.controller.dto.FiscalEventCCeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cce")
public class CCeAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CCeAPI.class);

    @PostMapping(value = "/enviar", produces = "text/plain;charset=UTF-8", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> sendCCe(@RequestParam("emitente") String emitter,
                                          @RequestParam("senha") String password,
                                          @RequestParam("chave") String key,
                                          @RequestParam("texto") String correction) {
        try {
            LOGGER.info("Call sendCCe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            CCeController controller = new CCeController();

            FiscalEventCCeDTO dto = new FiscalEventCCeDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setKey(key);
            dto.setCorrection(correction);

            return ResponseEntity.status(200).body(controller.sendCCe(dto));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(500).body("Exception: ".concat(ex.getMessage()));
        }
    }

}
