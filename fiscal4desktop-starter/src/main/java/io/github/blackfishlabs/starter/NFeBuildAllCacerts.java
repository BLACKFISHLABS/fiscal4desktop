package io.github.blackfishlabs.starter;

import br.indie.fiscal4j.DFAmbiente;
import br.indie.fiscal4j.utils.DFCadeiaCertificados;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;

@Slf4j
public class NFeBuildAllCacerts extends DFCadeiaCertificados {

    private final String path;

    private NFeBuildAllCacerts(String path) {
        this.path = path;
    }

    public static void main(String[] args) {
        try {
            final NFeBuildAllCacerts buildCacerts = new NFeBuildAllCacerts(FiscalProperties
                    .getInstance()
                    .getDirCertificate()
                    .concat("cadeia.jks"));

            FileUtils.writeByteArrayToFile(
                    new File(buildCacerts.getPath()),
                    geraCadeiaCertificados(DFAmbiente.PRODUCAO, buildCacerts.getPassphrase()));
            log.info("Certificate sucessfully generated in " + buildCacerts.getPath());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private String getPassphrase() {
        return "changeit";
    }

    private String getPath() {
        return path;
    }
}
