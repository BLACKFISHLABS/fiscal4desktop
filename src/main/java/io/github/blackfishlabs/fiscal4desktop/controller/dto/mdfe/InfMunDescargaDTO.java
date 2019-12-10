package io.github.blackfishlabs.fiscal4desktop.controller.dto.mdfe;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class InfMunDescargaDTO extends BasicDTO {

    @JsonProperty("cMunDescarga")
    private String cMunDescarga;

    @JsonProperty("xMunDescarga")
    private String xMunDescarga;

    @JsonProperty("infNFe")
    private List<InfNFeDTO> infNFe;

}
