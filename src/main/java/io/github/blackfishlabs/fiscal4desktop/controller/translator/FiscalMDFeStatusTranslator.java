package io.github.blackfishlabs.fiscal4desktop.controller.translator;

import br.indie.fiscal4j.mdfe3.classes.consultaRecibo.MDFeConsultaReciboRetorno;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.FiscalMDFeSendDTO;
import io.github.blackfishlabs.fiscal4desktop.domain.domain.FiscalMDFeStatusDomain;
import io.github.blackfishlabs.fiscal4desktop.infra.MDFeConfiguration;

public class FiscalMDFeStatusTranslator implements Translator<FiscalMDFeSendDTO, FiscalMDFeStatusDomain, MDFeConsultaReciboRetorno> {

    @Override
    public FiscalMDFeStatusDomain fromDTO(FiscalMDFeSendDTO dto) {
        FiscalMDFeStatusDomain domain = new FiscalMDFeStatusDomain();

        domain.setConfiguration(new MDFeConfiguration(dto.getEmitter(), dto.getPassword()));
        domain.setKey(dto.getKey());

        return domain;
    }

    @Override
    public String response(MDFeConsultaReciboRetorno result) throws Exception {
        return "Ambiente: " + result.getAmbiente() +
                "\n" +
                "UF: " + result.getUf() +
                "\n" +
                "Data Recebimento: " + result.getMdfProtocolo().getProtocoloInfo().getDataRecebimento() +
                "\n" +
                "Status: " + result.getCodigoStatus() + " - " + result.getMotivo() +
                "\n" +
                "Protocolo: " + result.getMdfProtocolo().getProtocoloInfo().getNumeroProtocolo() +
                "\n" +
                "Chave de Acesso: " + result.getMdfProtocolo().getProtocoloInfo().getChave();
    }
}
