package io.github.blackfishlabs.fiscal4desktop.controller.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmitDTO extends BasicDTO {

    @JsonProperty("CNPJ")
    private String CNPJ;

    @JsonProperty("xNome")
    private String xNome;

    @JsonProperty("xFant")
    private String xFant;

    @JsonProperty("enderEmit")
    private EnderEmitDTO enderEmit;

    @JsonProperty("IE")
    private String IE;

    @JsonProperty("CRT")
    private String CRT;
}
