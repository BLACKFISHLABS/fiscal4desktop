package io.github.blackfishlabs.fiscal4desktop.controller.translator;

import br.indie.fiscal4j.DFAmbiente;
import br.indie.fiscal4j.DFModelo;
import br.indie.fiscal4j.DFUnidadeFederativa;
import br.indie.fiscal4j.nfe.NFTipoEmissao;
import br.indie.fiscal4j.nfe400.classes.*;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvio;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvioRetornoDados;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteIndicadorProcessamento;
import br.indie.fiscal4j.nfe400.classes.nota.*;
import com.google.common.collect.Lists;
import io.github.blackfishlabs.fiscal4desktop.common.helper.DateHelper;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalHelper;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.FiscalDocumentDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.document.*;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.document.cofins.COFINSAliqDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.document.cofins.COFINSDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.document.cofins.COFINSNTDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.document.cofins.COFINSOutrDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.document.icms.*;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.document.ipi.IPIDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.document.ipi.IPITribDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.document.pis.PISAliqDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.document.pis.PISDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.document.pis.PISNTDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.document.pis.PISOutrDTO;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class FiscalDocumentTranslator implements Translator<FiscalDocumentDTO, NFLoteEnvio, NFLoteEnvioRetornoDados> {

    public static final String VERSION_NFE = "4.00";

    public static NFNota getNFNota(FiscalDocumentDTO dto) {
        final NFNota invoice = new NFNota();
        invoice.setInfo(getNFNotaInfo(dto));
        return invoice;
    }

    public static NFNotaInfo getNFNotaInfo(FiscalDocumentDTO dto) {
        final NFNotaInfo info = new NFNotaInfo();
        info.setVersao(new BigDecimal(VERSION_NFE));
        info.setIdentificador(getCalculateID(dto));

        if (nonNull(dto.getCobr()))
            info.setCobranca(getNFNotaInfoCobranca(dto.getCobr()));
        if (nonNull(dto.getDest()))
            info.setDestinatario(getNFNotaInfoDestinatario(dto.getDest()));
        if (nonNull(dto.getEmit()))
            info.setEmitente(getNFNotaInfoEmitente(dto.getEmit()));
        if (nonNull(dto.getPag()))
            info.setPagamentos(Collections.singletonList(getNFNotaInfoPagamento(dto.getPag())));
        if (nonNull(dto.getIde()))
            info.setIdentificacao(getNFNotaInfoIdentificacao(dto.getIde(), info.getIdentificador()));
        if (nonNull(dto.getInfAdic()))
            info.setInformacoesAdicionais(getNFNotaInfoInformacoesAdicionais(dto.getInfAdic()));
        if (nonNull(dto.getDet()) && !dto.getDet().isEmpty()) {
            List<NFNotaInfoItem> items = Lists.newArrayList();

            for (DetDTO detDTO : dto.getDet()) {
                items.add(getNFNotaInfoItem(detDTO));
            }

            info.setItens(items);
        }
        if (nonNull(dto.getTotal()))
            info.setTotal(getNFNotaInfoTotal(dto.getTotal()));
        if (nonNull(dto.getTransp()))
            info.setTransporte(getNFNotaInfoTransporte(dto.getTransp()));
        //if (nonNull(dto.getEmit()))
        //    info.setPessoasAutorizadasDownloadNFe(Collections.singletonList(getPessoaAutorizadaDownloadNFe(dto.getEmit())));
        //        info.setAvulsa(getNFNotaInfoAvulsa());
        //        info.setCana(getNFNotaInfoCana());
        //        info.setCompra(getNFNotaInfoCompra());
        //        info.setEntrega(getNFNotaInfoLocal());
        //        info.setExportacao(getNFNotaInfoExportacao());
        //        info.setIdentificador("89172658591754401086218048846976493475937081");
        //        info.setRetirada(getNFNotaInfoLocal());


        NFNotaInfoResponsavelTecnico responsavelTecnico = new NFNotaInfoResponsavelTecnico();
        responsavelTecnico.setCnpj("07609963000136");
        responsavelTecnico.setContatoNome("Jeferson Cruz");
        responsavelTecnico.setEmail("dev.blackfishlabs@gmail.com");
        responsavelTecnico.setTelefone("65996168022");

        info.setInformacaoResposavelTecnico(responsavelTecnico);

        return info;
    }

    public static String getCalculateID(FiscalDocumentDTO dto) {
        StringBuilder key = new StringBuilder();

        key.append(StringUtils.leftPad(dto.getIde().getCUF(), 2, "0"));
        key.append(StringUtils.leftPad(new DateTime(dto.getIde().getDhEmi()).toString("yyMM"), 4, "0"));
        key.append(StringUtils.leftPad(dto.getEmit().getCNPJ().replaceAll("\\D", ""), 14, "0"));
        key.append(StringUtils.leftPad(dto.getIde().getMod(), 2, "0"));
        key.append(StringUtils.leftPad(dto.getIde().getSerie(), 3, "0"));
        key.append(StringUtils.leftPad(String.valueOf(dto.getIde().getNNF()), 9, "0"));
        key.append(StringUtils.leftPad(dto.getIde().getTpEmis(), 1, "0"));
        key.append(StringUtils.leftPad(dto.getIde().getCNF(), 8, "0"));
        key.append(FiscalHelper.modulo11(key.toString()));

        return key.toString();
    }

    public static NFNotaInfoTotal getNFNotaInfoTotal(TotalDTO dto) {
        final NFNotaInfoTotal total = new NFNotaInfoTotal();

        if (nonNull(dto.getICMSTot())) {
            total.setIcmsTotal(getNFNotaInfoICMSTotal(dto.getICMSTot()));
        }

//        total.setIssqnTotal(getNFNotaInfoISSQNTotal());
//        total.setRetencoesTributos(getNFNotaInfoRetencoesTributos());

        return total;
    }

//    public static NFPessoaAutorizadaDownloadNFe getPessoaAutorizadaDownloadNFe(EmitDTO dto) {
//        final NFPessoaAutorizadaDownloadNFe pessoa = new NFPessoaAutorizadaDownloadNFe();
//        pessoa.setCnpj(dto.getCNPJ());
//        return pessoa;
//    }

    public static NFNotaInfoICMSTotal getNFNotaInfoICMSTotal(ICMSTotDTO dto) {
        final NFNotaInfoICMSTotal icmsTotal = new NFNotaInfoICMSTotal();

        if (!isNullOrEmpty(dto.getVBC()))
            icmsTotal.setBaseCalculoICMS(new BigDecimal(dto.getVBC()));
        if (!isNullOrEmpty(dto.getVOutro()))
            icmsTotal.setOutrasDespesasAcessorias(new BigDecimal(dto.getVOutro()));
        if (!isNullOrEmpty(dto.getVBCST()))
            icmsTotal.setBaseCalculoICMSST(new BigDecimal(dto.getVBCST()));
        if (!isNullOrEmpty(dto.getVCOFINS()))
            icmsTotal.setValorCOFINS(new BigDecimal(dto.getVCOFINS()));
        if (!isNullOrEmpty(dto.getVPIS()))
            icmsTotal.setValorPIS(new BigDecimal(dto.getVPIS()));
        if (!isNullOrEmpty(dto.getVDesc()))
            icmsTotal.setValorTotalDesconto(new BigDecimal(dto.getVDesc()));
        if (!isNullOrEmpty(dto.getVProd()))
            icmsTotal.setValorTotalDosProdutosServicos(new BigDecimal(dto.getVProd()));
        if (!isNullOrEmpty(dto.getVFrete()))
            icmsTotal.setValorTotalFrete(new BigDecimal(dto.getVFrete()));
        if (!isNullOrEmpty(dto.getVICMS()))
            icmsTotal.setValorTotalICMS(new BigDecimal(dto.getVICMS()));
        if (!isNullOrEmpty(dto.getVST()))
            icmsTotal.setValorTotalICMSST(new BigDecimal(dto.getVST()));
        if (!isNullOrEmpty(dto.getVII()))
            icmsTotal.setValorTotalII(new BigDecimal(dto.getVII()));
        if (!isNullOrEmpty(dto.getVIPI()))
            icmsTotal.setValorTotalIPI(new BigDecimal(dto.getVIPI()));
        if (!isNullOrEmpty(dto.getVIPIDevol()))
            icmsTotal.setValorTotalIPIDevolvido(new BigDecimal(dto.getVIPIDevol()));
        if (!isNullOrEmpty(dto.getVNF()))
            icmsTotal.setValorTotalNFe(new BigDecimal(dto.getVNF()));
        if (!isNullOrEmpty(dto.getVSeg()))
            icmsTotal.setValorTotalSeguro(new BigDecimal(dto.getVSeg()));
        if (!isNullOrEmpty(dto.getVICMSDeson()))
            icmsTotal.setValorICMSDesonerado(new BigDecimal(dto.getVICMSDeson()));
        if (!isNullOrEmpty(dto.getVFCPUFDest()))
            icmsTotal.setValorICMSFundoCombatePobreza(new BigDecimal(dto.getVFCPUFDest()));
        if (!isNullOrEmpty(dto.getVICMSUFDest()))
            icmsTotal.setValorICMSPartilhaDestinatario(new BigDecimal(dto.getVICMSUFDest()));
        if (!isNullOrEmpty(dto.getVICMSUFRemet()))
            icmsTotal.setValorICMSPartilhaRementente(new BigDecimal(dto.getVICMSUFRemet()));
        if (!isNullOrEmpty(dto.getVFCP()))
            icmsTotal.setValorTotalFundoCombatePobreza(new BigDecimal(dto.getVFCP()));
        if (!isNullOrEmpty(dto.getVFCPST()))
            icmsTotal.setValorTotalFundoCombatePobrezaST(new BigDecimal(dto.getVFCPST()));
        if (!isNullOrEmpty(dto.getVFCPSTRet()))
            icmsTotal.setValorTotalFundoCombatePobrezaSTRetido(new BigDecimal(dto.getVFCPSTRet()));
        if (!isNullOrEmpty(dto.getVTotTrib()))
            icmsTotal.setValorTotalTributos(new BigDecimal(dto.getVTotTrib()));

        return icmsTotal;
    }

//    public static NFNotaInfoRetencoesTributos getNFNotaInfoRetencoesTributos() {
//        final NFNotaInfoRetencoesTributos retencoesTributos = new NFNotaInfoRetencoesTributos();
//        retencoesTributos.setBaseCalculoIRRF(new BigDecimal("999999999999.99"));
//        retencoesTributos.setBaseCalculoRetencaoPrevidenciaSocial(new BigDecimal("999999999999.99"));
//        retencoesTributos.setValorRetencaoPrevidenciaSocial(new BigDecimal("999999999999.99"));
//        retencoesTributos.setValorRetidoCOFINS(new BigDecimal("999999999999.99"));
//        retencoesTributos.setValorRetidoCSLL(new BigDecimal("999999999999.99"));
//        retencoesTributos.setValorRetidoIRRF(new BigDecimal("999999999999.99"));
//        retencoesTributos.setValorRetidoPIS(new BigDecimal("999999999999.99"));
//        return retencoesTributos;
//    }
//
//    public static NFNotaInfoISSQNTotal getNFNotaInfoISSQNTotal() {
//        final NFNotaInfoISSQNTotal issqnTotal = new NFNotaInfoISSQNTotal();
//        issqnTotal.setBaseCalculoISS(new BigDecimal("999999999999.99"));
//        issqnTotal.setValorCOFINSsobreServicos(new BigDecimal("999999999999.99"));
//        issqnTotal.setValorPISsobreServicos(new BigDecimal("999999999999.99"));
//        issqnTotal.setValorTotalISS(new BigDecimal("999999999999.99"));
//        issqnTotal.setValorTotalServicosSobNaoIncidenciaNaoTributadosICMS(new BigDecimal("999999999999.99"));
//        issqnTotal.setDataPrestacaoServico(new LocalDate(2014, 1, 1));
//        issqnTotal.setValorDeducao(new BigDecimal("999999999999.99"));
//        issqnTotal.setValorOutros(new BigDecimal("999999999999.99"));
//        issqnTotal.setValorTotalDescontoIncondicionado(new BigDecimal("999999999999.99"));
//        issqnTotal.setValorTotalDescontoCondicionado(new BigDecimal("999999999999.99"));
//        issqnTotal.setValorTotalRetencaoISS(new BigDecimal("999999999999.99"));
//        issqnTotal.setTributacao(NFNotaInfoRegimeEspecialTributacao.SOCIEDADE_PROFISSIONAIS);
//        return issqnTotal;
//    }

    public static NFNotaInfoTransporte getNFNotaInfoTransporte(TranspDTO dto) {
        final NFNotaInfoTransporte transporte = new NFNotaInfoTransporte();

        //transporte.setIcmsTransporte(getNFNotaInfoRetencaoICMSTransporte());

        transporte.setModalidadeFrete(NFModalidadeFrete.valueOfCodigo(dto.getModFrete()));

        if (nonNull(dto.getTransporta())) {
            transporte.setTransportador(getNFNotaInfoTransportador(dto.getTransporta()));
        }

        if (nonNull(dto.getVol())) {
            transporte.setVolumes(Collections.singletonList(getNFNotaInfoVolume(dto.getVol())));
        }

        if (nonNull(dto.getVeicTransp())) {
            transporte.setVeiculo(getNFNotaInfoVeiculo(dto.getVeicTransp()));
        }

        return transporte;
    }

    public static NFNotaInfoTransportador getNFNotaInfoTransportador(TransportaDTO dto) {
        final NFNotaInfoTransportador transportador = new NFNotaInfoTransportador();

        transportador.setCnpj(dto.getCNPJ());
        transportador.setEnderecoComplemento(dto.getXEnder());
        transportador.setInscricaoEstadual(dto.getIE());
        transportador.setNomeMunicipio(dto.getXMun());
        transportador.setRazaoSocial(dto.getXNome());
        transportador.setUf(DFUnidadeFederativa.valueOfCodigo(dto.getUF()));

        return transportador;
    }

    public static NFNotaInfoVolume getNFNotaInfoVolume(VolDTO dto) {
        final NFNotaInfoVolume volume = new NFNotaInfoVolume();
        volume.setEspecieVolumesTransportados(dto.getEsp());

//        final NFNotaInfoLacre notaInfoLacre = new NFNotaInfoLacre();
//        notaInfoLacre.setNumeroLacre("gvmjb9BB2cmwsLbzeR3Bsk8QbA7b1XEgXUhKeS9QZGiwhFnqDtEzS3377MP2");
//        volume.setLacres(Collections.singletonList(notaInfoLacre));
//
//        volume.setMarca("lc0w13Xw2PxsSD4u4q3N6Qix9ZuCFm0HXo6BxBmKnjVbh9Xwy3k9UwBNfuYo");
//        volume.setNumeracaoVolumesTransportados("mcBUtZwnI5DKj2YZNAcLP7W9h6j1xKmF5SX1BTKmsvyg0H5xSrfVw8HGn8eb");
        volume.setPesoBruto(new BigDecimal(dto.getPesoB()));
        volume.setPesoLiquido(new BigDecimal(dto.getPesoL()));
        volume.setQuantidadeVolumesTransportados(new BigInteger(dto.getQVol()));

        return volume;
    }

    public static NFNotaInfoVeiculo getNFNotaInfoVeiculo(VeicTranspDTO dto) {
        final NFNotaInfoVeiculo veiculo = new NFNotaInfoVeiculo();

        veiculo.setPlacaVeiculo(dto.getPlaca());
        veiculo.setUf(DFUnidadeFederativa.valueOfCodigo(dto.getUF()));

        return veiculo;
    }

    public static NFNotaInfoCobranca getNFNotaInfoCobranca(CobrDTO dto) {
        final NFNotaInfoCobranca cob = new NFNotaInfoCobranca();
        cob.setFatura(getNFNotaInfoFatura(dto.getFat()));

        List<NFNotaInfoParcela> dup = Lists.newArrayList();

        if (nonNull(dto.getDup()) && !(dto.getDup().isEmpty())) {
            for (DupDTO dupDTO : dto.getDup()) {
                dup.add(getNFNotaInfoDuplicata(dupDTO));
            }

            cob.setParcelas(dup);
        }
        return cob;
    }
//
//    public static NFNotaInfoRetencaoICMSTransporte getNFNotaInfoRetencaoICMSTransporte() {
//        final NFNotaInfoRetencaoICMSTransporte retencaoICMSTransporte = new NFNotaInfoRetencaoICMSTransporte();
//        retencaoICMSTransporte.setAliquotaRetencao(new BigDecimal("99.99"));
//        retencaoICMSTransporte.setBcRetencaoICMS(new BigDecimal("999999999999.99"));
//        retencaoICMSTransporte.setCfop(5351);
//        retencaoICMSTransporte.setCodigoMunicipioOcorrenciaFatoGeradorICMSTransporte("9999999");
//        retencaoICMSTransporte.setValorICMSRetido(new BigDecimal("999999999999.99"));
//        retencaoICMSTransporte.setValorServico(new BigDecimal("999999999999.99"));
//        return retencaoICMSTransporte;
//    }

//    public static NFNotaInfoAvulsa getNFNotaInfoAvulsa() {
//        final NFNotaInfoAvulsa avulsa = new NFNotaInfoAvulsa();
//        avulsa.setCnpj("12345678901234");
//        avulsa.setDataEmissaoDocumentoArrecadacao(new LocalDate(2014, 1, 13));
//        avulsa.setDataPagamentoDocumentoArrecadacao(new LocalDate(2014, 3, 21));
//        avulsa.setFone("81579357");
//        avulsa.setMatriculaAgente("Nn5PPREBbkfmmk4lBFwgvkuKg8prnY5CPqHIzqGiD1lTnZJ37nAZ4NBc8XwM");
//        avulsa.setNomeAgente("lkLip3hIYSAIzH3Tf1LWQsaybqB76V66lMgWBcHVwcOKInuJ8mGUyY8DT4NL");
//        avulsa.setNumeroDocumentoArrecadacaoReceita("qqDt1f1ulcahrBnUH0otPFkjYqD2tH4ktYsR71WSYZLFW1zZObAqajHHkyxi");
//        avulsa.setOrgaoEmitente("qNre0x2eJthUYIoKBuBbbGSeA4R2wrDLxNwCuDFkYD54flBLbBBMakGDgQUV");
//        avulsa.setReparticaoFiscalEmitente("YQFmDI2HBjjfZpRjR2ghwmSo1oWk5QgUEYf2oG46uEHwY4zsXyH1ORSr8oq3");
//        avulsa.setUf(NFUnidadeFederativa.PR);
//        avulsa.setValorTotalConstanteDocumentoArrecadacaoReceita(new BigDecimal("999999999999.99"));
//        return avulsa;
//    }

//    public static NFNotaInfoCana getNFNotaInfoCana() {
//        final NFNotaInfoCana infoCana = new NFNotaInfoCana();
//        infoCana.setDeducoes(new ArrayList<>());
//        final ArrayList<NFNotaInfoCanaFornecimentoDiario> infosCanaFornecimentoDario = new ArrayList<>();
//        infosCanaFornecimentoDario.add(getNFNotaInfoCanaFornecimentoDiario());
//        infoCana.setFornecimentosDiario(infosCanaFornecimentoDario);
//        infoCana.setReferencia("06/2013");
//        infoCana.setSafra("2013/2014");
//        infoCana.setValorFornecimento(new BigDecimal("900"));
//        infoCana.setValorLiquidoFornecimento(new BigDecimal("980"));
//        infoCana.setValorTotalDeducao(new BigDecimal("2000.70"));
//        infoCana.setQuantidadeTotalAnterior(new BigDecimal("10"));
//        infoCana.setQuantidadeTotalGeral(new BigDecimal("80"));
//        infoCana.setQuantidadeTotalMes(new BigDecimal("30.0000001"));
//
//        return infoCana;
//    }

//    public static NFNotaInfoCanaFornecimentoDiario getNFNotaInfoCanaFornecimentoDiario() {
//        final NFNotaInfoCanaFornecimentoDiario canaFornecimentoDiario = new NFNotaInfoCanaFornecimentoDiario();
//        canaFornecimentoDiario.setDia(15);
//        canaFornecimentoDiario.setQuantidade(new BigDecimal("3"));
//        return canaFornecimentoDiario;
//    }

    public static NFNotaInfoFatura getNFNotaInfoFatura(FatDTO dto) {
        final NFNotaInfoFatura fatura = new NFNotaInfoFatura();

        fatura.setNumeroFatura(dto.getNFat());
        if (!isNullOrEmpty(dto.getVDesc())) {
            fatura.setValorDesconto(new BigDecimal(dto.getVDesc()));
        }
        fatura.setValorLiquidoFatura(new BigDecimal(dto.getVLiq()));
        fatura.setValorOriginalFatura(new BigDecimal(dto.getVOrig()));

        return fatura;
    }

    public static NFNotaInfoParcela getNFNotaInfoDuplicata(DupDTO dto) {
        final NFNotaInfoParcela duplicata = new NFNotaInfoParcela();

        duplicata.setDataVencimento(DateHelper.toLocalDate(DateTime.parse(dto.getNVenc())));
        duplicata.setNumeroParcela(dto.getNDup());
        duplicata.setValorParcela(new BigDecimal(dto.getVDup()));

        return duplicata;
    }

    public static NFNotaInfoDestinatario getNFNotaInfoDestinatario(DestDTO dto) {
        final NFNotaInfoDestinatario destinatario = new NFNotaInfoDestinatario();

        if (!isNullOrEmpty(dto.getCNPJ())) {
            destinatario.setCnpj(dto.getCNPJ());
        } else {
            destinatario.setCpf(dto.getCPF());
        }

        destinatario.setEmail(isNullOrEmpty(dto.getEmail()) ? "no@mail.com" : dto.getEmail());
        destinatario.setEndereco(getNFEnderecoDest(dto.getEnderDest()));
        destinatario.setInscricaoEstadual(dto.getIE());
        destinatario.setRazaoSocial(dto.getXNome());
        destinatario.setIndicadorIEDestinatario(NFIndicadorIEDestinatario.valueOfCodigo(dto.getIndIEDest()));
        // destinatario.setInscricaoMunicipal("5ow5E1mZQPe1VUR");
        // destinatario.setInscricaoSuframa("999999999");

        return destinatario;
    }

//    public static NFNotaInfoCompra getNFNotaInfoCompra() {
//        final NFNotaInfoCompra compra = new NFNotaInfoCompra();
//        compra.setContrato("9tQtearTIcXmO9vxNr3TPhSaItw5mk3zyTVlf2aIFXqqvtXrHoa0qPWKzUzc");
//        compra.setNotaDeEmpenho("abcefghijklmnopqrstuvx");
//        compra.setPedido("1kG8gghJ0YTrUZnt00BJlOsFCtj43eV5mEHHXUzp3rD6QwwUwX4GPavXkMB1");
//        return compra;
//    }

    public static NFEndereco getNFEnderecoDest(EnderDestDTO dto) {
        final NFEndereco endereco = new NFEndereco();

        if (!isNullOrEmpty(dto.getXBairro()))
            endereco.setBairro(dto.getXBairro());
        if (!isNullOrEmpty(dto.getCEP()))
            endereco.setCep(dto.getCEP());
        if (!isNullOrEmpty(dto.getCMun()))
            endereco.setCodigoMunicipio(dto.getCMun());
        if (!isNullOrEmpty(dto.getCPais()))
            endereco.setCodigoPais(dto.getCPais());
        if (!isNullOrEmpty(dto.getXCpl()))
            endereco.setComplemento(dto.getXCpl());
        if (!isNullOrEmpty(dto.getXMun()))
            endereco.setDescricaoMunicipio(dto.getXMun());
        if (!isNullOrEmpty(dto.getXLgr()))
            endereco.setLogradouro(dto.getXLgr());
        if (!isNullOrEmpty(dto.getNro()))
            endereco.setNumero(dto.getNro());
        if (!isNullOrEmpty(dto.getFone()))
            endereco.setTelefone(dto.getFone());
        if (!isNullOrEmpty(dto.getUF()))
            endereco.setUf(DFUnidadeFederativa.valueOfCodigo(dto.getUF()));

        return endereco;
    }

    public static NFEndereco getNFEnderecoEmit(EnderEmitDTO dto) {
        final NFEndereco endereco = new NFEndereco();

        if (!isNullOrEmpty(dto.getXBairro()))
            endereco.setBairro(dto.getXBairro());
        if (!isNullOrEmpty(dto.getCEP()))
            endereco.setCep(dto.getCEP());
        if (!isNullOrEmpty(dto.getCMun()))
            endereco.setCodigoMunicipio(dto.getCMun());
        if (!isNullOrEmpty(dto.getCPais()))
            endereco.setCodigoPais(dto.getCPais());
        if (!isNullOrEmpty(dto.getXMun()))
            endereco.setDescricaoMunicipio(dto.getXMun());
        if (!isNullOrEmpty(dto.getXLgr()))
            endereco.setLogradouro(dto.getXLgr());
        if (!isNullOrEmpty(dto.getNro()))
            endereco.setNumero(dto.getNro());
        if (!isNullOrEmpty(dto.getFone()))
            endereco.setTelefone(dto.getFone());
        if (!isNullOrEmpty(dto.getUF()))
            endereco.setUf(DFUnidadeFederativa.valueOfCodigo(dto.getUF()));

        return endereco;
    }

    public static NFNotaInfoEmitente getNFNotaInfoEmitente(EmitDTO dto) {
        final NFNotaInfoEmitente emitente = new NFNotaInfoEmitente();

//        emitente.setClassificacaoNacionalAtividadesEconomicas("0111111")
//        emitente.setInscricaoEstadualSubstituicaoTributaria("84371964648860");
//        emitente.setInscricaoMunicipal("zjfBnFVG8TBq8iW");

        emitente.setCnpj(dto.getCNPJ());
        emitente.setEndereco(getNFEnderecoEmit(dto.getEnderEmit()));
        emitente.setInscricaoEstadual(dto.getIE());
        emitente.setNomeFantasia(dto.getXFant());
        emitente.setRazaoSocial(dto.getXNome());
        emitente.setRegimeTributario(NFRegimeTributario.valueOfCodigo(dto.getCRT()));

        return emitente;
    }

    private static NFNotaInfoPagamento getNFNotaInfoPagamento(PagDTO dto) {
        final NFNotaInfoPagamento pagamento = new NFNotaInfoPagamento();

        pagamento.setDetalhamentoFormasPagamento(getNFNotaInfoFormaPagamento(dto));

        return pagamento;
    }

    private static List<NFNotaInfoFormaPagamento> getNFNotaInfoFormaPagamento(PagDTO dto) {
        List<NFNotaInfoFormaPagamento> formaPagamentos = Lists.newArrayList();

        dto.getDetPag().forEach(pag -> {
            NFNotaInfoFormaPagamento formaPagamento = new NFNotaInfoFormaPagamento();
            formaPagamento.setValorPagamento(new BigDecimal(pag.getVPag()));
            formaPagamento.setMeioPagamento(NFMeioPagamento.valueOfCodigo(pag.getTPag()));

            if (!isNull(pag.getCard()))
                formaPagamento.setCartao(getNFNotaInfoCartao(pag.getCard()));

            formaPagamentos.add(formaPagamento);
        });

        return formaPagamentos;
    }

    private static NFNotaInfoCartao getNFNotaInfoCartao(CardDTO dto) {
        NFNotaInfoCartao cartao = new NFNotaInfoCartao();

        cartao.setCnpj(dto.getCNPJ());
        cartao.setNumeroAutorizacaoOperacaoCartao(dto.getCAut());
        cartao.setOperadoraCartao(NFOperadoraCartao.valueOfCodigo(dto.getTBand()));
        cartao.setTipoIntegracao(NFTipoIntegracaoPagamento.valueOfCodigo(dto.getTpIntegra()));

        return cartao;
    }

//    public static NFNotaInfoLocal getNFNotaInfoLocal() {
//        final NFNotaInfoLocal local = new NFNotaInfoLocal();
//        local.setBairro("JE17uXBNBnYTSTSQgqXcGLOR6f22SnahtFHr5MoHQZtZhTowVe3SVwl57kil");
//        local.setCnpj("12345678901234");
//        local.setCodigoMunicipio("9999999");
//        local.setComplemento("ifyKIg3j3eZtlNVAj3XJYZiJCrul6VLL85E7x6Kx6DVeChwlRLEkCQn7k5pe");
//        local.setLogradouro("t59le7pl2eVn390y026Ebgh3HXtvEBzsMp4BzZJEwIazezToxeeKJCvm1GoG");
//        local.setNomeMunicipio("OpXKhaHINo7OwLkVGvRq43HNwyBAgXTKcarl6Jsq8NzOBs70eZM4zL6fELOI");
//        local.setNumero("YHTewrLNvzYaBmSbwxkDYcEZTCMORFVPAc6t6C5p0Bfu1globey70KWnaHHa");
//        local.setUf(NFUnidadeFederativa.RS);
//        return local;
//    }

//    public static NFNotaInfoExportacao getNFNotaInfoExportacao() {
//        final NFNotaInfoExportacao exportacao = new NFNotaInfoExportacao();
//        exportacao.setUfEmbarqueProduto(NFUnidadeFederativa.RS);
//        exportacao.setLocalEmbarqueProdutos("xEb99u9TExujbhMIcO9u9ycsZAg2gtKzIFgsUogoVjuyDAhnlkZz3I5Hpccm");
//        exportacao.setLocalDespachoProdutos("xEb99u9TExujbhMIcO9u9ycsZAg2gtKzIFgsUogoVjuyDAhnlkZz3I5Hpccm");
//        return exportacao;
//    }

    public static NFNotaInfoIdentificacao getNFNotaInfoIdentificacao(IdeDTO dto, String id) {
        final NFNotaInfoIdentificacao ide = new NFNotaInfoIdentificacao();

        ide.setAmbiente(DFAmbiente.valueOfCodigo(dto.getTpAmb()));
        ide.setCodigoMunicipio(dto.getCMunFG());
        ide.setCodigoRandomico(StringUtils.leftPad(FiscalHelper.generateCNF(), 8, "0"));
        ide.setDataHoraEmissao(DateHelper.toZonedDateTime(DateTime.parse(dto.getDhEmi()).toDate()));

        if (dto.getMod().equals("55")) {
            DateTime date = DateTime.parse(dto.getDhSaiEnt());
            ide.setDataHoraSaidaOuEntrada(DateHelper.toZonedDateTime(date.toDate()));
        }

        ide.setDigitoVerificador(Integer.parseInt(FiscalHelper.calculateCDV(id)));
        ide.setFinalidade(NFFinalidade.valueOfCodigo(dto.getFinNFe()));
        ide.setModelo(DFModelo.valueOfCodigo(dto.getMod()));
        ide.setNaturezaOperacao(dto.getNatOp());
        ide.setNumeroNota(dto.getNNF());
        ide.setProgramaEmissor(NFProcessoEmissor.CONTRIBUINTE);

        if (nonNull(dto.getNFref()) && !(dto.getNFref().isEmpty())) {
            List<NFInfoReferenciada> ref = Lists.newArrayList();

            for (NFrefDTO nFrefDTO : dto.getNFref()) {
                ref.add(getNFInfoReferenciada(nFrefDTO));
            }

            ide.setReferenciadas(ref);
        }

        ide.setSerie(dto.getSerie());
        ide.setTipo(NFTipo.valueOfCodigo(dto.getTpNF()));
        ide.setTipoEmissao(NFTipoEmissao.valueOfCodigo(dto.getTpEmis()));
        ide.setTipoImpressao(NFTipoImpressao.valueOfCodigo(dto.getTpImp()));
        ide.setUf(DFUnidadeFederativa.valueOfCodigo(dto.getCUF()));
        ide.setVersaoEmissor(dto.getVerProc());

        if (nonNull(dto.getXJust())) {
            ide.setDataHoraContigencia(DateHelper.toZonedDateTime(new DateTime(DateTimeZone.UTC).toDate()));
            ide.setJustificativaEntradaContingencia(dto.getXJust());
        }

        ide.setIdentificadorLocalDestinoOperacao(NFIdentificadorLocalDestinoOperacao.valueOfCodigo(dto.getIdDest()));
        ide.setOperacaoConsumidorFinal(NFOperacaoConsumidorFinal.valueOfCodigo(dto.getIndFinal()));
        ide.setIndicadorPresencaComprador(NFIndicadorPresencaComprador.valueOfCodigo(dto.getIndPres()));

        return ide;
    }

//    public static NFNotaInfoCartao getNFNotaInfoCartao() {
//        final NFNotaInfoCartao cartao = new NFNotaInfoCartao();
//        cartao.setCnpj("12345678901234");
//        cartao.setNumeroAutorizacaoOperacaoCartao("9ItpS1hBk3TyhjUB3I90");
//        cartao.setOperadoraCartao(NFOperadoraCartao.MASTERCARD);
//        cartao.setTipoIntegracao(NFTipoIntegracaoPagamento.INTEGRADO);
//        return cartao;
//    }

    public static NFInfoReferenciada getNFInfoReferenciada(NFrefDTO dto) {
        final NFInfoReferenciada ref = new NFInfoReferenciada();

        ref.setChaveAcesso(dto.getRefNFe());

        return ref;
    }

    public static NFNotaInfoInformacoesAdicionais getNFNotaInfoInformacoesAdicionais(InfAdicDTO dto) {
        final NFNotaInfoInformacoesAdicionais infoAdicionais = new NFNotaInfoInformacoesAdicionais();

        infoAdicionais.setInformacoesComplementaresInteresseContribuinte(FiscalHelper.removeAccent(dto.getInfCpl().trim()));

//        infoAdicionais.setInformacoesAdicionaisInteresseFisco("qe7Qi21GMSBan0iZLatpXAQAEhXEWZAO0HhHlQLlX18rryo9e1IX5Prav6fvNgZwfppMXa2RzJ7wyDH4gK3VEjeTARJ2iOLtZFDWrEaNMcGnKiusILw5bnRqBLxQfrtkTwcikLpsoI3ULurBUMMbSh1nJboZzwHUhWfArMie6CK1qBWeqgDUqMLXvkyZN66tOcBU4gv6oPZLaIJkblNYTZTEe4L1B5fx2TWec7P5Fi6HTWZiupnonWvZ51tPotK8g52ZUPXSl0lDbtWEkCGgWch0LX5xaalPL4taLgXJo1aJ1KwqSGh2SXPX9Vp316yZX6kiw6Z2yQnBN0cEfbVLp8wlYaAtsyWRGBSpqg6L3yjyciUeXkIWziOzuK0mtHsgqlXVcXLbh6sfx1zv9R3E3ITMbWOKMknfnrvoffPGJYj6p3300K4vfvUBo8ryf54eEHDhNHeegc4LMtrg2KYmr1a3QweF5B2lgNsWoyKkZ1eBU81vBNJsK9qwgeRxwBj5wqbYkk6JIKKiSbhPgP0IE7NsuobmoSyraX5QJCNyayP1oGJxLSuHR7YCGNXYJIDv3LErhgyo3qKPsLHznYP0PfSrlOSjkJzMT4A0jUrXBH3g2coofv5kug8EmOnG0u6NG2pXwClLfI3GD14H12iugRcfYU5qMWSK09bbDcMH7XuLZumguvIMsZcPxjrhbMjokxYaMLTohkPCnUNXfAPZaayNpEnRhJwRUwFKBvNPLRXbPNjxYJKjMhgtoiSur7lWwPDtkoawI0OaJZpZFUDF7qRV9oaBnNBq0xtwN4YzoCFkNok5gtcIE6VJljMOAkT1RuRhyg5hsIxaxqJWN37NBYBJvR2m9QakYNun5eRwmkIC2ejGzyK4GlqsvkT0HZ37j6SbMajFQ50jS7bY2x4zezyHQWUBB2M9mse90q8UyjnGgXqskm6nwlVAjnbOK9oqAUSXpEXUQnQYqFrmSJh1ZGFZXZ252JOQP8T3jE3UXsBUcxBqSKjTxfK5Llc3PIOD1lEasYwr7Y7MSDDofL6cJ8yChRbxcNf6rbMZ9eoMv9Xj2V4RCLOVyHSXx7zeBhJCgyzQWi6i3xECeyQz9ImWnU7oSB7r89lhHSkWemVJrYbKS82ru7jUIbeG9lYTyyERxOqwzEOCX55UM5kFihgaNIxz8Fq2BiScR79cPlD0AUAxwZjYIIC7B7rDatmxXQQWu9ZSCVTVD4FTIKotzz5Fksy1FDbYbUom523n8oXmpnUcmebSo2ocSB2LU0BDXMMXNTysznImi1qzEc5ItHwqYJAucSIQSXCMT2qv2DBjmU8Y7EJqVhRaBOQGeDI79HCfmk0XwZpAlmP5oUpDYFWlFU0wX1uFj2ozO7uZOa8vWq9ZgTJTFS1BgXYmyN4nzX0hseXOaGrE6SywDcVAcnBDtiV3D9oZ2Wf0WsAth3CZkGQ6i6QvRLHjGyHyu2cUemTJuQwNCG5FFkGaqMyxVhxqgv6yx387L4BDsMBxkWVyu6EB3UJ7hEmcoOeEp8OKGtgTJ9oqqLR8onzs1SADb9WnOCqyINCacUA4Kgmcixw6aZMtYolW5VV4h3m5syQo2qsqVczgklLYt15GLeHzeEwL9KUTxye2sBqY8IwSY7gJ4lpNhf7TFN9y42JZbFw0mBAh95GSHvyZRWOtb1CLBlBSqZX7RaA3s3S9a4FDFHOyYA6QGsW019Te2Jb6MbpsUsFtQsEB7yRXniQFbNW4rH89LzZbTC3zLRDnbTOBD4nGqvazEySlo1ReLfwku4BPkM0f8g3rTFtrMKB69kv7hHStzRLmBjU3T1JirQBc2UYjcxvNhu7wFhS2G7T4B1giejt9YHgFhtE8QjkSHTw692vSFtwOyw8GtuE7nmMe0bQLqS8TqzSgvantVepnuFttiw5Uw1B33XBNt3KhKmJYnyQxQ422qhtLIPo1JIMJ56WhWsejyXFropV7FJqHCZWqYIM1gyccj39HM4bJ3plj");

//        final ArrayList<NFNotaInfoObservacao> observacoesFisco = new ArrayList<>();
//        observacoesFisco.add(getNFNotaInfoObservacao());
//        infoAdicionais.setObservacoesFisco(observacoesFisco);

//        final ArrayList<NFNotaInfoObservacao> observacoesContribuinte = new ArrayList<>();
//        observacoesContribuinte.add(getNFNotaInfoObservacao());
//        infoAdicionais.setObservacoesContribuinte(observacoesContribuinte);
//
//        final ArrayList<NFNotaInfoProcessoReferenciado> processosRefenciado = new ArrayList<>();
//        processosRefenciado.add(getNFNotaInfoProcessoReferenciado());
//        infoAdicionais.setProcessosRefenciado(processosRefenciado);

        return infoAdicionais;
    }

    public static NFNotaInfoItem getNFNotaInfoItem(DetDTO dto) {
        final NFNotaInfoItem item = new NFNotaInfoItem();

        item.setImposto(getNFNotaInfoItemImposto(dto.getImposto()));
//        item.setInformacoesAdicionais("R3s36BVI9k15xOe3hnlEpZRpPHEom9inv4hE1oo8hzHYG8X6D9sQjt6oLYiH6yToSFM95zueMhE4s270GB7iLUKcQTRHWLcHb1TU2fSYx2NAz5ZflI3hoTnN8zmqJtGzneaNpDRA5gJW7wxMg9IXIuUCxg25MlIQ46AbDQNc3HLl82g3awWKigBMli0bUEWIMf8C2GG2sB2Y9w1GnsfiDvw7RUuU5vATfWWvYFRCehm2UpDhBlrBjjXcWKYzXsT3x2PNtCC82JqY1nkKrgt2AHCPUjM0tCQk5EHFcssb8I0Rkc4s8aNcARXtFrBzmWqXDQPmCpLIGaAo7LlypOKKaqUNqkRkf8c930p8HaRDvQJealZsVnpwJn3Ev7yEaBZ9INe5PXFwkTQEfpNE3B8IokFMh0aUbu8mfzjKLBazSKW2qA4faIo2Wp5FmOmTzCMiPqznOq3Bl0zM4wmuo0rOXbswjaCUzPB0KpM8Yaze9TArOEDrV6Li");
        item.setNumeroItem(Integer.parseInt(dto.getNItem()));
        item.setProduto(getNFNotaInfoItemProduto(dto.getProd()));
//        item.setImpostoDevolvido(getNFImpostoDevolvido());

        return item;
    }

//    public static NFNotaInfoObservacao getNFNotaInfoObservacao() {
//        final NFNotaInfoObservacao observacao = new NFNotaInfoObservacao();
//        observacao.setIdentificacaoCampo("kRkrK4FGWOn27RSjYjMB");
//        observacao.setConteudoCampo("ML73tIXUvsLEMijwgwjHVRfpP6upxiuipvEcQcSp8fpV402GXe3nXEHXJKJo");
//        return observacao;
//    }
//
//    public static NFNotaInfoProcessoReferenciado getNFNotaInfoProcessoReferenciado() {
//        final NFNotaInfoProcessoReferenciado processoReferenciado = new NFNotaInfoProcessoReferenciado();
//        processoReferenciado.setIdentificadorProcessoOuAtoConcessorio("SziSRSIRZvYWlxcbmmJfRZsLgVHaHTurUL9ea1kwFe7fssrxTVSK6uaFwGO5");
//        processoReferenciado.setIndicadorOrigemProcesso(NFOrigemProcesso.JUSTICA_FEDERAL);
//        return processoReferenciado;
//    }

    public static NFNotaInfoItemImposto getNFNotaInfoItemImposto(ImpostoDTO dto) {
        final NFNotaInfoItemImposto imposto = new NFNotaInfoItemImposto();

        if (nonNull(dto.getCOFINS())) {
            imposto.setCofins(getNFNotaInfoItemImpostoCOFINS(dto.getCOFINS()));
        }
//        imposto.setCofinsst(getNFNotaInfoItemImpostoCOFINSST());
        if (nonNull(dto.getICMS())) {
            imposto.setIcms(getNFNotaInfoItemImpostoICMS(dto.getICMS()));
        }
//        imposto.setImpostoImportacao(getNFNotaInfoItemImpostoImportacao());
        if (nonNull(dto.getIPI())) {
            imposto.setIpi(getNFNotaInfoItemImpostoIPI(dto.getIPI()));
        }
        if (nonNull(dto.getPIS())) {
            imposto.setPis(getNFNotaInfoItemImpostoPIS(dto.getPIS()));
        }
//        imposto.setPisst(getNFNotaInfoItemImpostoPISST());
        if (nonNull(dto.getICMSUFDest())) {
            imposto.setIcmsUfDestino(getNFNotaaInfoItemImpostoICMSUFDestino(dto.getICMSUFDest()));
        }
        imposto.setValorTotalTributos(new BigDecimal(dto.getVTotTrib()));

        return imposto;
    }

//    public static NFImpostoDevolvido getNFImpostoDevolvido() {
//        final NFImpostoDevolvido impostoDevolvido = new NFImpostoDevolvido();
//        impostoDevolvido.setInformacaoIPIDevolvido(getNFInformacaoImpostoDevolvido());
//        impostoDevolvido.setPercentualDevolucao(new BigDecimal("100"));
//        return impostoDevolvido;
//    }

//    public static NFInformacaoImpostoDevolvido getNFInformacaoImpostoDevolvido() {
//        final NFInformacaoImpostoDevolvido informacaoImpostoDevolvido = new NFInformacaoImpostoDevolvido();
//        informacaoImpostoDevolvido.setValorIPIDevolvido(new BigDecimal("9999999999999.99"));
//        return informacaoImpostoDevolvido;
//    }

    public static NFNotaInfoItemImpostoICMSUFDestino getNFNotaaInfoItemImpostoICMSUFDestino(ICMSUFDestDTO dto) {
        final NFNotaInfoItemImpostoICMSUFDestino icmsUFDestino = new NFNotaInfoItemImpostoICMSUFDestino();

        icmsUFDestino.setPercentualAliquotaInternaDestino(new BigDecimal(dto.getPICMSUFDest()));
        icmsUFDestino.setPercentualInterestadual(new BigDecimal(dto.getPICMSInter()));
        icmsUFDestino.setPercentualProvisorioPartilha(new BigDecimal(dto.getPICMSInterPart()));
        icmsUFDestino.setPercentualRelativoFundoCombatePobrezaDestino(new BigDecimal(dto.getPFCPUFDest()));
        icmsUFDestino.setValorBaseCalculoDestino(new BigDecimal(dto.getVBCUFDest()));
        icmsUFDestino.setValorICMSInterestadualDestino(new BigDecimal(dto.getVICMSUFDest()));
        icmsUFDestino.setValorICMSInterestadualRemetente(new BigDecimal(dto.getVICMSUFRemet()));
        icmsUFDestino.setValorRelativoFundoCombatePobrezaDestino(new BigDecimal(dto.getVFCPUFDest()));
        icmsUFDestino.setValorBCFundoCombatePobrezaDestino(new BigDecimal(dto.getVBCFCPUFDest()));

        return icmsUFDestino;
    }

    public static NFNotaInfoItemImpostoPIS getNFNotaInfoItemImpostoPIS(PISDTO dto) {
        final NFNotaInfoItemImpostoPIS pis = new NFNotaInfoItemImpostoPIS();

        if (nonNull(dto.getPISAliq()))
            pis.setAliquota(getNFNotaInfoItemImpostoPISAliquota(dto.getPISAliq()));

        if (nonNull(dto.getPISNT()))
            pis.setNaoTributado(getNFNotaInfoItemImpostoPISNT(dto.getPISNT()));

        if (nonNull(dto.getPISOutr()))
            pis.setOutrasOperacoes(getNFNotaInfoItemImpostoPISOutr(dto.getPISOutr()));

        return pis;
    }


//    public static NFNotaInfoItemImpostoPISST getNFNotaInfoItemImpostoPISST() {
//        final NFNotaInfoItemImpostoPISST impostoPISST = new NFNotaInfoItemImpostoPISST();
//        impostoPISST.setValorAliquota(new BigDecimal("9999999999.9999"));
//        impostoPISST.setQuantidadeVendida(new BigDecimal("99999999999.9999"));
//        impostoPISST.setValorTributo(new BigDecimal("999999999999.99"));
//        return impostoPISST;
//    }

    public static NFNotaInfoItemImpostoPISOutrasOperacoes getNFNotaInfoItemImpostoPISOutr(PISOutrDTO dto) {
        final NFNotaInfoItemImpostoPISOutrasOperacoes pis = new NFNotaInfoItemImpostoPISOutrasOperacoes();

        pis.setPercentualAliquota(new BigDecimal(dto.getPPIS()));
        pis.setSituacaoTributaria(NFNotaInfoSituacaoTributariaPIS.valueOfCodigo(dto.getCST()));
        pis.setValorBaseCalculo(new BigDecimal(dto.getVBC()));
        pis.setValorTributo(new BigDecimal(dto.getVPIS()));

        return pis;
    }

    public static NFNotaInfoItemImpostoPISNaoTributado getNFNotaInfoItemImpostoPISNT(PISNTDTO dto) {
        final NFNotaInfoItemImpostoPISNaoTributado pis = new NFNotaInfoItemImpostoPISNaoTributado();

        pis.setSituacaoTributaria(NFNotaInfoSituacaoTributariaPIS.valueOfCodigo(dto.getCST()));

        return pis;
    }

    public static NFNotaInfoItemImpostoPISAliquota getNFNotaInfoItemImpostoPISAliquota(PISAliqDTO dto) {
        final NFNotaInfoItemImpostoPISAliquota pis = new NFNotaInfoItemImpostoPISAliquota();

        pis.setPercentualAliquota(new BigDecimal(dto.getPPIS()));
        pis.setSituacaoTributaria(NFNotaInfoSituacaoTributariaPIS.valueOfCodigo(dto.getCST()));
        pis.setValorBaseCalculo(new BigDecimal(dto.getVBC()));
        pis.setValorTributo(new BigDecimal(dto.getVPIS()));

        return pis;
    }

    public static NFNotaInfoItemImpostoIPI getNFNotaInfoItemImpostoIPI(IPIDTO dto) {
        final NFNotaInfoItemImpostoIPI ipi = new NFNotaInfoItemImpostoIPI();

//        ipi.setClasseEnquadramento("157br");
//        ipi.setCnpjProdutor("12345678901234");
        ipi.setCodigoEnquadramento(dto.getCEnq());
//        ipi.setCodigoSelo("iNEFifS1jexTxcCvgjlQ186nR6JAwM2koyjbWKA1DJSLmZy432GoSwoygXc5");
//        ipi.setQuantidadeSelo(new BigInteger("999999999999"));
        ipi.setTributado(getNFNotaInfoItemImpostoIPITributado(dto.getIPITrib()));

        return ipi;
    }

    public static NFNotaInfoItemImpostoIPITributado getNFNotaInfoItemImpostoIPITributado(IPITribDTO dto) {
        final NFNotaInfoItemImpostoIPITributado ipiTributado = new NFNotaInfoItemImpostoIPITributado();

        ipiTributado.setSituacaoTributaria(NFNotaInfoSituacaoTributariaIPI.valueOfCodigo(dto.getCST()));
        ipiTributado.setValorBaseCalculo(new BigDecimal(dto.getVBC()));
        ipiTributado.setPercentualAliquota(new BigDecimal(dto.getPIPI()));
        ipiTributado.setValorTributo(new BigDecimal(dto.getVIPI()));

        return ipiTributado;
    }

    public static NFNotaInfoItemImpostoICMS getNFNotaInfoItemImpostoICMS(ICMSDTO dto) {
        //TODO: Complete this!
        final NFNotaInfoItemImpostoICMS icms = new NFNotaInfoItemImpostoICMS();
        if (nonNull(dto.getICMS00())) {
            icms.setIcms00(getNFNotaInfoItemImpostoICMS00(dto.getICMS00()));
        } else if (nonNull(dto.getICMS10())) {
            icms.setIcms10(getNFNotaInfoItemImpostoICMS10(dto.getICMS10()));
        } else if (nonNull(dto.getICMS20())) {
            icms.setIcms20(getNFNotaInfoItemImpostoICMS20(dto.getICMS20()));
        } else if (nonNull(dto.getICMS30())) {
            icms.setIcms30(getNFNotaInfoItemImpostoICMS30(dto.getICMS30()));
        } else if (nonNull(dto.getICMS40())) {
            icms.setIcms40(null);
        } else if (nonNull(dto.getICMS41())) {
            // No content
        } else if (nonNull(dto.getICMS50())) {
            // No content
        } else if (nonNull(dto.getICMS51())) {
            icms.setIcms51(getNFNotaInfoItemImpostoICMS51(dto.getICMS51()));
        } else if (nonNull(dto.getICMS60())) {
            icms.setIcms60(getNFNotaInfoItemImpostoICMS60(dto.getICMS60()));
        } else if (nonNull(dto.getICMS70())) {
            icms.setIcms70(null);
        } else if (nonNull(dto.getICMS90())) {
            icms.setIcms90(null);
        } else if (nonNull(dto.getICMSPart10())) {
            icms.setIcmsPartilhado(null);
        } else if (nonNull(dto.getICMSPart90())) {
            icms.setIcmsPartilhado(null);
        } else if (nonNull(dto.getICMSSN101())) {
            icms.setIcmssn101(getNFNotaInfoItemImpostoICMSSN101(dto.getICMSSN101()));
        } else if (nonNull(dto.getICMSSN102())) {
            icms.setIcmssn102(getNFNotaInfoItemImpostoICMSSN102(dto.getICMSSN102()));
        } else if (nonNull(dto.getICMSSN201())) {
            icms.setIcmssn201(getNFNotaInfoItemImpostoICMSSN201(dto.getICMSSN201()));
        } else if (nonNull(dto.getICMSSN202())) {
            icms.setIcmssn202(getNFNotaInfoItemImpostoICMSSN202(dto.getICMSSN202()));
        } else if (nonNull(dto.getICMSSN203())) {
            // No content
        } else if (nonNull(dto.getICMSSN300())) {
            // No content
        } else if (nonNull(dto.getICMSSN400())) {
            // No content
        } else if (nonNull(dto.getICMSSN500())) {
            icms.setIcmssn500(getNFNotaInfoItemImpostoICMSSN500(dto.getICMSSN500()));
        } else if (nonNull(dto.getICMSSN900())) {
            icms.setIcmssn900(getNFNotaInfoItemImpostoICMSSN900(dto.getICMSSN900()));
        } else if (nonNull(dto.getICMSST())) {
            icms.setIcmsst(null);
        }

        return icms;
    }

    private static NFNotaInfoItemImpostoICMSSN202 getNFNotaInfoItemImpostoICMSSN202(ICMSSN202DTO dto) {
        final NFNotaInfoItemImpostoICMSSN202 icmssn = new NFNotaInfoItemImpostoICMSSN202();
        if (nonNull(dto.getModalidadeBCICMSST()))
            icmssn.setModalidadeBCICMSST(NFNotaInfoItemModalidadeBCICMSST.valueOfCodigo(dto.getModalidadeBCICMSST()));
        if (nonNull(dto.getOrigem()))
            icmssn.setOrigem(NFOrigem.valueOfCodigo(dto.getOrigem()));
        if (nonNull(dto.getPercentualAliquotaImpostoICMSST()))
            icmssn.setPercentualAliquotaImpostoICMSST(new BigDecimal(dto.getPercentualAliquotaImpostoICMSST()));
        if (nonNull(dto.getPercentualFundoCombatePobrezaST()))
            icmssn.setPercentualFundoCombatePobrezaST(new BigDecimal(dto.getPercentualFundoCombatePobrezaST()));
        if (nonNull(dto.getModalidadeBCICMSST()))
            icmssn.setModalidadeBCICMSST(NFNotaInfoItemModalidadeBCICMSST.valueOfCodigo(dto.getModalidadeBCICMSST()));
        if (nonNull(dto.getPercentualMargemValorAdicionadoICMSST()))
            icmssn.setPercentualMargemValorAdicionadoICMSST(new BigDecimal(dto.getPercentualMargemValorAdicionadoICMSST()));
        if (nonNull(dto.getPercentualReducaoBCICMSST()))
            icmssn.setPercentualReducaoBCICMSST(new BigDecimal(dto.getPercentualReducaoBCICMSST()));
        if (nonNull(dto.getSituacaoOperacaoSN()))
            icmssn.setSituacaoOperacaoSN(NFNotaSituacaoOperacionalSimplesNacional.valueOfCodigo(dto.getSituacaoOperacaoSN()));
        if (nonNull(dto.getValorBCFundoCombatePobrezaST()))
            icmssn.setValorBCFundoCombatePobrezaST(new BigDecimal(dto.getValorBCFundoCombatePobrezaST()));
        if (nonNull(dto.getValorBCICMSST()))
            icmssn.setValorBCICMSST(new BigDecimal(dto.getValorBCICMSST()));
        if (nonNull(dto.getValorFundoCombatePobrezaST()))
            icmssn.setValorFundoCombatePobrezaST(new BigDecimal(dto.getValorFundoCombatePobrezaST()));
        if (nonNull(dto.getValorICMSST()))
            icmssn.setValorICMSST(new BigDecimal(dto.getValorICMSST()));
        return icmssn;
    }

    private static NFNotaInfoItemImpostoICMSSN201 getNFNotaInfoItemImpostoICMSSN201(ICMSSN201DTO dto) {
        final NFNotaInfoItemImpostoICMSSN201 icmssn = new NFNotaInfoItemImpostoICMSSN201();
        if (nonNull(dto.getModalidadeBCICMSST()))
            icmssn.setModalidadeBCICMSST(NFNotaInfoItemModalidadeBCICMSST.valueOfCodigo(dto.getModalidadeBCICMSST()));
        if (nonNull(dto.getOrigem()))
            icmssn.setOrigem(NFOrigem.valueOfCodigo(dto.getOrigem()));
        if (nonNull(dto.getPercentualAliquotaImpostoICMSST()))
            icmssn.setPercentualAliquotaImpostoICMSST(new BigDecimal(dto.getPercentualAliquotaImpostoICMSST()));
        if (nonNull(dto.getPercentualFundoCombatePobrezaST()))
            icmssn.setPercentualFundoCombatePobrezaST(new BigDecimal(dto.getPercentualFundoCombatePobrezaST()));
        if (nonNull(dto.getModalidadeBCICMSST()))
            icmssn.setModalidadeBCICMSST(NFNotaInfoItemModalidadeBCICMSST.valueOfCodigo(dto.getModalidadeBCICMSST()));
        if (nonNull(dto.getPercentualMargemValorAdicionadoICMSST()))
            icmssn.setPercentualMargemValorAdicionadoICMSST(new BigDecimal(dto.getPercentualMargemValorAdicionadoICMSST()));
        if (nonNull(dto.getPercentualReducaoBCICMSST()))
            icmssn.setPercentualReducaoBCICMSST(new BigDecimal(dto.getPercentualReducaoBCICMSST()));
        if (nonNull(dto.getSituacaoOperacaoSN()))
            icmssn.setSituacaoOperacaoSN(NFNotaSituacaoOperacionalSimplesNacional.valueOfCodigo(dto.getSituacaoOperacaoSN()));
        if (nonNull(dto.getValorBCFundoCombatePobrezaST()))
            icmssn.setValorBCFundoCombatePobrezaST(new BigDecimal(dto.getValorBCFundoCombatePobrezaST()));
        if (nonNull(dto.getValorBCICMSST()))
            icmssn.setValorBCICMSST(new BigDecimal(dto.getValorBCICMSST()));
        if (nonNull(dto.getValorFundoCombatePobrezaST()))
            icmssn.setValorFundoCombatePobrezaST(new BigDecimal(dto.getValorFundoCombatePobrezaST()));
        if (nonNull(dto.getValorICMSST()))
            icmssn.setValorICMSST(new BigDecimal(dto.getValorICMSST()));
        return icmssn;
    }

//    public static NFNotaInfoItemImpostoImportacao getNFNotaInfoItemImpostoImportacao() {
//        final NFNotaInfoItemImpostoImportacao importacao = new NFNotaInfoItemImpostoImportacao();
//        importacao.setValorBaseCalculo(new BigDecimal("999999999999.99"));
//        importacao.setValorDespesaAduaneira(new BigDecimal("999999999999.99"));
//        importacao.setValorImpostoImportacao(new BigDecimal("999999999999.99"));
//        importacao.setValorIOF(new BigDecimal("999999999999.99"));
//        return importacao;
//    }

    public static NFNotaInfoItemImpostoICMSSN101 getNFNotaInfoItemImpostoICMSSN101(ICMSSN101DTO dto) {
        final NFNotaInfoItemImpostoICMSSN101 icms = new NFNotaInfoItemImpostoICMSSN101();

        icms.setOrigem(NFOrigem.valueOfCodigo(dto.getOrig()));
        icms.setSituacaoOperacaoSN(NFNotaSituacaoOperacionalSimplesNacional.valueOfCodigo(dto.getCSOSN()));
        icms.setPercentualAliquotaAplicavelCalculoCreditoSN(new BigDecimal(dto.getPCredSN()));
        icms.setValorCreditoICMSSN(new BigDecimal(dto.getVCredICMSSN()));

        return icms;
    }

    public static NFNotaInfoItemImpostoICMS00 getNFNotaInfoItemImpostoICMS00(ICMS00DTO dto) {
        final NFNotaInfoItemImpostoICMS00 icms00 = new NFNotaInfoItemImpostoICMS00();

        icms00.setModalidadeBCICMS(NFNotaInfoItemModalidadeBCICMS.valueOfCodigo(dto.getModBC()));
        icms00.setOrigem(NFOrigem.valueOfCodigo(dto.getOrig()));
        icms00.setPercentualAliquota(new BigDecimal(dto.getPICMS()));
        icms00.setSituacaoTributaria(NFNotaInfoImpostoTributacaoICMS.valueOfCodigo(dto.getCST()));
        icms00.setValorBaseCalculo(new BigDecimal(dto.getVBC()));
        icms00.setValorTributo(new BigDecimal(dto.getVICMS()));
        if (nonNull(dto.getPFCP())) {
            icms00.setPercentualFundoCombatePobreza(new BigDecimal(dto.getPFCP()));
            icms00.setValorFundoCombatePobreza(new BigDecimal(dto.getVFCP()));
        }

        return icms00;
    }

    private static NFNotaInfoItemImpostoICMS10 getNFNotaInfoItemImpostoICMS10(ICMS10DTO dto) {
        final NFNotaInfoItemImpostoICMS10 icms10 = new NFNotaInfoItemImpostoICMS10();

        icms10.setOrigem(NFOrigem.valueOfCodigo(dto.getOrig()));
        icms10.setSituacaoTributaria(NFNotaInfoImpostoTributacaoICMS.valueOfCodigo(dto.getCST()));
        icms10.setModalidadeBCICMS(NFNotaInfoItemModalidadeBCICMS.valueOfCodigo(dto.getModBC()));
        icms10.setValorBaseCalculo(new BigDecimal(dto.getVBC()));
        icms10.setPercentualAliquota(new BigDecimal(dto.getPICMS()));
        icms10.setValorTributo(new BigDecimal(dto.getVICMS()));
        icms10.setModalidadeBCICMSST(NFNotaInfoItemModalidadeBCICMSST.valueOfCodigo(dto.getModBCST()));
        icms10.setPercentualMargemValorAdicionadoICMSST(new BigDecimal(dto.getPMVAST()));
        //icms10.setPercentualReducaoBCICMSST(new BigDecimal(dto.getPRedBCST()));
        icms10.setValorBCICMSST(new BigDecimal(dto.getVBCST()));
        icms10.setPercentualAliquotaImpostoICMSST(new BigDecimal(dto.getPICMSST()));
        icms10.setValorICMSST(new BigDecimal(dto.getVICMSST()));

        return icms10;
    }


    public static NFNotaInfoItemImpostoICMS20 getNFNotaInfoItemImpostoICMS20(ICMS20DTO dto) {
        final NFNotaInfoItemImpostoICMS20 icms20 = new NFNotaInfoItemImpostoICMS20();

        icms20.setOrigem(NFOrigem.valueOfCodigo(dto.getOrig()));
        icms20.setSituacaoTributaria(NFNotaInfoImpostoTributacaoICMS.valueOfCodigo(dto.getCST()));
        icms20.setModalidadeBCICMS(NFNotaInfoItemModalidadeBCICMS.valueOfCodigo(dto.getModBC()));
        icms20.setPercentualReducaoBC(new BigDecimal(dto.getPRedBC()));
        icms20.setValorBCICMS(new BigDecimal(dto.getVBC()));
        icms20.setPercentualAliquota(new BigDecimal(dto.getPICMS()));
        icms20.setValorTributo(new BigDecimal(dto.getVICMS()));

        return icms20;
    }

    public static NFNotaInfoItemImpostoICMS30 getNFNotaInfoItemImpostoICMS30(ICMS30DTO dto) {
        final NFNotaInfoItemImpostoICMS30 icms30 = new NFNotaInfoItemImpostoICMS30();

        icms30.setOrigem(NFOrigem.valueOfCodigo(dto.getOrig()));
        icms30.setSituacaoTributaria(NFNotaInfoImpostoTributacaoICMS.valueOfCodigo(dto.getCST()));
        icms30.setModalidadeBCICMSST(NFNotaInfoItemModalidadeBCICMSST.valueOfCodigo(dto.getModBCST()));
        icms30.setPercentualMargemValorAdicionadoICMSST(new BigDecimal(dto.getPMVAST()));
        icms30.setValorBCICMSST(new BigDecimal(dto.getVBCST()));
        icms30.setPercentualAliquotaImpostoICMSST(new BigDecimal(dto.getPICMSST()));
        icms30.setValorImpostoICMSST(new BigDecimal(dto.getVICMSST()));

        return icms30;
    }

    public static NFNotaInfoItemImpostoICMS51 getNFNotaInfoItemImpostoICMS51(ICMS51DTO dto) {
        final NFNotaInfoItemImpostoICMS51 icms51 = new NFNotaInfoItemImpostoICMS51();

        icms51.setOrigem(NFOrigem.valueOfCodigo(dto.getOrig()));
        icms51.setSituacaoTributaria(NFNotaInfoImpostoTributacaoICMS.valueOfCodigo(dto.getCST()));
        icms51.setModalidadeBCICMS(NFNotaInfoItemModalidadeBCICMS.valueOfCodigo(dto.getModBC()));
        if (nonNull(icms51.getPercentualReducaoBC()))
            icms51.setPercentualReducaoBC(new BigDecimal(dto.getPRedBC()));
        icms51.setValorBCICMS(new BigDecimal(dto.getVBC()));
        icms51.setPercentualICMS(new BigDecimal(dto.getPICMS()));
        icms51.setValorICMSOperacao(new BigDecimal(dto.getVICMSOp()));
        icms51.setPercentualDiferimento(new BigDecimal(dto.getPDif()));
        icms51.setValorICMSDiferimento(new BigDecimal(dto.getVICMSDif()));
        icms51.setValorICMS(new BigDecimal(dto.getVICMS()));

        return icms51;
    }

    private static NFNotaInfoItemImpostoICMS60 getNFNotaInfoItemImpostoICMS60(ICMS60DTO dto) {
        NFNotaInfoItemImpostoICMS60 icms60 = new NFNotaInfoItemImpostoICMS60();

        icms60.setOrigem(NFOrigem.valueOfCodigo(dto.getOrig()));
        icms60.setSituacaoTributaria(NFNotaInfoImpostoTributacaoICMS.valueOfCodigo(dto.getCST()));
        if (nonNull(dto.getVBCSTRet()))
            icms60.setValorBCICMSSTRetido(new BigDecimal(dto.getVBCSTRet()));
        if (nonNull(dto.getVICMSSTRet()))
            icms60.setValorICMSSTRetido(new BigDecimal(dto.getVICMSSTRet()));

        return icms60;
    }

    public static NFNotaInfoItemImpostoICMSSN102 getNFNotaInfoItemImpostoICMSSN102(ICMSSN102DTO dto) {
        final NFNotaInfoItemImpostoICMSSN102 icmssn102 = new NFNotaInfoItemImpostoICMSSN102();

        icmssn102.setOrigem(NFOrigem.valueOfCodigo(dto.getOrig()));
        icmssn102.setSituacaoOperacaoSN(NFNotaSituacaoOperacionalSimplesNacional.valueOfCodigo(dto.getCSOSN()));

        return icmssn102;
    }

    public static NFNotaInfoItemImpostoICMSSN500 getNFNotaInfoItemImpostoICMSSN500(ICMSSN500DTO dto) {
        final NFNotaInfoItemImpostoICMSSN500 icmssn500 = new NFNotaInfoItemImpostoICMSSN500();

        icmssn500.setOrigem(NFOrigem.valueOfCodigo(dto.getOrig()));
        icmssn500.setSituacaoOperacaoSN(NFNotaSituacaoOperacionalSimplesNacional.valueOfCodigo(dto.getCSOSN()));
        icmssn500.setValorBCICMSSTRetido(new BigDecimal(dto.getVBCSTRet()));
        icmssn500.setPercentualICMSSTRetido(new BigDecimal(dto.getPST()));
        icmssn500.setValorICMSSTRetido(new BigDecimal(dto.getVICMSSTRet()));

        return icmssn500;
    }

    public static NFNotaInfoItemImpostoICMSSN900 getNFNotaInfoItemImpostoICMSSN900(ICMSSN900DTO dto) {
        final NFNotaInfoItemImpostoICMSSN900 icmssn900 = new NFNotaInfoItemImpostoICMSSN900();

        icmssn900.setOrigem(NFOrigem.valueOfCodigo(dto.getOrig()));
        icmssn900.setSituacaoOperacaoSN(NFNotaSituacaoOperacionalSimplesNacional.valueOfCodigo(dto.getCSOSN()));
        icmssn900.setModalidadeBCICMSST(NFNotaInfoItemModalidadeBCICMSST.valueOfCodigo(dto.getModBCST()));
        icmssn900.setPercentualMargemValorAdicionadoICMSST(new BigDecimal(dto.getPMVAST()));
        icmssn900.setValorBCICMSST(new BigDecimal(dto.getVBCST()));
        icmssn900.setPercentualAliquotaImpostoICMSST(new BigDecimal(dto.getPICMSST()));
        icmssn900.setValorICMSST(new BigDecimal(dto.getVICMSST()));

        return icmssn900;
    }

    public static NFNotaInfoItemImpostoCOFINS getNFNotaInfoItemImpostoCOFINS(COFINSDTO dto) {
        final NFNotaInfoItemImpostoCOFINS cofins = new NFNotaInfoItemImpostoCOFINS();

        if (nonNull(dto.getCOFINSNT())) {
            cofins.setNaoTributavel(getNFNotaInfoItemImpostoCOFINSNT(dto.getCOFINSNT()));
        }

        if (nonNull(dto.getCOFINSAliq())) {
            cofins.setAliquota(getNFNotaInfoItemImpostoCOFINSAliq(dto.getCOFINSAliq()));
        }

        if (nonNull(dto.getCOFINSOutr())) {
            cofins.setOutrasOperacoes(getNFNotaInfoItemImpostoCOFINSOutr(dto.getCOFINSOutr()));
        }

        return cofins;
    }

    public static NFNotaInfoItemImpostoCOFINSAliquota getNFNotaInfoItemImpostoCOFINSAliq(COFINSAliqDTO dto) {
        final NFNotaInfoItemImpostoCOFINSAliquota cofins = new NFNotaInfoItemImpostoCOFINSAliquota();

        cofins.setSituacaoTributaria(NFNotaInfoSituacaoTributariaCOFINS.valueOfCodigo(dto.getCST()));
        cofins.setValorBaseCalculo(new BigDecimal(dto.getVBC()));
        cofins.setPercentualAliquota(new BigDecimal(dto.getPCOFINS()));
        cofins.setValor(new BigDecimal(dto.getVCOFINS()));

        return cofins;
    }

    public static NFNotaInfoItemImpostoCOFINSOutrasOperacoes getNFNotaInfoItemImpostoCOFINSOutr(COFINSOutrDTO dto) {
        final NFNotaInfoItemImpostoCOFINSOutrasOperacoes cofins = new NFNotaInfoItemImpostoCOFINSOutrasOperacoes();

        cofins.setSituacaoTributaria(NFNotaInfoSituacaoTributariaCOFINS.valueOfCodigo(dto.getCST()));
        cofins.setValorBaseCalculo(new BigDecimal(dto.getVBC()));
        cofins.setPercentualCOFINS(new BigDecimal(dto.getPCOFINS()));
        cofins.setValorCOFINS(new BigDecimal(dto.getVCOFINS()));

        return cofins;
    }

    public static NFNotaInfoItemImpostoCOFINSNaoTributavel getNFNotaInfoItemImpostoCOFINSNT(COFINSNTDTO dto) {
        final NFNotaInfoItemImpostoCOFINSNaoTributavel cofins = new NFNotaInfoItemImpostoCOFINSNaoTributavel();

        cofins.setSituacaoTributaria(NFNotaInfoSituacaoTributariaCOFINS.valueOfCodigo(dto.getCST()));

        return cofins;
    }

    public static NFNotaInfoItemProduto getNFNotaInfoItemProduto(ProdDTO dto) {
        final NFNotaInfoItemProduto produto = new NFNotaInfoItemProduto();

        produto.setCfop(dto.getCFOP());
        produto.setCodigo(dto.getCProd());
        produto.setCodigoDeBarras(dto.getCEAN());
        produto.setCodigoDeBarrasTributavel(dto.getCEANTrib());
        produto.setCompoeValorNota(NFProdutoCompoeValorNota.valueOfCodigo(dto.getIndTot()));
//        produto.setDeclaracoesImportacao(Collections.singletonList(getNFNotaInfoItemProdutoDeclaracaoImportacao()));
        produto.setDescricao(dto.getXProd().trim());
//        produto.setExtipi("999");
        produto.setCodigoEspecificadorSituacaoTributaria(dto.getCEST());
        if (!isNullOrEmpty(dto.getCBenef()))
            produto.setCodigoBeneficioFiscalUF(dto.getCBenef());
//        produto.setMedicamentos(Collections.singletonList(getNFNotaInfoItemProdutoMedicamento()));
        produto.setNcm(dto.getNCM());
//        produto.setNumeroPedidoCliente("NNxQ9nrQ3HCe5Mc");
//        produto.setNumeroPedidoItemCliente(999999);
        produto.setQuantidadeComercial(new BigDecimal(dto.getQCom()));
        produto.setQuantidadeTributavel(new BigDecimal(dto.getQTrib()));
        produto.setUnidadeComercial(dto.getUCom());
        produto.setUnidadeTributavel(dto.getUTrib());
        if (!isNullOrEmpty(dto.getVDesc()))
            produto.setValorDesconto(new BigDecimal(dto.getVDesc()));
        if (!isNullOrEmpty(dto.getVFrete()))
            produto.setValorFrete(new BigDecimal(dto.getVFrete()));
//        produto.setValorOutrasDespesasAcessorias(new BigDecimal("999999999999.99"));
        if (!isNullOrEmpty(dto.getVSeg()))
            produto.setValorSeguro(new BigDecimal(dto.getVSeg()));
        produto.setValorTotalBruto(new BigDecimal(dto.getVProd()));
        produto.setValorUnitario(new BigDecimal(dto.getVUnCom()));
//        produto.setNomeclaturaValorAduaneiroEstatistica(Collections.singletonList("AZ0123"));
        produto.setValorUnitarioTributavel(new BigDecimal(dto.getVUnTrib()));

        return produto;
    }

//    public static NFNotaInfoItemImpostoCOFINSAliquota getNFNotaInfoItemImpostoCOFINSAliquota() {
//        final NFNotaInfoItemImpostoCOFINSAliquota cofinsAliquota = new NFNotaInfoItemImpostoCOFINSAliquota();
//        cofinsAliquota.setPercentualAliquota(new BigDecimal("99.99"));
//        cofinsAliquota.setSituacaoTributaria(NFNotaInfoSituacaoTributariaCOFINS.OPERACAO_TRIBUTAVEL_CUMULATIVO_NAO_CUMULATIVO);
//        cofinsAliquota.setValor(new BigDecimal("999999999999.99"));
//        cofinsAliquota.setValorBaseCalulo(new BigDecimal("999999999999.99"));
//        return cofinsAliquota;
//    }

//    public static NFNotaInfoItemImpostoCOFINSST getNFNotaInfoItemImpostoCOFINSST() {
//        final NFNotaInfoItemImpostoCOFINSST cofins = new NFNotaInfoItemImpostoCOFINSST();
//        cofins.setValorBaseCalculo(new BigDecimal("999999999999.99"));
//        cofins.setPercentualAliquota(new BigDecimal("99.99"));
//        cofins.setValorCOFINS(new BigDecimal("999999999999"));
//        return cofins;
//    }

    @Override
    public NFLoteEnvio fromDTO(FiscalDocumentDTO dto) {
        NFLoteEnvio lot = new NFLoteEnvio();

        lot.setIndicadorProcessamento(NFLoteIndicadorProcessamento.PROCESSAMENTO_SINCRONO);
        lot.setVersao(VERSION_NFE);
        lot.setIdLote(Long.toString(DateTime.now().getMillis()));

        List<NFNota> invoices = Lists.newArrayList();
        invoices.add(getNFNota(dto));

        lot.setNotas(invoices);

        return lot;
    }

//    public static NFNotaInfoItemProdutoDeclaracaoImportacao getNFNotaInfoItemProdutoDeclaracaoImportacao() {
//        final NFNotaInfoItemProdutoDeclaracaoImportacao declaraoImportacao = new NFNotaInfoItemProdutoDeclaracaoImportacao();
//        declaraoImportacao.setAdicoes(Collections.singletonList(getNFNotaInfoItemProdutoDeclaracaoImportacaoAdicao()));
//        declaraoImportacao.setCodigoExportador("E9jBqM65b0MiCiRnYil203iNGJOSZs8iU1KGmQsj2N0kw6QMuvhbsQosFGcU");
//        declaraoImportacao.setDataDesembaraco(new LocalDate(2014, 1, 1));
//        declaraoImportacao.setDataRegistro(new LocalDate(2014, 2, 2));
//        declaraoImportacao.setLocalDesembaraco("kiVfWKB94ggsrWND0XBXwEjJkoiTXhkmX9qKGKzjpnEHHp852bDkYeEUkzpU");
//        declaraoImportacao.setNumeroRegistro("ZRJihqWLyHnb");
//        declaraoImportacao.setUfDesembaraco(NFUnidadeFederativa.RS);
//        declaraoImportacao.setTransporteInternacional(NFViaTransporteInternacional.AEREA);
//        declaraoImportacao.setValorAFRMM(new BigDecimal("999999999999.99"));
//        declaraoImportacao.setFormaImportacaoIntermediacao(NFFormaImportacaoIntermediacao.IMPORTACAO_ENCOMENDA);
//        declaraoImportacao.setCnpj("12345678901234");
//        declaraoImportacao.setUfTerceiro(NFUnidadeFederativa.RS);
//        return declaraoImportacao;
//    }
//
//    public static NFNotaInfoItemProdutoDeclaracaoImportacaoAdicao getNFNotaInfoItemProdutoDeclaracaoImportacaoAdicao() {
//        final NFNotaInfoItemProdutoDeclaracaoImportacaoAdicao importacaoAdicao = new NFNotaInfoItemProdutoDeclaracaoImportacaoAdicao();
//        importacaoAdicao.setCodigoFabricante("sA2FBRFMMNgF1AKRDDXYOlc3zGvzEc69l6zQ5O5uAUe82XZ3szQfw01DW0Ki");
//        importacaoAdicao.setDesconto(new BigDecimal("999999999999.99"));
//        importacaoAdicao.setNumero(999);
//        importacaoAdicao.setSequencial(999);
//        importacaoAdicao.setNumeroAtoConcessorioDrawback(new BigInteger("99999999999"));
//        return importacaoAdicao;
//    }
//
//    public static NFNotaInfoItemProdutoMedicamento getNFNotaInfoItemProdutoMedicamento() {
//        final NFNotaInfoItemProdutoMedicamento medicamento = new NFNotaInfoItemProdutoMedicamento();
//        medicamento.setDataFabricacao(new LocalDate(2014, 1, 1));
//        medicamento.setDataValidade(new LocalDate(2015, 1, 1));
//        medicamento.setLote("yq50jVDZsvQVNuWoS45U");
//        medicamento.setPrecoMaximoConsumidor(new BigDecimal("999999999999.99"));
//        medicamento.setQuantidade(new BigDecimal("9999999.999"));
//        return medicamento;
//    }

    @Override
    public String response(NFLoteEnvioRetornoDados result) {
        final StringBuilder sb = new StringBuilder();

        sb.append("Ambiente: ").append(result.getRetorno().getAmbiente());
        sb.append("\n");
        sb.append("UF: ").append(result.getRetorno().getUf());
        sb.append("\n");
        sb.append("Data Recebimento: ").append(result.getRetorno().getDataRecebimento());
        sb.append("\n");
        sb.append("Status: ").append(result.getRetorno().getProtocoloInfo().getStatus()).append(" - ").append(result.getRetorno().getProtocoloInfo().getMotivo());
        sb.append("\n");
        sb.append("Protocolo: ").append(result.getRetorno().getProtocoloInfo().getNumeroProtocolo());
        sb.append("\n");
        sb.append("Chave de Acesso: ").append(result.getRetorno().getProtocoloInfo().getChave());

        if (result.getLoteAssinado().getNotas().get(0).getInfo().getIdentificacao().getModelo().equals(DFModelo.NFCE)) {
            sb.append("\n");
            sb.append("QrCode: ").append(result.getLoteAssinado().getNotas().get(0).getInfoSuplementar().getQrCode());
            sb.append("#####");
        }

        return sb.toString();
    }

    public String response(NFNota document) {
        final StringBuilder sb = new StringBuilder();

        sb.append("Ambiente: ").append(document.getInfo().getIdentificacao().getAmbiente());
        sb.append("\n");
        sb.append("UF: ").append(document.getInfo().getIdentificacao().getUf());
        sb.append("\n");
        sb.append("Status: ").append("Contingência off-line da NFC-e");
        sb.append("\n");
        sb.append("QrCode: ").append(document.getInfoSuplementar().getQrCode());
        sb.append("#####");

        return sb.toString();
    }
}
