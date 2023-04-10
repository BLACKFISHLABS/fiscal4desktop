package io.github.blackfishlabs.fiscal4desktop.service;

import br.indie.fiscal4j.nfe400.classes.evento.NFEnviaEventoRetorno;
import br.indie.fiscal4j.nfe400.webservices.WSFacade;
import io.github.blackfishlabs.fiscal4desktop.domain.domain.FiscalCCeDomain;

public class CCeService {
    public NFEnviaEventoRetorno sendCCe(FiscalCCeDomain domain) throws Exception {
        return new WSFacade(domain.getConfiguration())
                .corrigeNota(
                        domain.getKey(),
                        domain.getCorrection(),
                        domain.getSequential());
    }
}
