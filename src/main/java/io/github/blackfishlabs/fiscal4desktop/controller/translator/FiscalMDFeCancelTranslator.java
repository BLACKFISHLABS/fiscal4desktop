package io.github.blackfishlabs.fiscal4desktop.controller.translator;

import br.indie.fiscal4j.mdfe3.classes.nota.evento.MDFeRetorno;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.FiscalMDFeCancellationDTO;
import io.github.blackfishlabs.fiscal4desktop.domain.domain.FiscalMDFeCancelDomain;
import io.github.blackfishlabs.fiscal4desktop.infra.MDFeConfiguration;

public class FiscalMDFeCancelTranslator implements Translator<FiscalMDFeCancellationDTO, FiscalMDFeCancelDomain, MDFeRetorno> {

    @Override
    public FiscalMDFeCancelDomain fromDTO(FiscalMDFeCancellationDTO dto) {
        FiscalMDFeCancelDomain domain = new FiscalMDFeCancelDomain();

        domain.setConfiguration(new MDFeConfiguration(dto.getEmitter(), dto.getPassword()));
        domain.setJustification(dto.getJustification());
        domain.setKey(dto.getKey());
        domain.setProtocol(dto.getProtocol());

        return domain;
    }

    @Override
    public String response(MDFeRetorno result) throws Exception {

        String sb = "Ambiente: " + result.getEventoRetorno().getAmbiente() +
                "\n" +
                "Tipo do Evento: " + result.getEventoRetorno().getTipoEvento() +
                "\n" +
                "Data do Registro: " + result.getEventoRetorno().getDataHoraRegistro() +
                "\n" +
                "Status: " + result.getEventoRetorno().getCodigoStatus() + " - " + result.getEventoRetorno().getMotivo() +
                "\n" +
                "Protocolo: " + result.getEventoRetorno().getNumeroProtocolo() +
                "\n" +
                "Chave de Acesso: " + result.getEventoRetorno().getChave();

        return sb;
    }
}
