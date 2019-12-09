package io.github.blackfishlabs.controller.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetDTO extends BasicDTO {

    @JsonProperty("nItem")
    private String nItem;

    @JsonProperty("prod")
    private ProdDTO prod;

    @JsonProperty("imposto")
    private ImpostoDTO imposto;
}
