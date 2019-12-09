package io.github.blackfishlabs.api;

import br.indie.fiscal4j.DFModelo;
import io.github.blackfishlabs.common.helper.FiscalHelper;
import io.github.blackfishlabs.common.properties.FiscalProperties;
import io.github.blackfishlabs.controller.StatusWebServiceController;
import io.github.blackfishlabs.controller.dto.FiscalStatusWebServiceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sefaz")
public class StatusSefazAPI {

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

            StatusWebServiceController controller = new StatusWebServiceController();

            FiscalStatusWebServiceDTO dto = new FiscalStatusWebServiceDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setUf(uf);
            dto.setModel(DFModelo.NFE.getCodigo());

            return ResponseEntity.status(200).body(controller.getStatusWebService(dto));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(500).body("Exception: ".concat(ex.getMessage()));
        }
    }
}
