package io.github.blackfishlabs.fiscal4desktop.controller.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.document.cofins.COFINSDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.document.icms.ICMSDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.document.icms.ICMSUFDestDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.document.ipi.IPIDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.document.pis.PISDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImpostoDTO extends BasicDTO {

    @JsonProperty("vTotTrib")
    private String vTotTrib;

    @JsonProperty("ICMS")
    private ICMSDTO ICMS;

    @JsonProperty("PIS")
    private PISDTO PIS;

    @JsonProperty("COFINS")
    private COFINSDTO COFINS;

    @JsonProperty("IPI")
    private IPIDTO IPI;

    @JsonProperty("ICMSUFDest")
    private ICMSUFDestDTO ICMSUFDest;

}
