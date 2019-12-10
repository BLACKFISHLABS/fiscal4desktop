package io.github.blackfishlabs.fiscal4desktop.controller.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TranspDTO extends BasicDTO {

    @JsonProperty("modFrete")
    private String modFrete;

    @JsonProperty("transporta")
    private TransportaDTO transporta;

    @JsonProperty("vol")
    private VolDTO vol;

    @JsonProperty("veicTransp")
    private VeicTranspDTO veicTransp;

}
