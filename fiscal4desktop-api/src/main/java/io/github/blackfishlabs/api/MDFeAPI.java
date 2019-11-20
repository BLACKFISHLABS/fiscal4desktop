package io.github.blackfishlabs.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.blackfishlabs.controller.MDFeController;
import io.github.blackfishlabs.controller.dto.*;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalHelper;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Slf4j
@Path("/mdfe")
public class MDFeAPI {

    @POST
    @Path("/emitir")
    @Produces("text/plain")
    @Consumes("application/x-www-form-urlencoded")
    public Response sendMDFe(@FormParam("mdfe") String mdfe,
                             @FormParam("emitente") String emitter,
                             @FormParam("senha") String password) {
        try {
            log.info("Call sendMDFe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            MDFeController controller = new MDFeController();
            FiscalMDFeSendDTO dto = new FiscalMDFeSendDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);

            String json = FiscalHelper.validation(mdfe);
            log.info("JSON para envio: " + json);

            ObjectMapper mapper = new ObjectMapper();
            FiscalMDFeDocumentDTO fiscalDocumentDTO = mapper.readValue(json, FiscalMDFeDocumentDTO.class);
            dto.setFiscalMDFeDocumentDTO(fiscalDocumentDTO);

            return Response.status(200).entity(controller.send(dto)).build();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return Response.status(500).entity("Exception: ".concat(ex.getMessage())).build();
        }
    }

    @POST
    @Path("/cancelar")
    @Produces("text/plain")
    @Consumes("application/x-www-form-urlencoded")
    public Response cancelMDFe(@FormParam("emitente") String emitter,
                               @FormParam("senha") String password,
                               @FormParam("chave") String key,
                               @FormParam("justificativa") String justification) {
        try {
            log.info("Call cancelMDFe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            MDFeController controller = new MDFeController();
            FiscalMDFeCancellationDTO dto = new FiscalMDFeCancellationDTO();
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
    @Path("/naoEncerradas")
    @Produces("text/plain")
    @Consumes("application/x-www-form-urlencoded")
    public Response statusNotClosing(@FormParam("emitente") String emitter,
                                     @FormParam("senha") String password) {
        try {
            log.info("Call statusNotClosing() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            MDFeController controller = new MDFeController();
            FiscalMDFeStatusNotClosingDTO dto = new FiscalMDFeStatusNotClosingDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setCnpj(emitter);

            return Response.status(200).entity(controller.statusNotClosing(dto)).build();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return Response.status(500).entity("Exception: ".concat(ex.getMessage())).build();
        }
    }

    @POST
    @Path("/encerrar")
    @Produces("text/plain")
    @Consumes("application/x-www-form-urlencoded")
    public Response closing(@FormParam("emitente") String emitter,
                            @FormParam("senha") String password,
                            @FormParam("chave") String key,
                            @FormParam("protocolo") String protocol,
                            @FormParam("codigoMunicipio") String code,
                            @FormParam("dataEncerramento") String closing,
                            @FormParam("UF") String uf) {
        try {
            log.info("Call closing() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            MDFeController controller = new MDFeController();
            FiscalMDFeClosingDTO dto = new FiscalMDFeClosingDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setClosing(closing);
            dto.setCode(code);
            dto.setUF(uf);
            dto.setKey(key);
            dto.setNumber(protocol);

            return Response.status(200).entity(controller.closing(dto)).build();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return Response.status(500).entity("Exception: ".concat(ex.getMessage())).build();
        }
    }
}
