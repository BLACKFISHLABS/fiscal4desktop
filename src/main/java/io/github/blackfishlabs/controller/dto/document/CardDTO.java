package io.github.blackfishlabs.controller.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CardDTO extends BasicDTO {

    @JsonProperty("tpIntegra")
    private String tpIntegra;

    @JsonProperty("CNPJ")
    private String CNPJ;

    @JsonProperty("tBand")
    private String tBand;

    @JsonProperty("cAut")
    private String cAut;

}
