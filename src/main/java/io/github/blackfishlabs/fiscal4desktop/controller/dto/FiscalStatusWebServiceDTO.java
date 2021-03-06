package io.github.blackfishlabs.fiscal4desktop.controller.dto;

import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FiscalStatusWebServiceDTO extends BasicDTO {

    private String uf;
    private String model;
    private String emitter;
    private String password;
}
