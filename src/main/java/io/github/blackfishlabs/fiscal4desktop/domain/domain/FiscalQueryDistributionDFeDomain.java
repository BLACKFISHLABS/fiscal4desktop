package io.github.blackfishlabs.fiscal4desktop.domain.domain;

import br.indie.fiscal4j.DFUnidadeFederativa;
import io.github.blackfishlabs.fiscal4desktop.infra.NFeConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FiscalQueryDistributionDFeDomain {

    private NFeConfiguration configuration;
    private String document;
    private DFUnidadeFederativa uf;
    private String key;
    private String nsu;
    private String lastNSU;
}
