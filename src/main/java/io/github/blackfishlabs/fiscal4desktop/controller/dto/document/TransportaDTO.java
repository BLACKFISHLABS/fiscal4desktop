package io.github.blackfishlabs.fiscal4desktop.controller.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransportaDTO extends BasicDTO {

    @JsonProperty("CNPJ")
    private String CNPJ;

    @JsonProperty("CPF")
    private String CPF;

    @JsonProperty("xNome")
    private String xNome;

    @JsonProperty("IE")
    private String IE;

    @JsonProperty("xEnder")
    private String xEnder;

    @JsonProperty("xMun")
    private String xMun;

    @JsonProperty("UF")
    private String UF;
}
