package io.github.blackfishlabs.api;

import br.indie.fiscal4j.DFModelo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.blackfishlabs.controller.NFCeController;
import io.github.blackfishlabs.controller.StatusWebServiceController;
import io.github.blackfishlabs.controller.dto.*;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalHelper;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Slf4j
@Path("/nfce")
public class NFCeAPI {

    @POST
    @Path("/emitir")
    @Produces("text/plain")
    @Consumes("application/x-www-form-urlencoded")
    public Response sendNFCe(@FormParam("nfce") String nfce,
                             @FormParam("emitente") String emitter,
                             @FormParam("senha") String password) {

        try {
            log.info("Call sendNFCe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            NFCeController controller = new NFCeController();

            FiscalSendDTO dto = new FiscalSendDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);

            String json = FiscalHelper.validation(nfce);
            log.info("JSON para envio: " + json);

            ObjectMapper mapper = new ObjectMapper();
            FiscalDocumentDTO fiscalDocumentDTO = mapper.readValue(json, FiscalDocumentDTO.class);
            dto.setFiscalDocumentDTO(fiscalDocumentDTO);

            StatusWebServiceController statusController = new StatusWebServiceController();
            FiscalStatusWebServiceDTO status = new FiscalStatusWebServiceDTO();
            status.setEmitter(emitter);
            status.setPassword(password);
            status.setUf(FiscalProperties.getInstance().getUF());
            status.setModel(DFModelo.NFCE.getCodigo());

            if (!statusController.getStatusWebServiceCode(status)) {
                log.info("Não há CONEXÃO com a INTERNET ou há INDISPONIBILIDADE DA SEFAZ. Entrada em contingência.");

                dto.setContingency(true);
                dto.getFiscalDocumentDTO().getIde().setTpEmis("9");
                dto.getFiscalDocumentDTO().getIde().setDhCont(new DateTime(DateTimeZone.UTC).toString("yyyy-MM-dd'T'HH:mm:ss"));
                dto.getFiscalDocumentDTO().getIde().setXJust("Contingência off-line da NFC-e");
            }

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
    public Response cancelNFCe(@FormParam("emitente") String emitter,
                               @FormParam("senha") String password,
                               @FormParam("chave") String key,
                               @FormParam("justificativa") String justification) {
        try {
            log.info("Call cancelNFCe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            NFCeController controller = new NFCeController();

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
            log.info("Call disablementNFCe() method");
            FiscalHelper.validateCertificateBeforeUse(
                    FiscalProperties.getInstance().getDirCertificate().concat(emitter).concat(".pfx"),
                    password);

            NFCeController controller = new NFCeController();

            FiscalDisablementDTO dto = new FiscalDisablementDTO();
            dto.setEmitter(emitter);
            dto.setPassword(password);
            dto.setYear(year);
            dto.setSeries(series);
            dto.setInitialNumber(initialNumber);
            dto.setFinalNumber(finalNumber);
            dto.setJustification(justification);
            dto.setModel(DFModelo.NFCE.getCodigo());

            return Response.status(200).entity(controller.disablement(dto)).build();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return Response.status(500).entity("Exception: ".concat(ex.getMessage())).build();
        }
    }
}
