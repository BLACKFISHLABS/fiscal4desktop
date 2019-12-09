package io.github.blackfishlabs.domain.domain;

import io.github.blackfishlabs.infra.NFeConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FiscalCancelDomain {

    private NFeConfiguration configuration;
    private String key;
    private String protocol;
    private String justification;

}
