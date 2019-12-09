package io.github.blackfishlabs.domain.domain;

import io.github.blackfishlabs.infra.MDFeConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FiscalMDFeStatusDomain {

    private MDFeConfiguration configuration;
    private String key;
}