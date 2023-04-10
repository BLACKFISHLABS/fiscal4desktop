package io.github.blackfishlabs.fiscal4desktop.infra;

import br.indie.fiscal4j.DFAmbiente;
import br.indie.fiscal4j.DFUnidadeFederativa;
import br.indie.fiscal4j.nfe.NFeConfig;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class NFeConfiguration extends NFeConfig {

    private static final String CADEIA_JKS = "cadeia.jks";
    private static final String CHANGEIT = "changeit";

    private KeyStore keyStoreCertificado = null;
    private KeyStore keyStoreCadeia = null;
    private String password;
    private String certificate;
    private String csc;

    public NFeConfiguration(String certificate, String password) {
        this.certificate = certificate;
        this.password = password;
    }

    public NFeConfiguration(String certificate, String password, String csc) {
        this.certificate = certificate;
        this.password = password;
        this.csc = csc;
    }

    private NFeConfiguration() {
    }

    @Override
    public DFUnidadeFederativa getCUF() {
        return DFUnidadeFederativa.valueOfCodigo(FiscalProperties.getInstance().getUF());
    }

    @Override
    public KeyStore getCertificadoKeyStore() throws KeyStoreException {
        if (this.keyStoreCertificado == null) {
            this.keyStoreCertificado = KeyStore.getInstance("PKCS12");
            try (InputStream certificadoStream = Files.newInputStream(Paths.get(FiscalProperties.getInstance().getDirCertificate().concat(this.certificate).concat(".pfx")))) {
                this.keyStoreCertificado.load(certificadoStream, this.getCertificadoSenha().toCharArray());
            } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
                this.keyStoreCadeia = null;
                throw new KeyStoreException("Nao foi possivel montar o KeyStore com a cadeia de certificados", e);
            }
        }
        return this.keyStoreCertificado;
    }

    @Override
    public String getCertificadoSenha() {
        return this.password;
    }

    @Override
    public KeyStore getCadeiaCertificadosKeyStore() throws KeyStoreException {
        if (this.keyStoreCadeia == null) {
            this.keyStoreCadeia = KeyStore.getInstance("JKS");
            try (InputStream cadeia = Files.newInputStream(Paths.get(FiscalProperties.getInstance().getDirCertificate().concat(CADEIA_JKS)))) {
                this.keyStoreCadeia.load(cadeia, this.getCadeiaCertificadosSenha().toCharArray());
            } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
                this.keyStoreCadeia = null;
                throw new KeyStoreException("Nao foi possivel montar o KeyStore com o certificado", e);
            }
        }
        return this.keyStoreCadeia;
    }

    @Override
    public String getCadeiaCertificadosSenha() {
        return CHANGEIT;
    }

    @Override
    public String getCodigoSegurancaContribuinte() {
        return this.csc;
    }

    @Override
    public Integer getCodigoSegurancaContribuinteID() {
        return 1;
    }

    @Override
    public DFAmbiente getAmbiente() {
        return FiscalProperties
                .getInstance()
                .getProduction()
                .equals("SIM") ? DFAmbiente.PRODUCAO : DFAmbiente.HOMOLOGACAO;
    }
}
