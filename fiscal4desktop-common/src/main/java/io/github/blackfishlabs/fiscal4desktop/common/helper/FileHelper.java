package io.github.blackfishlabs.fiscal4desktop.common.helper;

import br.indie.fiscal4j.DFModelo;
import br.indie.fiscal4j.danfe.CCeDanfeReport;
import br.indie.fiscal4j.danfe.DFParser;
import br.indie.fiscal4j.danfe.MDFeDanfeReport;
import br.indie.fiscal4j.danfe.NFDanfeReport;
import br.indie.fiscal4j.danfe.NFDanfeReport.NFCePagamento;
import br.indie.fiscal4j.mdfe3.classes.nota.MDFProcessado;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvio;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvioRetorno;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvioRetornoDados;
import br.indie.fiscal4j.nfe400.classes.nota.NFNota;
import br.indie.fiscal4j.nfe400.classes.nota.NFNotaProcessada;
import br.indie.fiscal4j.nfe400.classes.nota.consulta.NFProtocoloEvento;
import com.google.common.collect.Lists;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Slf4j
public class FileHelper {

    private static String pdfPath = "";
    private static String xmlPath = "";

    public static void exportXml(String xml, String path) throws IOException {
        FileUtils.writeStringToFile(new File(path), xml);
    }

    private static void exportPDF(byte[] pdf, String path) throws IOException {
        FileUtils.writeByteArrayToFile(new File(path), pdf);
    }

