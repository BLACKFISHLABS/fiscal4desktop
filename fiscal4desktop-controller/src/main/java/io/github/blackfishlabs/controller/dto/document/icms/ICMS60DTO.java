package io.github.blackfishlabs.controller.dto.document.icms;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ICMS60DTO extends BasicDTO {

    @JsonProperty("orig")
    private String orig;

    @JsonProperty("CST")
    private String CST;

    @JsonProperty("vBCSTRet")
    private String vBCSTRet;

    @JsonProperty("vICMSSTRet")
    private String vICMSSTRet;

}
