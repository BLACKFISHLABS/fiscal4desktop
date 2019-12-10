package io.github.blackfishlabs.fiscal4desktop.controller.dto.document.pis;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PISAliqDTO extends BasicDTO {

    @JsonProperty("CST")
    private String CST;

    @JsonProperty("vBC")
    private String vBC;

    @JsonProperty("pPIS")
    private String pPIS;

    @JsonProperty("vPIS")
    private String vPIS;

}
