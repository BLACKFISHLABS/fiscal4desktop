package io.github.blackfishlabs.controller.translator;

import br.indie.fiscal4j.DFUnidadeFederativa;
import br.indie.fiscal4j.mdfe3.classes.nota.evento.MDFeRetorno;
import io.github.blackfishlabs.controller.dto.FiscalMDFeClosingDTO;
import io.github.blackfishlabs.domain.domain.FiscalMDFeClosingDomain;
import io.github.blackfishlabs.infra.MDFeConfiguration;
import org.joda.time.DateTime;

public class FiscalMDFeClosingTranslator implements Translator<FiscalMDFeClosingDTO, FiscalMDFeClosingDomain, MDFeRetorno> {

    @Override
    public FiscalMDFeClosingDomain fromDTO(FiscalMDFeClosingDTO dto) {
        FiscalMDFeClosingDomain domain = new FiscalMDFeClosingDomain();

        domain.setConfiguration(new MDFeConfiguration(dto.getEmitter(), dto.getPassword()));
        domain.setClosing(DateTime.parse(dto.getClosing()));
        domain.setCode(dto.getCode());
        domain.setKey(dto.getKey());
        domain.setNumber(dto.getNumber());
        domain.setUF(DFUnidadeFederativa.valueOfCodigo(dto.getUF()));

        return domain;
    }

    @Override
    public String response(MDFeRetorno result) {
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
