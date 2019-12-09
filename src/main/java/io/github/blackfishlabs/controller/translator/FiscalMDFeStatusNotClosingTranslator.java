package io.github.blackfishlabs.controller.translator;

import br.indie.fiscal4j.mdfe3.classes.consultanaoencerrados.MDFeConsultaNaoEncerradosRetorno;
import io.github.blackfishlabs.controller.dto.FiscalMDFeStatusNotClosingDTO;
import io.github.blackfishlabs.domain.domain.FiscalMDFeStatusNotClosingDomain;
import io.github.blackfishlabs.infra.MDFeConfiguration;

public class FiscalMDFeStatusNotClosingTranslator implements Translator<FiscalMDFeStatusNotClosingDTO, FiscalMDFeStatusNotClosingDomain, MDFeConsultaNaoEncerradosRetorno> {

    @Override
    public FiscalMDFeStatusNotClosingDomain fromDTO(FiscalMDFeStatusNotClosingDTO dto) {
        FiscalMDFeStatusNotClosingDomain domain = new FiscalMDFeStatusNotClosingDomain();

        domain.setConfiguration(new MDFeConfiguration(dto.getEmitter(), dto.getPassword()));
        domain.setCnpj(dto.getCnpj());

        return domain;
    }

    @Override
    public String response(MDFeConsultaNaoEncerradosRetorno result) {
        StringBuilder sb = new StringBuilder();

        sb.append("Ambiente: ").append(result.getAmbiente());
        sb.append("\n");
        sb.append("UF: ").append(result.getUf());
        sb.append("\n");
        sb.append("Manifestos não encerrados:");
        sb.append("\n");
        result.getInfMDFe().forEach(s -> {
            sb.append("Chave: ").append(s.getChave());
            sb.append(" - ");
            sb.append("Protocolo: ").append(s.getNumeroProtocolo());
            sb.append("\n");
        });

        return sb.toString();
    }
}
