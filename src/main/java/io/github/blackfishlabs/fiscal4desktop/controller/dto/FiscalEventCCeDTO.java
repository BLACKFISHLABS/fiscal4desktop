package io.github.blackfishlabs.fiscal4desktop.controller.dto;

import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
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
    private int seq = 1;
}
