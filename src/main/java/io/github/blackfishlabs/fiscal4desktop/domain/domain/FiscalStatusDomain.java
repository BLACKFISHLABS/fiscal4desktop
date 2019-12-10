package io.github.blackfishlabs.fiscal4desktop.domain.domain;

import io.github.blackfishlabs.fiscal4desktop.infra.NFeConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FiscalStatusDomain {

    private NFeConfiguration configuration;
    private String key;
}
