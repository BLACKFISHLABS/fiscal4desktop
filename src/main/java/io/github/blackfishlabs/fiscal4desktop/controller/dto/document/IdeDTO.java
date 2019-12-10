package io.github.blackfishlabs.fiscal4desktop.controller.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.common.BasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class IdeDTO extends BasicDTO {

    @JsonProperty("cUF")
    private String cUF;

    @JsonProperty("xUF")
    private String xUF;

    @JsonProperty("cNF")
    private String cNF;

    @JsonProperty("natOp")
    private String natOp;

    @JsonProperty("mod")
    private String mod;

    @JsonProperty("serie")
    private String serie;

    @JsonProperty("nNF")
    private String nNF;

    @JsonProperty("dhEmi")
    private String dhEmi;

    @JsonProperty("dhSaiEnt")
    private String dhSaiEnt;

    @JsonProperty("tpNF")
    private String tpNF;

    @JsonProperty("idDest")
    private String idDest;

    @JsonProperty("cMunFG")
    private String cMunFG;

    @JsonProperty("tpImp")
    private String tpImp;

    @JsonProperty("tpEmis")
    private String tpEmis;

    @JsonProperty("cDV")
    private String cDV;

    @JsonProperty("tpAmb")
    private String tpAmb;

    @JsonProperty("finNFe")
    private String finNFe;

    @JsonProperty("indFinal")
    private String indFinal;

    @JsonProperty("indPres")
    private String indPres;

    @JsonProperty("procEmi")
    private String procEmi;

    @JsonProperty("verProc")
    private String verProc;

    @JsonProperty("cscH")
    private String cscH;

    @JsonProperty("cscP")
    private String cscP;

    @JsonProperty("dhCont")
    private String dhCont;

    @JsonProperty("xJust")
    private String xJust;

    @JsonProperty("NFref")
    private List<NFrefDTO> NFref;

}
