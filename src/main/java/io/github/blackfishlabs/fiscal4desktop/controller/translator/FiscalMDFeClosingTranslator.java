package io.github.blackfishlabs.fiscal4desktop.controller.translator;

import br.indie.fiscal4j.DFUnidadeFederativa;
import br.indie.fiscal4j.mdfe3.classes.nota.evento.MDFeRetorno;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.FiscalMDFeClosingDTO;
import io.github.blackfishlabs.fiscal4desktop.domain.domain.FiscalMDFeClosingDomain;
import io.github.blackfishlabs.fiscal4desktop.infra.MDFeConfiguration;
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
