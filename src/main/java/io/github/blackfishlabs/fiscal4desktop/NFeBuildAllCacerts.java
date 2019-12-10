package io.github.blackfishlabs.fiscal4desktop;

import br.indie.fiscal4j.DFAmbiente;
import br.indie.fiscal4j.utils.DFCadeiaCertificados;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class NFeBuildAllCacerts extends DFCadeiaCertificados {

    private static final Logger LOGGER = LoggerFactory.getLogger(NFeBuildAllCacerts.class);
    private final String path;

    private NFeBuildAllCacerts(String path) {
        this.path = path;
    }

    public static void main(String[] args) {
        try {
            final NFeBuildAllCacerts buildCacerts = new NFeBuildAllCacerts(FiscalProperties
                    .getInstance()
                    .getDirCertificate()
                    .concat("production.jks"));

            FileUtils.writeByteArrayToFile(
                    new File(buildCacerts.getPath()),
                    geraCadeiaCertificados(DFAmbiente.PRODUCAO, buildCacerts.getPassphrase()));
            LOGGER.info("Certificate sucessfully generated in " + buildCacerts.getPath());
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
