package io.github.blackfishlabs.domain.domain;

import io.github.blackfishlabs.fiscal4desktop.infra.MDFeConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FiscalMDFeStatusNotClosingDomain {

    private MDFeConfiguration configuration;
    private String cnpj;

}
