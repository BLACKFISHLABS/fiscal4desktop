package io.github.blackfishlabs.controller.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FatDTO extends BasicDTO {

    @JsonProperty("nFat")
    private String nFat;

    @JsonProperty("vOrig")
    private String vOrig;

    @JsonProperty("vDesc")
    private String vDesc;

    @JsonProperty("vLiq")
    private String vLiq;
}
