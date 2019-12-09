package io.github.blackfishlabs;

import io.github.blackfishlabs.common.helper.FiscalHelper;
import io.github.blackfishlabs.common.properties.FiscalProperties;
import io.github.blackfishlabs.ui.TrayIconUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
@SpringBootApplication
public class FiscalApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(FiscalApplication.class);
    private static final ScheduledExecutorService scheduler_contingency = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) throws UnknownHostException {
        ApplicationContext app = SpringApplication.run(ApplicationConfiguration.class, args);

        String applicationName = app.getEnvironment().getProperty("spring.application.name");
        String port = app.getEnvironment().getProperty("server.port");
        String hostAddress = InetAddress.getLocalHost().getHostAddress();

        LOGGER.info("\n|------------------------------------------------------------" +
                "\n|   Application '" + applicationName + "' is running!         " +
                "\n|   Access URLs:                                              " +
                "\n|   Local:      http://127.0.0.1:" + port +
                "\n|   External:   http://" + hostAddress + ":" + port +
                "\n|-------------------------------------------------------------");

        initialize();
    }

    private static void initialize() {
        lookAndFeel();
        SwingUtilities.invokeLater(TrayIconUI::createAndShowGUI);

        LOGGER.info(">> Workspace: " + FiscalProperties.getInstance().getDirApplication());
        verifyCertificate();

        ContingencyScheduler contingencyScheduler = new ContingencyScheduler();
        scheduler_contingency.scheduleAtFixedRate(contingencyScheduler::execute, 1, 1, TimeUnit.HOURS);
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

                LOGGER.info(">> Certificado Digital");
                LOGGER.info("Alias: " + alias);
                X509Certificate cert = (X509Certificate) certificado;

                LOGGER.info(cert.getSubjectDN().getName());
                LOGGER.info("Válido a partir de..: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cert.getNotBefore()));
                LOGGER.info("Válido até..........: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cert.getNotAfter()));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
    }
}
