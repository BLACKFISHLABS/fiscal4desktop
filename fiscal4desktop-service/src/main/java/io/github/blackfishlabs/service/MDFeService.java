package io.github.blackfishlabs.service;

import br.indie.fiscal4j.mdfe3.classes.consultaRecibo.MDFeConsultaReciboRetorno;
import br.indie.fiscal4j.mdfe3.classes.consultanaoencerrados.MDFeConsultaNaoEncerradosRetorno;
import br.indie.fiscal4j.mdfe3.classes.lote.envio.MDFEnvioLote;
import br.indie.fiscal4j.mdfe3.classes.lote.envio.MDFEnvioLoteRetornoDados;
import br.indie.fiscal4j.mdfe3.classes.nota.consulta.MDFeNotaConsultaRetorno;
import br.indie.fiscal4j.mdfe3.classes.nota.evento.MDFeRetorno;
import br.indie.fiscal4j.mdfe3.webservices.WSFacade;
import io.github.blackfishlabs.domain.domain.FiscalMDFeCancelDomain;
import io.github.blackfishlabs.domain.domain.FiscalMDFeClosingDomain;
import io.github.blackfishlabs.domain.domain.FiscalMDFeStatusDomain;
import io.github.blackfishlabs.domain.domain.FiscalMDFeStatusNotClosingDomain;
import io.github.blackfishlabs.fiscal4desktop.common.helper.DateHelper;
import io.github.blackfishlabs.fiscal4desktop.infra.MDFeConfiguration;

public class MDFeService {

    public MDFEnvioLoteRetornoDados send(final MDFeConfiguration configuration, final MDFEnvioLote domain) throws Exception {
        return new WSFacade(configuration).envioRecepcaoLote(domain);
    }

    public MDFeRetorno cancel(final FiscalMDFeCancelDomain domain) throws Exception {
        return new WSFacade(domain.getConfiguration())
                .cancelaMdfe(
                        domain.getKey(),
                        domain.getProtocol(),
                        domain.getJustification());
    }

    public MDFeConsultaReciboRetorno status(final FiscalMDFeStatusDomain domain) throws Exception {
        return new WSFacade(domain.getConfiguration()).consultaRecibo(domain.getKey());
    }

    public MDFeConsultaNaoEncerradosRetorno statusNotClosing(final FiscalMDFeStatusNotClosingDomain domain) throws Exception {
        return new WSFacade(domain.getConfiguration()).consultaNaoEncerrados(domain.getCnpj());
    }

    public MDFeRetorno closing(final FiscalMDFeClosingDomain domain) throws Exception {
        return new WSFacade(domain.getConfiguration())
                .encerramento(
                        domain.getKey(),
                        domain.getNumber(),
                        domain.getCode(),
                        DateHelper.toLocalDate(domain.getClosing()),
                        domain.getUF());
    }

    public MDFeNotaConsultaRetorno statusMDFe(final FiscalMDFeStatusDomain domain) throws Exception {
        return new WSFacade(domain.getConfiguration()).consultaMdfe(domain.getKey());
    }
}