    public static void saveFilesAndSendToEmailAttach(NFLoteEnvioRetornoDados send) {
        try {
            log.info("Salvando arquivos na pasta de XML e PDF");

            List<String> files;
            files = saveFiles(send);
            files.forEach(f -> log.info("Arquivo salvo: ".concat(f)));

            Optional<NFNota> doc = send.getLoteAssinado().getNotas().stream().findFirst();

            doc.ifPresent(n -> new Thread(() -> {
                if (nonNull(n.getInfo().getDestinatario())) {
                    log.info(EmailHelper.sendDocumentByEmail(files, "Autorização de NFe",
                            n.getInfo().getEmitente().getRazaoSocial(),
                            n.getInfo().getIdentificacao().getNumeroNota(),
                            n.getInfo().getEmitente().getCnpj(),
                            n.getInfo().getChaveAcesso(),
                            n.getInfo().getDestinatario().getEmail()));
                } else {
                    log.info("Nota de consumidor. Sem Destinatário.");
                }
            }).start());

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static void saveFilesAndSendToEmailAttach(NFLoteEnvioRetorno send, String xml) {
        NFLoteEnvio loteEnvio = new DFParser().loteParaObjeto(xml);
        NFNota nota = loteEnvio.getNotas().get(0);

        try {
            log.info("Salvando arquivos na pasta de XML e PDF");
            List<String> files;
            files = saveFiles(send, xml);
            files.forEach(f -> log.info("Arquivo salvo: ".concat(f)));

            new Thread(() -> {
                if (nonNull(nota.getInfo().getDestinatario())) {
                    log.info(EmailHelper.sendDocumentByEmail(files, "Autorização de NFe emitida em Contingência",
                            nota.getInfo().getEmitente().getRazaoSocial(),
                            nota.getInfo().getIdentificacao().getNumeroNota(),
                            nota.getInfo().getEmitente().getCnpj(),
                            nota.getInfo().getChaveAcesso(),
                            nota.getInfo().getDestinatario().getEmail()));
                } else {
                    log.info("Nota de consumidor. Sem Destinatário.");
                }
            }).start();


        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private static List<String> saveFiles(NFLoteEnvioRetorno send, String xml) throws Exception {
        NFLoteEnvio loteEnvio = new DFParser().loteParaObjeto(xml);
        NFNota nota = loteEnvio.getNotas().get(0);

        List<String> nfeFiles = Lists.newArrayList();

        mountPath(nota, FiscalConstantHelper.NFCE_PATH_CONTINGENCY, nfeFiles);

        exportFiles(send, nota);

        return nfeFiles;
    }

    private static void mountPath(NFNota nota, String path, List<String> nfeFiles) {
        String key = nota.getInfo().getChaveAcesso();
        String cfop = nota.getInfo().getItens().get(0).getProduto().getCfop();
        String nf = nota.getInfo().getIdentificacao().getNumeroNota();

        pdfPath = "";
        pdfPath = FiscalProperties.getInstance().getDirPDF()
                .concat(path)
                .concat(DateHelper.toDirFormat(new Date()))
                .concat("/")
                .concat(nf)
                .concat(".pdf");

        xmlPath = "";
        xmlPath = FiscalProperties.getInstance().getDirXML()
                .concat(path)
                .concat(DateHelper.toDirFormat(new Date()))
                .concat("/")
                .concat(cfop)
                .concat("/")
                .concat(key)
                .concat(".xml");

        nfeFiles.add(pdfPath);
        nfeFiles.add(xmlPath);
    }

    private static List<String> saveFiles(NFLoteEnvioRetornoDados nfLoteEnvioRetornoDados) throws Exception {
        List<String> nfeFiles = Lists.newArrayList();

        Optional<NFNota> document = nfLoteEnvioRetornoDados.getLoteAssinado().getNotas().stream().findFirst();

        if (document.isPresent()) {
            String path = DFModelo.NFCE.equals(document.get().getInfo().getIdentificacao().getModelo()) ? FiscalConstantHelper.NFCE_PATH : FiscalConstantHelper.NFE_PATH;

            mountPath(document.get(), path, nfeFiles);

            exportFiles(nfLoteEnvioRetornoDados, document.get());
        }

        return nfeFiles;
    }

    private static void exportFiles(NFLoteEnvioRetorno send, NFNota document) throws Exception {
        final NFNotaProcessada procesed = FiscalHelper.getNFProcessed(send, document);

        exportFiles(procesed);
    }

    private static void exportFiles(NFLoteEnvioRetornoDados nfLoteEnvioRetornoDados, NFNota document) throws Exception {
        final NFNotaProcessada processed = FiscalHelper.getNFProcessed(nfLoteEnvioRetornoDados, document);

        exportFiles(processed);
    }

    private static void exportFiles(NFNotaProcessada processed) throws Exception {
        exportXml(processed.toString(), xmlPath);

        byte[] pdf;
        NFDanfeReport report = new NFDanfeReport(processed.toString());
        ArrayList<NFCePagamento> nfCePagamentos = new ArrayList<>();

        if (DFModelo.NFCE.equals(processed.getNota().getInfo().getIdentificacao().getModelo())) {
            processed.getNota().getInfo().getPagamentos().forEach(pag -> pag.getDetalhamentoFormasPagamento().forEach(detPag -> {
                nfCePagamentos.add(new NFCePagamento(detPag.getMeioPagamento().toString(), new BigDecimal(detPag.getValorPagamento())));
            }));

            pdf = report.gerarDanfeNFCe("", true, nfCePagamentos.toArray(new NFCePagamento[0]));
        } else {
            byte[] bytes = Files.readAllBytes(Paths.get(FiscalProperties.getInstance().getDirImg().concat(FiscalConstantHelper.LOGO_NFE)));
            pdf = report.gerarDanfeNFe(bytes);
        }

        exportPDF(pdf, pdfPath);
    }

    public static void exportFilesPDFOnly(NFNotaProcessada processed) throws Exception {
        byte[] pdf;
        NFDanfeReport report = new NFDanfeReport(processed.toString());
        ArrayList<NFCePagamento> nfCePagamentos = new ArrayList<>();

        processed.getNota().getInfo().getPagamentos().forEach(pag -> pag.getDetalhamentoFormasPagamento().forEach(detPag -> {
            nfCePagamentos.add(new NFCePagamento(detPag.getMeioPagamento().toString(), new BigDecimal(detPag.getValorPagamento())));
        }));

        pdf = report.gerarDanfeNFCe("", true, nfCePagamentos.toArray(new NFCePagamento[0]));

        mountPath(processed.getNota(), FiscalConstantHelper.NFCE_PATH, Lists.newArrayList());
        exportPDF(pdf, pdfPath);
    }

    public static void exportFilesPDFOnly(NFProtocoloEvento cce) throws Exception {
        CCeDanfeReport report = new CCeDanfeReport(cce);

        byte[] pdf = report.gerarDanfeCCe();

        pdfPath = "";
        pdfPath = FiscalProperties.getInstance().getDirPDF()
                .concat(FiscalConstantHelper.CCE_PATH)
                .concat(DateHelper.toDirFormat(new Date()))
                .concat("/")
                .concat(cce.getEvento().getInfoEvento().getChave())
                .concat(".pdf");
        exportPDF(pdf, pdfPath);
    }

    public static void exportFilesPDFOnly(MDFProcessado processed) throws Exception {

        MDFeDanfeReport report = new MDFeDanfeReport(processed);

        byte[] bytes = Files.readAllBytes(Paths.get(FiscalProperties.getInstance().getDirImg().concat(FiscalConstantHelper.LOGO_NFE)));
        byte[] pdf = report.gerarDanfeMDFe(bytes, "BLACKFISH LABS (blackfishlabs.github.io)");

        pdfPath = "";
        pdfPath = FiscalProperties.getInstance().getDirPDF()
                .concat(FiscalConstantHelper.MDFE_PATH)
                .concat(DateHelper.toDirFormat(new Date()))
                .concat("/")
                .concat(processed.getMdfe().getInfo().getIdentificacao().getNumero().toString())
                .concat(".pdf");
        exportPDF(pdf, pdfPath);
    }
}
