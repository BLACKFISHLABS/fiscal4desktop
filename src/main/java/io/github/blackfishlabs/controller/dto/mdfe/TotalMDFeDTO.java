package io.github.blackfishlabs.controller.dto.mdfe;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TotalMDFeDTO {

    @JsonProperty("qNFe")
    private String qNFe;

    @JsonProperty("vCarga")
    private String vCarga;

    @JsonProperty("cUnid")
    private String cUnid;

    @JsonProperty("qCarga")
    private String qCarga;

}
