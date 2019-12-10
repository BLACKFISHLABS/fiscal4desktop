package io.github.blackfishlabs.fiscal4desktop.controller.dto.mdfe;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmitMDFeDTO extends BasicDTO {

    @JsonProperty("CNPJ")
    private String CNPJ;

    @JsonProperty("IE")
    private String IE;

    @JsonProperty("xNome")
    private String xNome;

    @JsonProperty("xFant")
    private String xFant;

    @JsonProperty("enderEmit")
    private EnderEmitMDFeDTO enderEmit;
}
