package io.github.blackfishlabs.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FiscalQueryDistributionDFeDTO {

    private String emitter;
    private String password;
    private String document;
    private String uf;
    private String key;
    private String nsu;
    private String lastNSU;
}
