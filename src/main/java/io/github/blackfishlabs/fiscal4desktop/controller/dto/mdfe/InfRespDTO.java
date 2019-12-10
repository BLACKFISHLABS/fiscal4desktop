package io.github.blackfishlabs.fiscal4desktop.controller.dto.mdfe;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InfRespDTO extends BasicDTO {

    @JsonProperty("CNPJ")
    private String CNPJ;

    @JsonProperty("CPF")
    private String CPF;

    @JsonProperty("respSeg")
    private String respSeg;

}
