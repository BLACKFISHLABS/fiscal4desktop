package io.github.blackfishlabs.fiscal4desktop.controller.dto.document.icms;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ICMS20DTO extends BasicDTO {

    @JsonProperty("orig")
    private String orig;

    @JsonProperty("CST")
    private String CST;

    @JsonProperty("modBC")
    private String modBC;

    @JsonProperty("pRedBC")
    private String pRedBC;

    @JsonProperty("vBC")
    private String vBC;

    @JsonProperty("pICMS")
    private String pICMS;

    @JsonProperty("vICMS")
    private String vICMS;
}
