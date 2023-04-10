package io.github.blackfishlabs.fiscal4desktop;

import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalHelper;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import io.github.blackfishlabs.fiscal4desktop.ui.TrayIconUI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
@SpringBootApplication
public class FiscalApplication {

    private static final ScheduledExecutorService scheduler_contingency = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext app = new SpringApplicationBuilder(ApplicationConfiguration.class).headless(false).run(args);

        String applicationName = app.getEnvironment().getProperty("spring.application.name");
        String port = app.getEnvironment().getProperty("server.port");
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        initialize();

        log.info("\n|------------------------------------------------------------" +
                "\n|   Application '" + applicationName + "' is running!         " +
                "\n|   Access URLs:                                              " +
                "\n|   Local:      http://127.0.0.1:" + port +
                "\n|   External:   http://" + hostAddress + ":" + port +
                "\n|-------------------------------------------------------------");
    }

    private static void initialize() {
        lookAndFeel();
        SwingUtilities.invokeLater(TrayIconUI::createAndShowGUI);

        log.info("WORKSPACE: " + FiscalProperties.getInstance().getDirApplication());
        verifyCertificate();

        scheduler_contingency.scheduleAtFixedRate(() -> {
            try {
                sendGET();
            } catch (IOException e) {
                log.error(e.toString());
            }
        }, 1, 3, TimeUnit.HOURS);
    }

    private static void sendGET() throws IOException {
        URL obj = new URL("http://127.0.0.1:8182/nfce/contingency");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        log.info("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            log.info(response.toString());
        } else {
            log.info("GET request not worked");
        }
    }

    private static void lookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            log.error(">> " + e.getMessage());
        }
    }

    private static void verifyCertificate() {
        try {
            String path = FiscalProperties.getInstance().getDirCertificate().concat(FiscalProperties.getInstance().getCNPJ()).concat(".pfx");
            String password = FiscalHelper.decodeBase64(FiscalProperties.getInstance().getPassword());

            KeyStore keystore = KeyStore.getInstance(("PKCS12"));
            keystore.load(Files.newInputStream(Paths.get(path)), password.toCharArray());

            Enumeration<String> eAliases = keystore.aliases();

            while (eAliases.hasMoreElements()) {
                String alias = eAliases.nextElement();
                Certificate c = keystore.getCertificate(alias);

                log.info("CERTIFICADO DIGITAL:");
                log.info("Alias: " + alias);
                X509Certificate cert = (X509Certificate) c;

                log.info(cert.getSubjectX500Principal().getName());
                log.info("Válido a partir de..: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cert.getNotBefore()));
                log.info("Válido até..........: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cert.getNotAfter()));
            }
        } catch (Exception e) {
            log.warn(e.toString());
        }
    }
}
