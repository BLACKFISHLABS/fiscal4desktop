package io.github.blackfishlabs.fiscal4desktop.controller.dto.document.icms;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ICMSSN101DTO extends BasicDTO {

    @JsonProperty("orig")
    private String orig;

    @JsonProperty("CSOSN")
    private String CSOSN;

    @JsonProperty("pCredSN")
    private String pCredSN;

    @JsonProperty("vCredICMSSN")
    private String vCredICMSSN;

}
