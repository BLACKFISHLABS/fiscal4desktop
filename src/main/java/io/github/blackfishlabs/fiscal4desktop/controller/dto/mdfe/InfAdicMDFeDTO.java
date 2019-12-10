package io.github.blackfishlabs.fiscal4desktop.controller.dto.mdfe;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InfAdicMDFeDTO extends BasicDTO {

    @JsonProperty("infAdFisco")
    private String infAdFisco;

    @JsonProperty("infCpl")
    private String infCpl;

}
