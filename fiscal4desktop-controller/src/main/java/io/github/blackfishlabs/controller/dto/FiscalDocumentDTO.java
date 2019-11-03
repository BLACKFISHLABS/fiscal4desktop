package io.github.blackfishlabs.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import io.github.blackfishlabs.controller.dto.document.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FiscalDocumentDTO extends BasicDTO {

    @JsonProperty("ide")
    private IdeDTO ide;

    @JsonProperty("emit")
    private EmitDTO emit;

    @JsonProperty("dest")
    private DestDTO dest;

    @JsonProperty("det")
    private List<DetDTO> det;

    @JsonProperty("pag")
    private PagDTO pag;

    @JsonProperty("total")
    private TotalDTO total;

    @JsonProperty("transp")
    private TranspDTO transp;

    @JsonProperty("cobr")
    private CobrDTO cobr;

    @JsonProperty("infAdic")
    private InfAdicDTO infAdic;
}
