package io.github.blackfishlabs.controller.translator;

import br.indie.fiscal4j.nfe400.classes.nota.consulta.NFNotaConsultaRetorno;
import io.github.blackfishlabs.controller.dto.FiscalStatusDocumentDTO;
import io.github.blackfishlabs.domain.domain.FiscalStatusDomain;
import io.github.blackfishlabs.infra.NFeConfiguration;

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
