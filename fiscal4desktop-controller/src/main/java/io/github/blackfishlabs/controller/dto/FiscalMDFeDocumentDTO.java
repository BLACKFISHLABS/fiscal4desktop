package io.github.blackfishlabs.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import io.github.blackfishlabs.controller.dto.mdfe.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FiscalMDFeDocumentDTO extends BasicDTO {

    @JsonProperty("ide")
    private IdeMDFeDTO ide;

    @JsonProperty("emit")
    private EmitMDFeDTO emit;

    @JsonProperty("infModal")
    private InfModalMDFeDTO infModal;

    @JsonProperty("infDoc")
    private InfDocDTO infDoc;

    @JsonProperty("seg")
    private List<SegDTO> seg;

    @JsonProperty("tot")
    private TotalMDFeDTO tot;

    @JsonProperty("infAdic")
    private InfAdicMDFeDTO infAdic;

}
