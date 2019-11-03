package io.github.blackfishlabs.controller.translator;

import br.indie.fiscal4j.mdfe3.classes.nota.evento.MDFeRetorno;
import io.github.blackfishlabs.controller.dto.FiscalMDFeCancellationDTO;
import io.github.blackfishlabs.domain.domain.FiscalMDFeCancelDomain;
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
        final StringBuilder sb = new StringBuilder();

        sb.append("Ambiente: ").append(result.getEventoRetorno().getAmbiente());
        sb.append("\n");
        sb.append("Tipo do Evento: ").append(result.getEventoRetorno().getTipoEvento());
        sb.append("\n");
        sb.append("Data do Registro: ").append(result.getEventoRetorno().getDataHoraRegistro());
        sb.append("\n");
        sb.append("Status: ").append(result.getEventoRetorno().getCodigoStatus()).append(" - ").append(result.getEventoRetorno().getMotivo());
        sb.append("\n");
        sb.append("Protocolo: ").append(result.getEventoRetorno().getNumeroProtocolo());
        sb.append("\n");
        sb.append("Chave de Acesso: ").append(result.getEventoRetorno().getChave());

        return sb.toString();
    }
}
