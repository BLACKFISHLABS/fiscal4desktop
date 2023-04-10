package io.github.blackfishlabs.fiscal4desktop.controller;

import br.indie.fiscal4j.nfe400.classes.statusservico.consulta.NFStatusServicoConsultaRetorno;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.FiscalStatusWebServiceDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.translator.FiscalStatusWSTranslator;
import io.github.blackfishlabs.fiscal4desktop.service.StatusWSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.net.URLConnection;

@Slf4j
@Service
public class StatusWebServiceController {

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
            log.error("Houve um erro na conexão: ".concat(e.getMessage()));
            return false;
        }

        return statusWebService.getStatus().equals("107");
    }
}
