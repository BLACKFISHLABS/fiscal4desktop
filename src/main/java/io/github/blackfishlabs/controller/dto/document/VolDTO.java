package io.github.blackfishlabs.controller.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VolDTO extends BasicDTO {

    @JsonProperty("qVol")
    private String qVol;

    @JsonProperty("esp")
    private String esp;

    @JsonProperty("pesoL")
    private String pesoL;

    @JsonProperty("pesoB")
    private String pesoB;

}
