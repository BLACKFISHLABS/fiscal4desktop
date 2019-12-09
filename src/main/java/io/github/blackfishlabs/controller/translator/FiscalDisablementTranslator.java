package io.github.blackfishlabs.controller.translator;

import br.indie.fiscal4j.DFModelo;
import br.indie.fiscal4j.nfe400.classes.evento.inutilizacao.NFRetornoEventoInutilizacao;
import io.github.blackfishlabs.controller.dto.FiscalDisablementDTO;
import io.github.blackfishlabs.domain.domain.FiscalDisablementDomain;
import io.github.blackfishlabs.infra.NFeConfiguration;

public class FiscalDisablementTranslator implements Translator<FiscalDisablementDTO, FiscalDisablementDomain, NFRetornoEventoInutilizacao> {

    @Override
    public FiscalDisablementDomain fromDTO(FiscalDisablementDTO dto) {
        FiscalDisablementDomain domain = new FiscalDisablementDomain();

        domain.setConfiguration(new NFeConfiguration(dto.getEmitter(), dto.getPassword()));
        domain.setEmitter(dto.getEmitter());
        domain.setYear(Integer.parseInt(dto.getYear()));
        domain.setSeries(dto.getSeries());
        domain.setInitialNumber(dto.getInitialNumber());
        domain.setFinalNumber(dto.getFinalNumber());
        domain.setJustification(dto.getJustification());
        domain.setModel(DFModelo.valueOfCodigo(dto.getModel()));

        return domain;
    }

    @Override
    public String response(NFRetornoEventoInutilizacao result) {
        return "Ambiente: " + result.getDados().getAmbiente() +
                "\n" +
                "Motivo: " + result.getDados().getMotivo() +
                "\n" +
                "Faixa Inutilizada: " + result.getDados().getNumeroNFInicial() + " - " + result.getDados().getNumeroNFFinal() +
                "\n" +
                "Data do Registro: " + result.getDados().getDatahoraRecebimento() +
                "\n" +
                "Status: " + result.getDados().getStatus() +
                "\n" +
                "Protocolo: " + result.getDados().getNumeroProtocolo();
    }

}
