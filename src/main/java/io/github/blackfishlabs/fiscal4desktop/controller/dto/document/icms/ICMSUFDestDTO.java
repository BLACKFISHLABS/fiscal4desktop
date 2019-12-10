package io.github.blackfishlabs.fiscal4desktop.controller.dto.document.icms;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ICMSUFDestDTO extends BasicDTO {

    @JsonProperty("vBCUFDest")
    private String vBCUFDest;

    @JsonProperty("pFCPUFDest")
    private String pFCPUFDest;

    @JsonProperty("pICMSUFDest")
    private String pICMSUFDest;

    @JsonProperty("pICMSInter")
    private String pICMSInter;

    @JsonProperty("pICMSInterPart")
    private String pICMSInterPart;

    @JsonProperty("vFCPUFDest")
    private String vFCPUFDest;

    @JsonProperty("vICMSUFDest")
    private String vICMSUFDest;

    @JsonProperty("vICMSUFRemet")
    private String vICMSUFRemet;

    @JsonProperty("vBCFCPUFDest")
    private String vBCFCPUFDest;

}
