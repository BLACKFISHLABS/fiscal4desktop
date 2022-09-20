package io.github.blackfishlabs.fiscal4desktop.controller.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RastroDTO extends BasicDTO {

    @JsonProperty("nLote")
    private String nLote;

    @JsonProperty("qLote")
    private String qLote;

    @JsonProperty("dFab")
    private String dFab;

    @JsonProperty("dVal")
    private String dVal;
}
