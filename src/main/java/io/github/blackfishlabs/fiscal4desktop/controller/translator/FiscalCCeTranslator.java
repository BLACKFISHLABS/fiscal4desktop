package io.github.blackfishlabs.fiscal4desktop.controller.translator;

import br.indie.fiscal4j.nfe400.classes.evento.NFEnviaEventoRetorno;
import br.indie.fiscal4j.nfe400.classes.evento.NFEventoRetorno;
import br.indie.fiscal4j.nfe400.classes.evento.NFInfoEventoRetorno;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.FiscalEventCCeDTO;
import io.github.blackfishlabs.fiscal4desktop.domain.domain.FiscalCCeDomain;
import io.github.blackfishlabs.fiscal4desktop.infra.NFeConfiguration;

import java.util.Optional;

public class FiscalCCeTranslator implements Translator<FiscalEventCCeDTO, FiscalCCeDomain, NFEnviaEventoRetorno> {

    @Override
    public FiscalCCeDomain fromDTO(FiscalEventCCeDTO dto) {
        FiscalCCeDomain domain = new FiscalCCeDomain();
        domain.setConfiguration(new NFeConfiguration(dto.getEmitter(), dto.getPassword()));
        domain.setCorrection(dto.getCorrection());
        domain.setKey(dto.getKey());
        domain.setSequential(dto.getSeq());
        return domain;
    }

    @Override
    public String response(NFEnviaEventoRetorno result) {
        final StringBuilder sb = new StringBuilder();

        Optional<NFEventoRetorno> event = result.getEventoRetorno().stream().findFirst();
        if (event.isPresent()) {
            NFInfoEventoRetorno info = event.get().getInfoEventoRetorno();

            sb.append("Ambiente: ").append(info.getAmbiente());
            sb.append("\n");
            sb.append("Tipo do Evento: ").append(info.getTipoEvento());
            sb.append("\n");
            sb.append("Data do Registro: ").append(info.getDataHoraRegistro());
            sb.append("\n");
            sb.append("Status: ").append(info.getCodigoStatus()).append(" - ").append(info.getMotivo());
            sb.append("\n");
            sb.append("Protocolo: ").append(info.getNumeroProtocolo());
            sb.append("\n");
            sb.append("Chave de Acesso: ").append(info.getChave());
        }

        return sb.toString();
    }
}
