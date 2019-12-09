package io.github.blackfishlabs.controller.dto.document.icms;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ICMSTotDTO extends BasicDTO {

    @JsonProperty("vBC")
    private String vBC;

    @JsonProperty("vICMS")
    private String vICMS;

    @JsonProperty("vICMSDeson")
    private String vICMSDeson;

    @JsonProperty("vFCPUFDest")
    private String vFCPUFDest;

    @JsonProperty("vICMSUFDest")
    private String vICMSUFDest;

    @JsonProperty("vICMSUFRemet")
    private String vICMSUFRemet;

    @JsonProperty("vFCP")
    private String vFCP;

    @JsonProperty("vFCPST")
    private String vFCPST;

    @JsonProperty("vFCPSTRet")
    private String vFCPSTRet;

    @JsonProperty("vBCST")
    private String vBCST;

    @JsonProperty("vST")
    private String vST;

    @JsonProperty("vProd")
    private String vProd;

    @JsonProperty("vFrete")
    private String vFrete;

    @JsonProperty("vSeg")
    private String vSeg;

    @JsonProperty("vDesc")
    private String vDesc;

    @JsonProperty("vII")
    private String vII;

    @JsonProperty("vIPI")
    private String vIPI;

    @JsonProperty("vIPIDevol")
    private String vIPIDevol;

    @JsonProperty("vCOFINS")
    private String vCOFINS;

    @JsonProperty("vPIS")
    private String vPIS;

    @JsonProperty("vOutro")
    private String vOutro;

    @JsonProperty("vNF")
    private String vNF;

    @JsonProperty("vTotTrib")
    private String vTotTrib;

}
