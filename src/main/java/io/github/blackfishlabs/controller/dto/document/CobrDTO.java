package io.github.blackfishlabs.controller.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CobrDTO extends BasicDTO {

    @JsonProperty("fat")
    private FatDTO fat;

    @JsonProperty("dup")
    private List<DupDTO> dup;

}
