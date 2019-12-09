package io.github.blackfishlabs.controller;

import br.indie.fiscal4j.nfe400.classes.statusservico.consulta.NFStatusServicoConsultaRetorno;
import io.github.blackfishlabs.controller.dto.FiscalStatusWebServiceDTO;
import io.github.blackfishlabs.controller.translator.FiscalStatusWSTranslator;
import io.github.blackfishlabs.service.StatusWSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLConnection;

public class StatusWebServiceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusWebServiceController.class);

    private static final String GOOGLE = "http://www.google.com.br";

    public String getStatusWebService(FiscalStatusWebServiceDTO dto) throws Exception {
        StatusWSService statusWSService = new StatusWSService();
        FiscalStatusWSTranslator translator = new FiscalStatusWSTranslator();

        NFStatusServicoConsultaRetorno statusWebService = statusWSService.getStatusWebService(translator.fromDTO(dto));

        return translator.response(statusWebService);
    }

    public boolean getStatusWebServiceCode(FiscalStatusWebServiceDTO dto) {
        StatusWSService statusWSService = new StatusWSService();
        FiscalStatusWSTranslator translator = new FiscalStatusWSTranslator();

        NFStatusServicoConsultaRetorno statusWebService;
        try {
            URLConnection connection = new URL(GOOGLE).openConnection();
            connection.connect();

            statusWebService = statusWSService.getStatusWebService(translator.fromDTO(dto));
        } catch (Exception e) {
            LOGGER.error("Houve um erro na conexão: ".concat(e.getMessage()));
            return false;
        }

        return statusWebService.getStatus().equals("107");
    }
}
