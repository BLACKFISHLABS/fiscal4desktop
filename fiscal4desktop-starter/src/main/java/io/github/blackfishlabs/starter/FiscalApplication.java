package io.github.blackfishlabs.starter;

import io.github.blackfishlabs.api.application.ServerApplication;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalHelper;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import io.github.blackfishlabs.ui.TrayIconUI;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Jeferson Cruz
 * BLACKFISH LABS
 */
@Slf4j
public class FiscalApplication {
    private static final ScheduledExecutorService scheduler_contingency = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        initialize();
    }

    private static void initialize() {
        lookAndFeel();
        SwingUtilities.invokeLater(TrayIconUI::createAndShowGUI);

        log.info(ServerApplication.start(8182));
        verifyCertificate();
        log.info(">> Workspace: " + FiscalProperties.getInstance().getDirApplication());

        scheduler_contingency.scheduleAtFixedRate(ContingencyScheduler::execute, 1, 1, TimeUnit.HOURS);
    }

    private static void lookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private static void verifyCertificate() {
        try {
            String path = FiscalProperties.getInstance().getDirCertificate().concat(FiscalProperties.getInstance().getCNPJ()).concat(".pfx");
            String password = FiscalHelper.decodeBase64(FiscalProperties.getInstance().getPassword());

            KeyStore keystore = KeyStore.getInstance(("PKCS12"));
            keystore.load(new FileInputStream(path), password.toCharArray());

            Enumeration<String> eAliases = keystore.aliases();

            while (eAliases.hasMoreElements()) {
                String alias = eAliases.nextElement();
                Certificate certificado = keystore.getCertificate(alias);

                log.info(">> Certificado Digital");
                log.info("Alias: " + alias);
                X509Certificate cert = (X509Certificate) certificado;

                log.info(cert.getSubjectDN().getName());
                log.info("Válido a partir de..: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cert.getNotBefore()));
                log.info("Válido até..........: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cert.getNotAfter()));
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
}
