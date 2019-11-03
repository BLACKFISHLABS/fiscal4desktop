package io.github.blackfishlabs.controller.dto;

import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FiscalSendDTO extends BasicDTO {

    private String emitter;
    private String password;
    private FiscalDocumentDTO fiscalDocumentDTO;
    private boolean isContingency = Boolean.FALSE;

}
