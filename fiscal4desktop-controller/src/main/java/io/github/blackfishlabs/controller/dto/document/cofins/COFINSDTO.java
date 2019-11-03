package io.github.blackfishlabs.controller.dto.document.cofins;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class COFINSDTO extends BasicDTO {

    @JsonProperty("COFINSNT")
    private COFINSNTDTO COFINSNT;

    @JsonProperty("COFINSAliq")
    private COFINSAliqDTO COFINSAliq;

    @JsonProperty("COFINSOutr")
    private COFINSOutrDTO COFINSOutr;

}
