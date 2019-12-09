package io.github.blackfishlabs.controller.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetPagDTO extends BasicDTO {

    @JsonProperty("tPag")
    private String tPag;

    @JsonProperty("vPag")
    private String vPag;

    @JsonProperty("card")
    private CardDTO card;

}
