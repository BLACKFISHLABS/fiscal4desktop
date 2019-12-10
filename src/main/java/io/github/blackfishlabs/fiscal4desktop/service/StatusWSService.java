package io.github.blackfishlabs.fiscal4desktop.service;

import br.indie.fiscal4j.nfe400.classes.statusservico.consulta.NFStatusServicoConsultaRetorno;
import br.indie.fiscal4j.nfe400.webservices.WSFacade;
import io.github.blackfishlabs.fiscal4desktop.domain.domain.FiscalStatusWebServiceDomain;

public class StatusWSService {

    public NFStatusServicoConsultaRetorno getStatusWebService(FiscalStatusWebServiceDomain domain) throws Exception {
        return new WSFacade(domain.getConfiguration()).consultaStatus(domain.getUf(), domain.getModel());
    }
}
