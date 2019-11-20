package io.github.blackfishlabs.api;

import io.github.blackfishlabs.controller.CCeController;
import io.github.blackfishlabs.controller.dto.FiscalEventCCeDTO;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalHelper;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Slf4j
@Path("/cce")
public class CCeAPI {

    @POST
    @Path("/enviar")
    @Produces("text/plain")
    @Consumes("application/x-www-form-urlencoded")
    public Response sendCCe(@FormParam("emitente") String emitter,
                            @FormParam("senha") String password,
                            @FormParam("chave") String key,
                            @FormParam("texto") String correction) {
        try {
            log.info("Call sendCCe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            CCeController controller = new CCeController();

            FiscalEventCCeDTO dto = new FiscalEventCCeDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setKey(key);
            dto.setCorrection(correction);

            return Response.status(200).entity(controller.sendCCe(dto)).build();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return Response.status(500).entity("Exception: ".concat(ex.getMessage())).build();
        }
    }

}
