package io.github.blackfishlabs.api;

import br.indie.fiscal4j.DFModelo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.blackfishlabs.controller.NFeController;
import io.github.blackfishlabs.controller.StatusWebServiceController;
import io.github.blackfishlabs.controller.dto.*;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalHelper;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Slf4j
@Path("/nfe")
public class NFeAPI {

    @POST
    @Path("/emitir")
    @Produces("text/plain")
    @Consumes("application/x-www-form-urlencoded")
    public Response sendNFe(@FormParam("nfe") String nfe,
                            @FormParam("emitente") String emitter,
                            @FormParam("senha") String password) {
        try {
            log.info("Call sendNFe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            NFeController controller = new NFeController();
            FiscalSendDTO dto = new FiscalSendDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);

            String json = FiscalHelper.validation(nfe);
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
                return Response.status(200).entity(controller.send(dto)).build();

            return Response.status(500).entity("Não há CONEXÃO com a INTERNET ou há INDISPONIBILIDADE DA SEFAZ.").build();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return Response.status(500).entity("Exception: ".concat(ex.getMessage())).build();
        }
    }

    @POST
    @Path("/cancelar")
    @Produces("text/plain")
    @Consumes("application/x-www-form-urlencoded")
    public Response cancelNFe(@FormParam("emitente") String emitter,
                              @FormParam("senha") String password,
                              @FormParam("chave") String key,
                              @FormParam("justificativa") String justification) {
        try {
            log.info("Call cancelNFe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            NFeController controller = new NFeController();

            FiscalEventCancellationDTO dto = new FiscalEventCancellationDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setKey(key);
            dto.setJustification(justification);

            return Response.status(200).entity(controller.cancel(dto)).build();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return Response.status(500).entity("Exception: ".concat(ex.getMessage())).build();
        }
    }

    @POST
    @Path("/inutilizar")
    @Produces("text/plain")
    @Consumes("application/x-www-form-urlencoded")
    public Response disablementNFe(@FormParam("emitente") String emitter,
                                   @FormParam("senha") String password,
                                   @FormParam("ano") String year,
                                   @FormParam("serie") String series,
                                   @FormParam("inicio") String initialNumber,
                                   @FormParam("final") String finalNumber,
                                   @FormParam("justificativa") String justification) {
        try {
            log.info("Call disablementNFe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            NFeController controller = new NFeController();

            FiscalDisablementDTO dto = new FiscalDisablementDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setYear(year);
            dto.setSeries(series);
            dto.setInitialNumber(initialNumber);
            dto.setFinalNumber(finalNumber);
            dto.setJustification(justification);
            dto.setModel(DFModelo.NFE.getCodigo());

            return Response.status(200).entity(controller.disablement(dto)).build();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return Response.status(500).entity("Exception: ".concat(ex.getMessage())).build();
        }
    }

    @POST
    @Path("/status")
    @Produces("text/plain")
    @Consumes("application/x-www-form-urlencoded")
    public Response status(@FormParam("chave") String key,
                           @FormParam("emitente") String emitter,
                           @FormParam("senha") String password) {

        try {
            log.info("Call status() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            NFeController controller = new NFeController();

            FiscalStatusDocumentDTO dto = new FiscalStatusDocumentDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setKey(key);

            return Response.status(200).entity(controller.status(dto)).build();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return Response.status(500).entity("Exception: ".concat(ex.getMessage())).build();
        }
    }
}
