package io.github.blackfishlabs.starter;

import br.indie.fiscal4j.DFAmbiente;
import br.indie.fiscal4j.utils.DFCadeiaCertificados;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class NFeBuildAllCacerts extends DFCadeiaCertificados {

    private static final Logger logger = LogManager.getLogger(NFeBuildAllCacerts.class);
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
            logger.info("Certificate sucessfully generated in " + buildCacerts.getPath());
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
