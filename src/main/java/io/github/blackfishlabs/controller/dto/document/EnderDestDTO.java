package io.github.blackfishlabs.controller.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EnderDestDTO extends BasicDTO {

    @JsonProperty("xLgr")
    private String xLgr;

    @JsonProperty("nro")
    private String nro;

    @JsonProperty("xCpl")
    private String xCpl;

    @JsonProperty("xBairro")
    private String xBairro;

    @JsonProperty("cMun")
    private String cMun;

    @JsonProperty("xMun")
    private String xMun;

    @JsonProperty("UF")
    private String UF;

    @JsonProperty("CEP")
    private String CEP;

    @JsonProperty("cPais")
    private String cPais;

    @JsonProperty("xPais")
    private String xPais;

    @JsonProperty("fone")
    private String fone;

}
