package io.github.blackfishlabs.fiscal4desktop.api;

import br.indie.fiscal4j.DFModelo;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalHelper;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import io.github.blackfishlabs.fiscal4desktop.controller.StatusWebServiceController;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.FiscalStatusWebServiceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sefaz")
public class StatusSefazAPI {

    @Autowired
    private StatusWebServiceController statusWebServiceController;

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusSefazAPI.class);

    @PostMapping(value = "/status", produces = "text/plain;charset=UTF-8", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> status(@RequestParam("uf") String uf,
                                         @RequestParam("emitente") String emitter,
                                         @RequestParam("senha") String password) {

        try {
            LOGGER.info("Call status() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            FiscalStatusWebServiceDTO dto = new FiscalStatusWebServiceDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setUf(uf);
            dto.setModel(DFModelo.NFE.getCodigo());

            return ResponseEntity.status(200).body(statusWebServiceController.getStatusWebService(dto));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(500).body("Exception: ".concat(ex.getMessage()));
        }
    }
}
