package io.github.blackfishlabs.controller.dto.document.pis;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PISDTO extends BasicDTO {

    @JsonProperty("PISNT")
    private PISNTDTO PISNT;

    @JsonProperty("PISAliq")
    private PISAliqDTO PISAliq;

    @JsonProperty("PISOutr")
    private PISOutrDTO PISOutr;

}
