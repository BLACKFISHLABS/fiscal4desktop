package io.github.blackfishlabs.domain.domain;

import br.indie.fiscal4j.DFUnidadeFederativa;
import io.github.blackfishlabs.fiscal4desktop.infra.MDFeConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

@Getter
@Setter
@NoArgsConstructor
public class FiscalMDFeClosingDomain {

    private MDFeConfiguration configuration;
    private String key;
    private String number;
    private String code;
    private DateTime closing;
    private DFUnidadeFederativa UF;
}
