package io.github.blackfishlabs.controller.dto.mdfe;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RodoDTO extends BasicDTO {

    @JsonProperty("infANTT")
    private InfANTTDTO infANTT;

    @JsonProperty("veicTracao")
    private VeicTracaoDTO veicTracao;

    @JsonProperty("codAgPorto")
    private String codAgPorto;

}
