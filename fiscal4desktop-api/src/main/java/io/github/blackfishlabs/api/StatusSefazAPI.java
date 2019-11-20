package io.github.blackfishlabs.api;

import br.indie.fiscal4j.DFModelo;
import io.github.blackfishlabs.controller.StatusWebServiceController;
import io.github.blackfishlabs.controller.dto.FiscalStatusWebServiceDTO;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalHelper;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Slf4j
@Path("/sefaz")
public class StatusSefazAPI {

    @POST
    @Path("/status")
    @Produces("text/plain")
    @Consumes("application/x-www-form-urlencoded")
    public Response status(@FormParam("uf") String uf,
                           @FormParam("emitente") String emitter,
                           @FormParam("senha") String password) {

        try {
            log.info("Call status() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            StatusWebServiceController controller = new StatusWebServiceController();

            FiscalStatusWebServiceDTO dto = new FiscalStatusWebServiceDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setUf(uf);
            dto.setModel(DFModelo.NFE.getCodigo());

            return Response.status(200).entity(controller.getStatusWebService(dto)).build();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return Response.status(500).entity("Exception: ".concat(ex.getMessage())).build();
        }
    }
}
