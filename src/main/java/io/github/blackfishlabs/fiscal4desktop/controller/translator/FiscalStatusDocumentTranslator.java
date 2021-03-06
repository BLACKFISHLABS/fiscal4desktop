package io.github.blackfishlabs.fiscal4desktop.controller.translator;

import br.indie.fiscal4j.nfe400.classes.nota.consulta.NFNotaConsultaRetorno;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.FiscalStatusDocumentDTO;
import io.github.blackfishlabs.fiscal4desktop.domain.domain.FiscalStatusDomain;
import io.github.blackfishlabs.fiscal4desktop.infra.NFeConfiguration;

public class FiscalStatusDocumentTranslator implements Translator<FiscalStatusDocumentDTO, FiscalStatusDomain, NFNotaConsultaRetorno> {

    @Override
    public FiscalStatusDomain fromDTO(FiscalStatusDocumentDTO dto) {
        FiscalStatusDomain domain = new FiscalStatusDomain();
        domain.setConfiguration(new NFeConfiguration(dto.getEmitter(), dto.getPassword()));
        domain.setKey(dto.getKey());

        return domain;
    }

    @Override
    public String response(NFNotaConsultaRetorno result) {
        return "Ambiente: " + result.getAmbiente() +
                "\n" +
                "Chave: " + result.getChave() +
                "\n" +
                "Status: " + result.getMotivo();
    }
}
