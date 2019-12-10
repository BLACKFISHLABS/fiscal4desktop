package io.github.blackfishlabs.fiscal4desktop.controller.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProdDTO extends BasicDTO {

    @JsonProperty("cProd")
    private String cProd;

    @JsonProperty("cEAN")
    private String cEAN;

    @JsonProperty("xProd")
    private String xProd;

    @JsonProperty("NCM")
    private String NCM;

    @JsonProperty("CEST")
    private String CEST;

    @JsonProperty("cBenef")
    private String cBenef;

    @JsonProperty("CFOP")
    private String CFOP;

    @JsonProperty("uCom")
    private String uCom;

    @JsonProperty("qCom")
    private String qCom;

    @JsonProperty("vUnCom")
    private String vUnCom;

    @JsonProperty("vProd")
    private String vProd;

    @JsonProperty("cEANTrib")
    private String cEANTrib;

    @JsonProperty("uTrib")
    private String uTrib;

    @JsonProperty("qTrib")
    private String qTrib;

    @JsonProperty("vUnTrib")
    private String vUnTrib;

    @JsonProperty("vFrete")
    private String vFrete;

    @JsonProperty("vSeg")
    private String vSeg;

    @JsonProperty("vDesc")
    private String vDesc;

    @JsonProperty("indTot")
    private String indTot;
}
