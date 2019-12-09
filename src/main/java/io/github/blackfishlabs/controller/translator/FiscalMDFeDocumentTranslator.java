package io.github.blackfishlabs.controller.translator;

import br.indie.fiscal4j.DFAmbiente;
import br.indie.fiscal4j.DFUnidadeFederativa;
import br.indie.fiscal4j.mdfe3.MDFeConfig;
import br.indie.fiscal4j.mdfe3.classes.def.*;
import br.indie.fiscal4j.mdfe3.classes.lote.envio.MDFEnvioLote;
import br.indie.fiscal4j.mdfe3.classes.lote.envio.MDFEnvioLoteRetornoDados;
import br.indie.fiscal4j.mdfe3.classes.nota.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.github.blackfishlabs.common.helper.DateHelper;
import io.github.blackfishlabs.common.helper.FiscalHelper;
import io.github.blackfishlabs.controller.dto.FiscalMDFeDocumentDTO;
import io.github.blackfishlabs.controller.dto.mdfe.*;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FiscalMDFeDocumentTranslator implements Translator<FiscalMDFeDocumentDTO, MDFEnvioLote, MDFEnvioLoteRetornoDados> {

    @Override
    public MDFEnvioLote fromDTO(FiscalMDFeDocumentDTO dto) {
        MDFEnvioLote lot = new MDFEnvioLote();

        lot.setIdLote(Long.toString(DateTime.now().getMillis()));
        lot.setMdfe(getMDFe(dto));
        lot.setVersao(MDFeConfig.VERSAO);

        return lot;
    }

    private static MDFe getMDFe(FiscalMDFeDocumentDTO dto) {
        MDFe mdFe = new MDFe();

        mdFe.setInfo(getInfo(dto));

        return mdFe;
    }

    private static MDFInfo getInfo(FiscalMDFeDocumentDTO dto) {
        MDFInfo info = new MDFInfo();

        info.setAutorizacaoDownload(null);
        info.setEmitente(getEmitente(dto.getEmit()));

        dto.getIde().setCMDF(StringUtils.leftPad(FiscalHelper.generateCNF(), 8, '0'));

        info.setIdentificador(getCalculateID(dto));
        info.setIdentificacao(getIdentificacao(dto.getIde(), info.getIdentificador()));
        info.setInformacoesAdicionais(getInformacoesAdicionais(dto.getInfAdic()));
        info.setInformacoesDocumentos(getInformacaoDocumentos(dto.getInfDoc()));
        info.setInfoTotal(getInformacaoTotal(dto.getTot()));
        info.setLacres(null);
        info.setMdfInfoModal(getInformacaoModal(dto.getInfModal()));
        if (Objects.nonNull(dto.getSeg()))
            info.setSeguro(getSeguro(dto.getSeg()));
        info.setVersao(MDFeConfig.VERSAO);
        return info;
    }

    private static List<MDFInfoSeguro> getSeguro(List<SegDTO> dtos) {
        List<MDFInfoSeguro> seguros = Lists.newArrayList();
        for (SegDTO dto : dtos) {
            MDFInfoSeguro seguro = new MDFInfoSeguro();

            seguro.setApolice(dto.getNApol());
            //seguro.setAverbacao(dto.getNAver());
            seguro.setInfo(getInfSeguro(dto.getInfSeg()));
            seguro.setResponsavelSeguro(getResponsavelSeguro(dto.getInfResp()));

            seguros.add(seguro);
        }

        return seguros;
    }

    private static MDFInfoSeguroResponsavel getResponsavelSeguro(InfRespDTO dto) {
        MDFInfoSeguroResponsavel responsavel = new MDFInfoSeguroResponsavel();

        responsavel.setCnpj(dto.getCNPJ());
        responsavel.setCpf(dto.getCPF());
        responsavel.setResponsavelSeguro(MDFTipoResponsavelSeguro.valueOfCodigo(dto.getRespSeg()));

        return responsavel;
    }

    private static MDFInfoSeguroInfo getInfSeguro(InfSegDTO dto) {
        MDFInfoSeguroInfo infoSeguro = new MDFInfoSeguroInfo();

        infoSeguro.setCnpj(dto.getCNPJ());
        infoSeguro.setSeguradora(dto.getXSeg());

        return infoSeguro;

    }

    private static MDFInfoModal getInformacaoModal(InfModalMDFeDTO dto) {
        MDFInfoModal info = new MDFInfoModal();

        info.setRodoviario(getInformacaoRodo(dto.getRodo()));
        info.setVersao(MDFeConfig.VERSAO);

        return info;

    }

    private static MDFInfoModalRodoviario getInformacaoRodo(RodoDTO dto) {
        MDFInfoModalRodoviario info = new MDFInfoModalRodoviario();

        info.setCodAgPorto(dto.getCodAgPorto());
        info.setLacres(null);
        if (Objects.nonNull(dto.getInfANTT()))
            info.setMdfInfoModalRodoviarioANTT(getInformacaoModalRodoviario(dto.getInfANTT()));
        info.setVeiculoReboques(Lists.newArrayList());
        info.setVeiculoTracao(getVeiculoTracao(dto.getVeicTracao()));

        return info;

    }

    private static MDFInfoModalRodoviarioVeiculoTracao getVeiculoTracao(VeicTracaoDTO dto) {
        MDFInfoModalRodoviarioVeiculoTracao veiculoTracao = new MDFInfoModalRodoviarioVeiculoTracao();

        veiculoTracao.setCapacidadeKG(dto.getCapKG());
        veiculoTracao.setCapacidadeM3(dto.getCapM3());
        //veiculoTracao.setCodigoInterno(null);
        veiculoTracao.setCondutor(Collections.singletonList(getCondutor(dto.getCondutor())));
        veiculoTracao.setPlaca(dto.getPlaca());
        //veiculoTracao.setRenavam("");
        veiculoTracao.setTara(dto.getTara());
        veiculoTracao.setTipoRodado(MDFTipoRodado.valueOfCodigo(dto.getTpRod()));
        veiculoTracao.setTipoCarroceria(MDFTipoCarroceria.valueOfCodigo(dto.getTpCar()));
        veiculoTracao.setUnidadeFederativa(DFUnidadeFederativa.valueOfCodigo(dto.getUF()));
        if (Objects.nonNull(dto.getProp()))
            veiculoTracao.setProprietario(getProprietario(dto.getProp()));

        return veiculoTracao;
    }

    private static MDFInfoModalRodoviarioVeiculoProp getProprietario(PropDTO dto) {
        MDFInfoModalRodoviarioVeiculoProp proprietario = new MDFInfoModalRodoviarioVeiculoProp();

        if (Objects.nonNull(dto.getCNPJ()))
            proprietario.setCnpj(dto.getCNPJ());
        if (Objects.nonNull(dto.getCPF()))
            proprietario.setCpf(dto.getCPF());
        if (Objects.nonNull(dto.getIE()))
            proprietario.setInscricaoEstadual(dto.getIE());
        proprietario.setRazaoSocial(dto.getXNome());
        if (Objects.nonNull(dto.getRNTRC()))
            proprietario.setRegistroNacionalTransportes(dto.getRNTRC());
        proprietario.setTipoProprietario(MDFTipoProprietario.valueOfCodigo(dto.getTpProp()));
        proprietario.setUnidadeFederativa(DFUnidadeFederativa.valueOfCodigo(dto.getUF()));

        return proprietario;
    }

    private static MDFInfoModalRodoviarioVeiculoCondutor getCondutor(CondutorDTO dto) {
        MDFInfoModalRodoviarioVeiculoCondutor condutor = new MDFInfoModalRodoviarioVeiculoCondutor();

        condutor.setCpf(dto.getCPF());
        condutor.setNomeCondutor(dto.getXNome());

        return condutor;
    }

    private static MDFInfoModalRodoviarioANTT getInformacaoModalRodoviario(InfANTTDTO dto) {
        MDFInfoModalRodoviarioANTT info = new MDFInfoModalRodoviarioANTT();

//      info.setInfCIOT(null);
//      info.setInfContratante(null);
        info.setRntrc(dto.getRNTRC());
//      info.setValePedagio(null);

        return info;
    }

    private static String getCalculateID(FiscalMDFeDocumentDTO dto) {
        StringBuilder key = new StringBuilder();

        key.append(StringUtils.leftPad(dto.getIde().getCUF(), 2, "0"));
        key.append(StringUtils.leftPad(DateTime.parse(dto.getIde().getDhEmi()).toString("yyMM"), 4, "0"));
        key.append(StringUtils.leftPad(dto.getEmit().getCNPJ().replaceAll("\\D", ""), 14, "0"));
        key.append(StringUtils.leftPad(dto.getIde().getMod(), 2, "0"));
        key.append(StringUtils.leftPad(dto.getIde().getSerie() + "", 3, "0"));
        key.append(StringUtils.leftPad(dto.getIde().getNMDF() + "", 9, "0"));
        key.append(StringUtils.leftPad(dto.getIde().getTpEmis(), 1, "0"));
        key.append(StringUtils.leftPad(dto.getIde().getCMDF(), 8, "0"));
        key.append(FiscalHelper.modulo11(key.toString()));

        return key.toString();
    }

    private static MDFInfoTotal getInformacaoTotal(TotalMDFeDTO dto) {
        MDFInfoTotal info = new MDFInfoTotal();

        info.setPesoCarga(new BigDecimal(dto.getQCarga()));
        info.setQtdeCTe(null);
        info.setQtdeMDFe(null);
        info.setQtdeNFe(dto.getQNFe());
        info.setUnidadeMedidaPesoBrutoCarga(MDFUnidadeMedidaPesoBrutoCarga.valueOfCodigo(dto.getCUnid()));
        info.setValorTotalCarga(new BigDecimal(dto.getVCarga()));

        return info;
    }

    private static MDFInfoInformacoesDocumentos getInformacaoDocumentos(InfDocDTO dto) {
        MDFInfoInformacoesDocumentos info = new MDFInfoInformacoesDocumentos();

        info.setInformacoesMunicipioDescargas(Collections.singletonList(getInformacaoUFDescarga(dto.getInfMunDescarga())));

        return info;
    }

    private static MDFInfoInformacoesMunicipioDescarga getInformacaoUFDescarga(InfMunDescargaDTO dto) {
        MDFInfoInformacoesMunicipioDescarga info = new MDFInfoInformacoesMunicipioDescarga();

        info.setMunicipioDescarga(dto.getCMunDescarga());
        info.setxMunDescarga(dto.getXMunDescarga());
        info.setInfNFe(getInformacaoNFe(dto.getInfNFe()));
        info.setInfMDFeTransp(Lists.newArrayList());
        info.setInfCTe(Lists.newArrayList());

        return info;
    }

    private static List<MDFInfoInformacoesNFe> getInformacaoNFe(List<InfNFeDTO> dto) {
        List<MDFInfoInformacoesNFe> infNFe = Lists.newArrayList();

        dto.forEach(nfe -> {
            MDFInfoInformacoesNFe info = new MDFInfoInformacoesNFe();
            info.setChaveNFe(nfe.getChNFe());

            infNFe.add(info);
        });

        return infNFe;
    }

    private static MDFInfoInformacoesAdicionais getInformacoesAdicionais(InfAdicMDFeDTO dto) {
        MDFInfoInformacoesAdicionais info = new MDFInfoInformacoesAdicionais();

        if (!Strings.isNullOrEmpty(dto.getInfAdFisco()))
            info.setInformacoesAdicionaisInteresseFisco(dto.getInfAdFisco());

        if (!Strings.isNullOrEmpty(dto.getInfCpl()))
            info.setInformacoesComplementaresInteresseContribuinte(dto.getInfCpl());

        return info;
    }

    private static MDFInfoIdentificacao getIdentificacao(IdeMDFeDTO dto, String id) {
        MDFInfoIdentificacao ide = new MDFInfoIdentificacao();

        ide.setCodigoUF(DFUnidadeFederativa.valueOfCodigo(dto.getCUF()));
        ide.setAmbiente(DFAmbiente.valueOfCodigo(dto.getTpAmb()));
        ide.setTipoEmitente(MDFTipoEmitente.valueOfCodigo(dto.getTpEmit()));
        ide.setTipoTranportador(null);
        ide.setSerie(Integer.parseInt(dto.getSerie()));
        ide.setNumero(Integer.parseInt(dto.getNMDF()));
        ide.setCodigoNumerico(dto.getCMDF());
        ide.setDigitoVerificador(Integer.parseInt(FiscalHelper.calculateCDV(id)));
        ide.setModalidadeFrete(MDFModalidadeTransporte.valueOfCodigo(dto.getModal()));
        ide.setDataEmissao(DateHelper.toZonedDateTime(new DateTime(DateTimeZone.UTC).toDate()));
        ide.setTipoEmissao(MDFTipoEmissao.valueOfCodigo(dto.getTpEmis()));
        ide.setProcessoEmissao(MDFProcessoEmissao.valueOfCodigo(dto.getProcEmi()));
        ide.setVersaoProcessoEmissao(dto.getVerProc());
        ide.setUnidadeFederativaInicio(DFUnidadeFederativa.valueOfCodigo(dto.getUFIni()));
        ide.setUnidadeFederativaFim(DFUnidadeFederativa.valueOfCodigo(dto.getUFFim()));
        ide.setMunicipioCarregamentos(Collections.singletonList(getUFCarregamentos(dto.getInfMunCarrega())));
        ide.setIdentificacaoUfPercursos(getUFPercurso(dto.getInfPercurso()));
        ide.setDataHoraDoInicioViagem(null);

        return ide;
    }

    private static List<MDFInfoIdentificacaoUfPercurso> getUFPercurso(List<InfPercursoDTO> infPercurso) {
        List<MDFInfoIdentificacaoUfPercurso> percursoList = Lists.newArrayList();

        infPercurso.forEach(info -> {
            MDFInfoIdentificacaoUfPercurso percurso = new MDFInfoIdentificacaoUfPercurso();
            percurso.setUfPercurso(DFUnidadeFederativa.valueOfCodigo(info.getUFPer()));

            percursoList.add(percurso);
        });

        return percursoList;
    }

    private static MDFInfoIdentificacaoMunicipioCarregamento getUFCarregamentos(InfMunCarregaDTO dto) {
        MDFInfoIdentificacaoMunicipioCarregamento carregamento = new MDFInfoIdentificacaoMunicipioCarregamento();

        carregamento.setCodigoMunicipioCarregamento(dto.getCMunCarrega());
        carregamento.setNomeMunicipioCarregamento(dto.getCMunCarrega());

        return carregamento;
    }

    private static MDFInfoEmitente getEmitente(EmitMDFeDTO dto) {
        MDFInfoEmitente emitente = new MDFInfoEmitente();

        emitente.setCnpj(dto.getCNPJ());
        emitente.setEndereco(getEndereco(dto.getEnderEmit()));
        emitente.setInscricaoEstadual(dto.getIE());
        emitente.setNomeFantasia(dto.getXFant());
        emitente.setRazaoSocial(dto.getXNome());

        return emitente;
    }

    private static MDFInfoEmitenteEndereco getEndereco(EnderEmitMDFeDTO dto) {
        MDFInfoEmitenteEndereco endereco = new MDFInfoEmitenteEndereco();

        endereco.setBairro(dto.getXBairro());
        endereco.setCep(dto.getCEP());
        endereco.setCodigoMunicipio(dto.getCMun());
        endereco.setComplemento(null);
        endereco.setDescricaoMunicipio(dto.getXMun());
        if (!Strings.isNullOrEmpty(dto.getEmail()))
            endereco.setEmail(dto.getEmail());
        endereco.setLogradouro(dto.getXLgr());
        endereco.setNumero(dto.getNro());
        endereco.setSiglaUF(dto.getUF());
        endereco.setTelefone(dto.getFone());

        return endereco;
    }

    @Override
    public String response(MDFEnvioLoteRetornoDados result) {
        return "Ambiente: " + result.getRetorno().getAmbiente() +
                "\n" +
                "UF: " + result.getRetorno().getUf() +
                "\n" +
                "Data Recibo: " + result.getRetorno().getInfoRecebimento().getDataRecibo() +
                "\n" +
                "Status: " + result.getRetorno().getStatus() + " - " + result.getRetorno().getMotivo() +
                "\n" +
                "Numero Recibo: " + result.getRetorno().getInfoRecebimento().getNumeroRecibo();
    }
}
