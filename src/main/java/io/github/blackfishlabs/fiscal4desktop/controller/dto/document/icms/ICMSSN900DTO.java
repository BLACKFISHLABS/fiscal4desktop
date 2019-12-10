package io.github.blackfishlabs.fiscal4desktop.controller.dto.document.icms;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ICMSSN900DTO extends BasicDTO {

    @JsonProperty("orig")
    private String orig;

    @JsonProperty("CSOSN")
    private String CSOSN;

    @JsonProperty("modBCST")
    private String modBCST;

    @JsonProperty("pMVAST")
    private String pMVAST;

    @JsonProperty("vBCST")
    private String vBCST;

    @JsonProperty("pICMSST")
    private String pICMSST;

    @JsonProperty("vICMSST")
    private String vICMSST;
}
