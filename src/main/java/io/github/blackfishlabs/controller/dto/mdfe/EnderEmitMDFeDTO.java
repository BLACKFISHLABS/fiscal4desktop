package io.github.blackfishlabs.controller.dto.mdfe;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EnderEmitMDFeDTO extends BasicDTO {

    @JsonProperty("xLgr")
    private String xLgr;

    @JsonProperty("nro")
    private String nro;

    @JsonProperty("xBairro")
    private String xBairro;

    @JsonProperty("cMun")
    private String cMun;

    @JsonProperty("xMun")
    private String xMun;

    @JsonProperty("CEP")
    private String CEP;

    @JsonProperty("UF")
    private String UF;

    @JsonProperty("fone")
    private String fone;

    @JsonProperty("email")
    private String email;

}
