package io.github.blackfishlabs.controller.dto.document.icms;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ICMSSN202DTO extends BasicDTO {

    @JsonProperty("orig")
    private String origem;

    @JsonProperty("CSOSN")
    private String situacaoOperacaoSN;

    @JsonProperty("modBCST")
    private String modalidadeBCICMSST;

    @JsonProperty("pMVAST")
    private String percentualMargemValorAdicionadoICMSST;

    @JsonProperty("pRedBCST")
    private String percentualReducaoBCICMSST;

    @JsonProperty("vBCST")
    private String valorBCICMSST;

    @JsonProperty("pICMSST")
    private String percentualAliquotaImpostoICMSST;

    @JsonProperty("vICMSST")
    private String valorICMSST;

    @JsonProperty("vBCFCPST")
    private String valorBCFundoCombatePobrezaST;

    @JsonProperty("pFCPST")
    private String percentualFundoCombatePobrezaST;

    @JsonProperty("vFCPST")
    private String valorFundoCombatePobrezaST;
}
