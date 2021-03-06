package io.github.blackfishlabs.fiscal4desktop.controller.dto.document.ipi;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IPIDTO extends BasicDTO {

    @JsonProperty("cEnq")
    private String cEnq;

    @JsonProperty("IPITrib")
    private IPITribDTO IPITrib;

}
