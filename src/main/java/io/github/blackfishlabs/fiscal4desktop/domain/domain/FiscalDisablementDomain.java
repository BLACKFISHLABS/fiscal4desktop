package io.github.blackfishlabs.fiscal4desktop.domain.domain;

import br.indie.fiscal4j.DFModelo;
import io.github.blackfishlabs.fiscal4desktop.infra.NFeConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FiscalDisablementDomain {

    private NFeConfiguration configuration;
    private int year;
    private String emitter;
    private String series;
    private String initialNumber;
    private String finalNumber;
    private String justification;
    private DFModelo model;

}
