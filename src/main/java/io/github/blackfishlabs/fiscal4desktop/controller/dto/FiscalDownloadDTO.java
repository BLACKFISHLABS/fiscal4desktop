package io.github.blackfishlabs.fiscal4desktop.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FiscalDownloadDTO {

    private String emitter;
    private String password;
    private String key;
    private String cnpj;

}
