package io.github.blackfishlabs.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FiscalStatusDocumentDTO {

    private String emitter;
    private String password;
    private String key;
}
