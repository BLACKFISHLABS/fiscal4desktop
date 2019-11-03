package io.github.blackfishlabs.controller.dto.document.icms;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ICMSDTO extends BasicDTO {

    @JsonProperty("ICMS00")
    private ICMS00DTO ICMS00;

    @JsonProperty("ICMS10")
    private ICMS10DTO ICMS10;

    @JsonProperty("ICMS20")
    private ICMS20DTO ICMS20;

    @JsonProperty("ICMS30")
    private ICMS30DTO ICMS30;

    @JsonProperty("ICMS40")
    private ICMS40DTO ICMS40;

    @JsonProperty("ICMS41")
    private ICMS41DTO ICMS41;

    @JsonProperty("ICMS50")
    private ICMS50DTO ICMS50;

    @JsonProperty("ICMS51")
    private ICMS51DTO ICMS51;

    @JsonProperty("ICMS60")
    private ICMS60DTO ICMS60;

    @JsonProperty("ICMS70")
    private ICMS70DTO ICMS70;

    @JsonProperty("ICMS90")
    private ICMS90DTO ICMS90;

    @JsonProperty("ICMSPart10")
    private ICMSPart10DTO ICMSPart10;

    @JsonProperty("ICMSPart90")
    private ICMSPart90DTO ICMSPart90;

    @JsonProperty("ICMSST")
    private ICMSSTDTO ICMSST;

    @JsonProperty("ICMSSN101")
    private ICMSSN101DTO ICMSSN101;

    @JsonProperty("ICMSSN102")
    private ICMSSN102DTO ICMSSN102;

    @JsonProperty("ICMSSN300")
    private ICMSSN300DTO ICMSSN300;

    @JsonProperty("ICMSSN400")
    private ICMSSN400DTO ICMSSN400;

    @JsonProperty("ICMSSN201")
    private ICMSSN201DTO ICMSSN201;

    @JsonProperty("ICMSSN202")
    private ICMSSN202DTO ICMSSN202;

    @JsonProperty("ICMSSN203")
    private ICMSSN203DTO ICMSSN203;

    @JsonProperty("ICMSSN500")
    private ICMSSN500DTO ICMSSN500;

    @JsonProperty("ICMSSN900")
    private ICMSSN900DTO ICMSSN900;
}
