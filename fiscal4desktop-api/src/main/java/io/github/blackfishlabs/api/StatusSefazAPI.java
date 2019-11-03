package io.github.blackfishlabs.api;

import br.indie.fiscal4j.DFModelo;
import io.github.blackfishlabs.controller.StatusWebServiceController;
import io.github.blackfishlabs.controller.dto.FiscalStatusWebServiceDTO;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalHelper;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/sefaz")
public class StatusSefazAPI {

    private static final Logger logger = LogManager.getLogger(StatusSefazAPI.class);

    @POST
    @Path("/status")
    @Produces("text/plain")
    @Consumes("application/x-www-form-urlencoded")
    public Response status(@FormParam("uf") String uf,
                           @FormParam("emitente") String emitter,
                           @FormParam("senha") String password) {

        try {
            logger.info("Call status() method");
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
            logger.error(ex.getMessage());
            return Response.status(500).entity("Exception: ".concat(ex.getMessage())).build();
        }
    }
}
