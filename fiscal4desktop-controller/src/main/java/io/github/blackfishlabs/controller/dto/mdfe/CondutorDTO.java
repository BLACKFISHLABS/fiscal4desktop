package io.github.blackfishlabs.controller.dto.mdfe;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CondutorDTO extends BasicDTO {

    @JsonProperty("xNome")
    private String xNome;

    @JsonProperty("CPF")
    private String CPF;

}
