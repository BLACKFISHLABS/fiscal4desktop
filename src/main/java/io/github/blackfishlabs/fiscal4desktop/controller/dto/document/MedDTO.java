package io.github.blackfishlabs.fiscal4desktop.controller.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MedDTO extends BasicDTO {

    @JsonProperty("cProdANVISA")
    private String cProdANVISA;

    @JsonProperty("vPMC")
    private String vPMC;

    @JsonProperty("xMotivoIsencao")
    private String xMotivoIsencao;
}
