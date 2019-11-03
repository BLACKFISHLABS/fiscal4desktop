package io.github.blackfishlabs.controller.dto.document.cofins;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class COFINSNTDTO extends BasicDTO {

    @JsonProperty("CST")
    private String CST;

}
