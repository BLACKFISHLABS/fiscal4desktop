package io.github.blackfishlabs.controller.dto.document.icms;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ICMS51DTO extends BasicDTO {

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

    @JsonProperty("vICMSOp")
    private String vICMSOp;

    @JsonProperty("pDif")
    private String pDif;

    @JsonProperty("vICMSDif")
    private String vICMSDif;

    @JsonProperty("vICMS")
    private String vICMS;
}
