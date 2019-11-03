package io.github.blackfishlabs.controller.dto.mdfe;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SegDTO extends BasicDTO {

    @JsonProperty("nApol")
    private String nApol;

    @JsonProperty("nAver")
    private List<String> nAver = Lists.newArrayList();

    @JsonProperty("infSeg")
    private InfSegDTO infSeg;

    @JsonProperty("infResp")
    private InfRespDTO infResp;

}
