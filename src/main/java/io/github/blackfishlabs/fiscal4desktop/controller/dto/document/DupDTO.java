package io.github.blackfishlabs.fiscal4desktop.controller.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DupDTO extends BasicDTO {

    @JsonProperty("nDup")
    private String nDup;

    @JsonProperty("nVenc")
    private String nVenc;

    @JsonProperty("vDup")
    private String vDup;
}
