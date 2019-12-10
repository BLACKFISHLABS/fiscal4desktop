package io.github.blackfishlabs.fiscal4desktop.domain.domain;

import io.github.blackfishlabs.fiscal4desktop.infra.MDFeConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FiscalMDFeCancelDomain {

    private MDFeConfiguration configuration;
    private String key;
    private String protocol;
    private String justification;
}
