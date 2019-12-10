package io.github.blackfishlabs.fiscal4desktop.controller.dto.mdfe;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PropDTO extends BasicDTO {

    @JsonProperty("CPF")
    private String CPF;

    @JsonProperty("CNPJ")
    private String CNPJ;

    @JsonProperty("RNTRC")
    private String RNTRC;

    @JsonProperty("xNome")
    private String xNome;

    @JsonProperty("IE")
    private String IE;

    @JsonProperty("UF")
    private String UF;

    @JsonProperty("tpProp")
    private String tpProp;

}
