package io.github.blackfishlabs.controller.dto.document.icms;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ICMS10DTO extends BasicDTO {

    @JsonProperty("orig")
    private String orig;

    @JsonProperty("CST")
    private String CST;

    @JsonProperty("modBC")
    private String modBC;

    @JsonProperty("vBC")
    private String vBC;

    @JsonProperty("pICMS")
    private String pICMS;

    @JsonProperty("vICMS")
    private String vICMS;

    @JsonProperty("modBCST")
    private String modBCST;

    @JsonProperty("pMVAST")
    private String pMVAST;

    @JsonProperty("pRedBCST")
    private String pRedBCST;

    @JsonProperty("vBCST")
    private String vBCST;

    @JsonProperty("pICMSST")
    private String pICMSST;

    @JsonProperty("vICMSST")
    private String vICMSST;

}
