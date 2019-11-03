package io.github.blackfishlabs.controller.dto.mdfe;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import io.github.blackfishlabs.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class IdeMDFeDTO extends BasicDTO {

    @JsonProperty("cUF")
    private String cUF;

    @JsonProperty("tpAmb")
    private String tpAmb;

    @JsonProperty("tpEmit")
    private String tpEmit;

    @JsonProperty("mod")
    private String mod;

    @JsonProperty("serie")
    private String serie;

    @JsonProperty("nMDF")
    private String nMDF;

    @JsonProperty("cMDF")
    private String cMDF;

    @JsonProperty("cDV")
    private String cDV;

    @JsonProperty("modal")
    private String modal;

    @JsonProperty("dhEmi")
    private String dhEmi;

    @JsonProperty("tpEmis")
    private String tpEmis;

    @JsonProperty("procEmi")
    private String procEmi;

    @JsonProperty("verProc")
    private String verProc;

    @JsonProperty("UFIni")
    private String UFIni;

    @JsonProperty("UFFim")
    private String UFFim;

    @JsonProperty("infMunCarrega")
    private InfMunCarregaDTO infMunCarrega;

    @JsonProperty("infPercurso")
    private List<InfPercursoDTO> infPercurso = Lists.newArrayList();
}
