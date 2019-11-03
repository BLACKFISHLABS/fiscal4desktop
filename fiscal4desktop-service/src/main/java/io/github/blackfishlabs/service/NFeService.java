package io.github.blackfishlabs.service;

import br.indie.fiscal4j.DFModelo;
import br.indie.fiscal4j.nfe.classes.distribuicao.NFDistribuicaoIntRetorno;
import br.indie.fiscal4j.nfe400.classes.evento.NFEnviaEventoRetorno;
import br.indie.fiscal4j.nfe400.classes.evento.inutilizacao.NFRetornoEventoInutilizacao;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvio;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvioRetorno;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvioRetornoDados;
import br.indie.fiscal4j.nfe400.classes.nota.consulta.NFNotaConsultaRetorno;
import br.indie.fiscal4j.nfe400.webservices.WSFacade;
import io.github.blackfishlabs.domain.domain.FiscalCancelDomain;
import io.github.blackfishlabs.domain.domain.FiscalDisablementDomain;
import io.github.blackfishlabs.domain.domain.FiscalQueryDistributionDFeDomain;
import io.github.blackfishlabs.domain.domain.FiscalStatusDomain;
import io.github.blackfishlabs.fiscal4desktop.infra.NFeConfiguration;

public class NFeService {

    public NFLoteEnvioRetornoDados send(final NFeConfiguration configuration, NFLoteEnvio domain) throws Exception {
        return new WSFacade(configuration).enviaLote(domain);
    }

    public String contingency(final NFeConfiguration configuration, NFLoteEnvio domain) throws Exception {
        return new WSFacade(configuration).getLoteAssinado(domain).toString();
    }

    public NFEnviaEventoRetorno cancel(final FiscalCancelDomain domain) throws Exception {
        return new WSFacade(domain.getConfiguration())
                .cancelaNota(
                        domain.getKey(),
                        domain.getProtocol(),
                        domain.getJustification());
    }

    public NFRetornoEventoInutilizacao disablement(final FiscalDisablementDomain domain) throws Exception {
        return new WSFacade(domain.getConfiguration())
                .inutilizaNota(
                        domain.getYear(),
                        domain.getEmitter(),
                        domain.getSeries(),
                        domain.getInitialNumber(),
                        domain.getFinalNumber(),
                        domain.getJustification(),
                        domain.getModel());
    }

    public NFNotaConsultaRetorno status(final FiscalStatusDomain domain) throws Exception {
        return new WSFacade(domain.getConfiguration()).consultaNota(domain.getKey());
    }

    public NFLoteEnvioRetorno send(final NFeConfiguration configuration, final String xml) throws Exception {
        return new WSFacade(configuration).enviaLoteAssinado(xml, DFModelo.NFCE);
    }

    public NFDistribuicaoIntRetorno queryDFe(final FiscalQueryDistributionDFeDomain domain) throws Exception {
        return new WSFacade(domain.getConfiguration())
                .consultarDistribuicaoDFe(
                        domain.getDocument(),
                        domain.getUf(),
                        domain.getKey(),
                        domain.getNsu(),
                        domain.getLastNSU());
    }

}
