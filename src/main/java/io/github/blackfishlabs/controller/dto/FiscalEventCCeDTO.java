package io.github.blackfishlabs.controller.dto;

import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FiscalEventCCeDTO extends BasicDTO {

    private String emitter;
    private String password;
    private String key;
    private String correction;

}
