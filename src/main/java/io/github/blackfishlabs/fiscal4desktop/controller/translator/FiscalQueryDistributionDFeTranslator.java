package io.github.blackfishlabs.fiscal4desktop.controller.translator;

import br.indie.fiscal4j.DFUnidadeFederativa;
import br.indie.fiscal4j.nfe.classes.distribuicao.NFDistribuicaoIntRetorno;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.FiscalQueryDistributionDFeDTO;
import io.github.blackfishlabs.fiscal4desktop.domain.domain.FiscalQueryDistributionDFeDomain;
import io.github.blackfishlabs.fiscal4desktop.infra.NFeConfiguration;

public class FiscalQueryDistributionDFeTranslator implements Translator<FiscalQueryDistributionDFeDTO, FiscalQueryDistributionDFeDomain, NFDistribuicaoIntRetorno> {

    @Override
    public FiscalQueryDistributionDFeDomain fromDTO(FiscalQueryDistributionDFeDTO dto) {
        FiscalQueryDistributionDFeDomain domain = new FiscalQueryDistributionDFeDomain();
        domain.setConfiguration(new NFeConfiguration(dto.getEmitter(), dto.getPassword()));
        domain.setKey(dto.getKey());
        domain.setDocument(dto.getDocument());
        domain.setUf(DFUnidadeFederativa.valueOfCodigo(dto.getUf()));
        domain.setLastNSU(dto.getLastNSU());
        domain.setNsu(dto.getNsu());

        return domain;
    }

    @Override
    public String response(NFDistribuicaoIntRetorno result) throws Exception {
        return result.toString();
    }
}
