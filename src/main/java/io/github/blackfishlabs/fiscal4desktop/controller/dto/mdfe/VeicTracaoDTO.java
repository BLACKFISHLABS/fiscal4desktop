package io.github.blackfishlabs.fiscal4desktop.controller.dto.mdfe;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VeicTracaoDTO extends BasicDTO {

    @JsonProperty("placa")
    private String placa;

    @JsonProperty("tara")
    private String tara;

    @JsonProperty("capKG")
    private String capKG;

    @JsonProperty("capM3")
    private String capM3;

    @JsonProperty("prop")
    private PropDTO prop;

    @JsonProperty("condutor")
    private CondutorDTO condutor;

    @JsonProperty("tpRod")
    private String tpRod;

    @JsonProperty("tpCar")
    private String tpCar;

    @JsonProperty("UF")
    private String UF;
}
