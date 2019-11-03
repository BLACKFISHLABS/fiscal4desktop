package io.github.blackfishlabs.controller.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import io.github.blackfishlabs.controller.dto.document.icms.ICMSTotDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TotalDTO extends BasicDTO {

    @JsonProperty("ICMSTot")
    private ICMSTotDTO ICMSTot;

}
