package io.github.blackfishlabs.fiscal4desktop.common.helper;

import br.indie.fiscal4j.mdfe3.classes.consultaRecibo.MDFeConsultaReciboRetorno;
import br.indie.fiscal4j.mdfe3.classes.nota.MDFProcessado;
import br.indie.fiscal4j.mdfe3.classes.nota.MDFe;
import br.indie.fiscal4j.nfe400.classes.NFProtocolo;
import br.indie.fiscal4j.nfe400.classes.NFProtocoloInfo;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvioRetorno;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvioRetornoDados;
import br.indie.fiscal4j.nfe400.classes.nota.NFNota;
import br.indie.fiscal4j.nfe400.classes.nota.NFNotaProcessada;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FiscalHelper {

    private FiscalHelper() {
    }

    private static final String VERSION_NFE = "4.00";

    // String stuffs
    public static String removeAccent(String str) {
        String validated = validationData(str).toUpperCase();
        return Normalizer.normalize(validated, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public static String validationData(String value) {
        String json = value.trim();
        json = json.replaceAll("#", "\"").trim();
        json = json.replaceAll("\\s+", " ").trim();
        json = cleanText(json, "\r", "\n");
        return json.trim();
    }

    private static String cleanText(String text, String... dirty) {
        for (String value : dirty) {
            text = text.replaceAll(value, "");
        }
        return text;
    }

    public static String generateCNF() {
        int randomInt = (int) (99999999.0 * Math.random());
        return Integer.toString(randomInt);
    }

    public static String calculateCDV(String key) {
        key = key.replaceAll("NFe", "").replaceAll("MDFe", "");
        int soma = 0;
        int multiplicador = 2;

        for (byte b = 42; b >= 0; b--) {
            soma += Character.digit(key.charAt(b), 10) * multiplicador;
            multiplicador++;
            if (multiplicador > 9) {
                multiplicador = 2;
            }
        }

        int auxDV = 11 - soma % 11;
        if (auxDV >= 10) {
            return Integer.toString(0);
        } else {
            return Integer.toString(auxDV);
        }
    }

    public static int modulo11(String key) {
        int total = 0;
        int peso = 2;

        for (int i = 0; i < key.length(); i++) {
            total += (key.charAt((key.length() - 1) - i) - '0') * peso;
            peso++;
            if (peso == 10) {
                peso = 2;
            }
        }
        int rest = total % 11;
        return (rest == 0 || rest == 1) ? 0 : (11 - rest);
    }

    // To Entity
    public static NFNotaProcessada getNFProcessed(NFLoteEnvioRetornoDados result, NFNota document) {
        final NFNotaProcessada processed = new NFNotaProcessada();
        processed.setVersao(new BigDecimal(VERSION_NFE));

        final NFProtocolo nfProtocolo = new NFProtocolo();
        nfProtocolo.setVersao(VERSION_NFE);
        nfProtocolo.setProtocoloInfo(result.getRetorno().getProtocoloInfo());

        processed.setProtocolo(nfProtocolo);
        processed.setNota(document);
        return processed;
    }

    // MDFe Processed
    public static MDFProcessado getMDFeProcessed(MDFeConsultaReciboRetorno result, MDFe document) {
        final MDFProcessado processed = new MDFProcessado();
        processed.setVersao(new BigDecimal(MDFe.VERSAO));

        processed.setProtocolo(result.getMdfProtocolo());
        processed.setMdfe(document);
        return processed;
    }

    // For NFe/NFCe Normal
    static NFNotaProcessada getNFProcessed(NFLoteEnvioRetorno result, NFNota document) {
        final NFNotaProcessada processed = new NFNotaProcessada();
        processed.setVersao(new BigDecimal(VERSION_NFE));

        final NFProtocolo nfProtocolo = new NFProtocolo();
        nfProtocolo.setVersao(VERSION_NFE);
        nfProtocolo.setProtocoloInfo(result.getProtocoloInfo());

        processed.setProtocolo(nfProtocolo);
        processed.setNota(document);
        return processed;
    }

    // For NFCe Contingency
    public static NFNotaProcessada getNFProcessed(NFNota document) {
        final NFNotaProcessada processed = new NFNotaProcessada();
        processed.setVersao(new BigDecimal(VERSION_NFE));

        final NFProtocolo nfProtocolo = new NFProtocolo();
        nfProtocolo.setVersao(VERSION_NFE);

        // Fake Protocol
        NFProtocoloInfo protocoloInfo = new NFProtocoloInfo();
        protocoloInfo.setAmbiente(document.getInfo().getIdentificacao().getAmbiente());
        protocoloInfo.setChave("-");
        protocoloInfo.setDataRecebimento(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
        protocoloInfo.setIdentificador("");
        protocoloInfo.setMotivo("");
        protocoloInfo.setNumeroProtocolo("");
        protocoloInfo.setStatus("-");
        protocoloInfo.setValidador("");
        protocoloInfo.setVersaoAplicacao(VERSION_NFE);

        nfProtocolo.setProtocoloInfo(protocoloInfo);

        processed.setProtocolo(nfProtocolo);
        processed.setNota(document);
        return processed;
    }

    public static String encodeBase64(String valor) {
        return new String(Base64.getEncoder().encode(valor.getBytes()));
    }

    public static String decodeBase64(String valor) {
        return new String(Base64.getDecoder().decode(valor));
    }

    public static void validateCertificateBeforeUse(String path, String password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        keyStore.load(new FileInputStream(path), password.toCharArray());
        Enumeration<String> eAliases = keyStore.aliases();

        while (eAliases.hasMoreElements()) {
            String alias = eAliases.nextElement();

            Certificate certificado = keyStore.getCertificate(alias);
            X509Certificate c = (X509Certificate) certificado;

            try {
                c.checkValidity();
            } catch (CertificateExpiredException e) {
                throw new RuntimeException("Certificado Digital " + alias + " expirado desde " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(c.getNotAfter()));
            }
        }
    }

    static String CNPJMask(String cnpj) {
        Pattern pattern = Pattern.compile("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})");
        Matcher matcher = pattern.matcher(cnpj);
        if (matcher.find()) {
            return matcher.replaceAll("$1.$2.$3/$4-$5");
        }
        return cnpj;
    }

}
