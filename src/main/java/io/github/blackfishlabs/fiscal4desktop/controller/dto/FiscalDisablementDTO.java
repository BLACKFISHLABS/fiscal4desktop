package io.github.blackfishlabs.fiscal4desktop.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FiscalDisablementDTO {

    private String emitter;
    private String password;
    private String year;
    private String series;
    private String initialNumber;
    private String finalNumber;
    private String justification;
    private String model;

}
