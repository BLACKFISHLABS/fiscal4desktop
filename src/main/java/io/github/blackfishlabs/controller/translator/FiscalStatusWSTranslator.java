package io.github.blackfishlabs.controller.translator;

import br.indie.fiscal4j.DFModelo;
import br.indie.fiscal4j.DFUnidadeFederativa;
import br.indie.fiscal4j.nfe400.classes.statusservico.consulta.NFStatusServicoConsultaRetorno;
import io.github.blackfishlabs.controller.dto.FiscalStatusWebServiceDTO;
import io.github.blackfishlabs.domain.domain.FiscalStatusWebServiceDomain;
import io.github.blackfishlabs.infra.NFeConfiguration;

public class FiscalStatusWSTranslator implements Translator<FiscalStatusWebServiceDTO, FiscalStatusWebServiceDomain, NFStatusServicoConsultaRetorno> {

    @Override
    public FiscalStatusWebServiceDomain fromDTO(FiscalStatusWebServiceDTO dto) {
        FiscalStatusWebServiceDomain domain = new FiscalStatusWebServiceDomain();
        domain.setConfiguration(new NFeConfiguration(dto.getEmitter(), dto.getPassword()));
        domain.setUf(DFUnidadeFederativa.valueOfCodigo(dto.getUf()));
        domain.setModel(DFModelo.valueOfCodigo(dto.getModel()));
        return domain;
    }

    @Override
    public String response(NFStatusServicoConsultaRetorno result) {
        return "UF: " + result.getUf() +
                "\n" +
                "Versão: " + result.getVersao() +
                "\n" +
                "Ambiente: " + result.getAmbiente() +
                "\n" +
                result.getStatus() + " - " + result.getMotivo() +
                "\n" +
                "Data: " + result.getDataRecebimento() +
                "\n";
    }
}
