package io.github.blackfishlabs.domain.domain;

import br.indie.fiscal4j.DFModelo;
import br.indie.fiscal4j.DFUnidadeFederativa;
import io.github.blackfishlabs.infra.NFeConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FiscalStatusWebServiceDomain {

    private NFeConfiguration configuration;
    private DFUnidadeFederativa uf;
    private DFModelo model;
}
