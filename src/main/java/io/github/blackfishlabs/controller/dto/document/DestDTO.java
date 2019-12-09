package io.github.blackfishlabs.controller.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DestDTO extends BasicDTO {

    @JsonProperty("CNPJ")
    private String CNPJ;

    @JsonProperty("CPF")
    private String CPF;

    @JsonProperty("xNome")
    private String xNome;

    @JsonProperty("email")
    private String email;

    @JsonProperty("enderDest")
    private EnderDestDTO enderDest;

    @JsonProperty("indIEDest")
    private String indIEDest;

    @JsonProperty("IE")
    private String IE;

}
