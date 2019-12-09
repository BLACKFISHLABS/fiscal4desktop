package io.github.blackfishlabs.controller.dto.document.cofins;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class COFINSAliqDTO extends BasicDTO {

    @JsonProperty("CST")
    private String CST;

    @JsonProperty("vBC")
    private String vBC;

    @JsonProperty("pCOFINS")
    private String pCOFINS;

    @JsonProperty("vCOFINS")
    private String vCOFINS;
}
