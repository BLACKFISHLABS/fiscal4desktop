package io.github.blackfishlabs.fiscal4desktop.controller.dto.document.ipi;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IPITribDTO extends BasicDTO {

    @JsonProperty("CST")
    private String CST;

    @JsonProperty("vBC")
    private String vBC;

    @JsonProperty("pIPI")
    private String pIPI;

    @JsonProperty("qUnid")
    private String qUnid;

    @JsonProperty("vUnid")
    private String vUnid;

    @JsonProperty("vIPI")
    private String vIPI;
}
