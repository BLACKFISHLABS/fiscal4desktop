package io.github.blackfishlabs.fiscal4desktop.ui;

import com.google.common.collect.Lists;
import io.github.blackfishlabs.fiscal4desktop.common.helper.EmailHelper;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalConstantHelper;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalHelper;
import io.github.blackfishlabs.fiscal4desktop.common.helper.ZipHelper;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import io.github.blackfishlabs.fiscal4desktop.controller.StatusWebServiceController;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.FiscalStatusWebServiceDTO;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;

@Slf4j
public class TrayIconUI {
    private static final String BLACKFISH_LABS = "BLACKFISH LABS";

    public static void createAndShowGUI() {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported!");
            return;
        }

        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(createImage());
        final SystemTray tray = SystemTray.getSystemTray();

        final MenuItem developer = new MenuItem(BLACKFISH_LABS);
        final MenuItem aboutItem = new MenuItem("SOBRE O SISTEMA");
        final MenuItem archivesItem = new MenuItem("ARQUIVOS FISCAIS - ENVIO");
        final MenuItem certificateItem = new MenuItem("CERTIFICADO");
        final MenuItem status = new MenuItem("CONSULTA STATUS - SEFAZ");
        final MenuItem exitItem = new MenuItem("SAIR");

        popup.add(developer).setEnabled(false);
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(archivesItem);
        popup.add(certificateItem);
        popup.add(status);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }

        final StringBuilder sb = new StringBuilder();

        sb.append("FISCAL APPLICATION - DESKTOP");
        sb.append("\n");
        sb.append("\n");
        sb.append("BLACKFISH LABS - https://blackfishlabs.github.io");

        trayIcon.addActionListener(e -> show(sb.toString()));
        aboutItem.addActionListener(e -> show(sb.toString()));

        status.addActionListener(e -> {
            try {
                StatusWebServiceController gateway = new StatusWebServiceController();
                FiscalStatusWebServiceDTO dto = new FiscalStatusWebServiceDTO();
                dto.setEmitter(FiscalProperties.getInstance().getCNPJ());
                dto.setPassword(FiscalHelper.decodeBase64(FiscalProperties.getInstance().getPassword()));
                dto.setUf(FiscalProperties.getInstance().getUF());

                show(gateway.getStatusWebService(dto));
            } catch (Exception e1) {
                e1.printStackTrace();
                show(e1.getMessage());
            }
        });

        certificateItem.addActionListener(e -> {
            try {
                String path = FiscalProperties.getInstance().getDirCertificate().concat(FiscalProperties.getInstance().getCNPJ()).concat(".pfx");
                String password = FiscalHelper.decodeBase64(FiscalProperties.getInstance().getPassword());

                KeyStore keystore = KeyStore.getInstance(("PKCS12"));
                keystore.load(Files.newInputStream(Paths.get(path)), password.toCharArray());

                Enumeration<String> eAliases = keystore.aliases();

                while (eAliases.hasMoreElements()) {
                    String alias = eAliases.nextElement();
                    Certificate certificado = keystore.getCertificate(alias);

                    X509Certificate cert = (X509Certificate) certificado;

                    String sb1 = "Certificado Digital" +
                            "\n" +
                            "\n" +
                            "Alias: " + alias +
                            "\n" +
                            cert.getSubjectX500Principal().getName() +
                            "\n" +
                            "Válido a partir de..: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cert.getNotBefore()) +
                            "\n" +
                            "Válido até............: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cert.getNotAfter());
                    show(sb1);
                }

            } catch (Exception e2) {
                log.error(e2.getMessage());
                show(e2.getMessage());
            }
        });

        archivesItem.addActionListener(e -> {
            String email = FiscalProperties.getInstance().getEmailAccountant();
            if (isNullOrEmpty(email)) {
                show("Arquivo de Propriedade: Preencher parametro de email do contador e conferir CNPJ!");
                return;
            }

            String name = null;

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            Date result = cal.getTime();
            String formatDate = new SimpleDateFormat("YYYYMM").format(result);

            while (isNullOrEmpty(name)) {
                name = (String) JOptionPane.showInputDialog(null,
                        "Digite o ano e o mês: [AAAAMM]",
                        "Arquivos Fiscais\n", JOptionPane.QUESTION_MESSAGE, null,
                        null, formatDate);
                if (name == null || name.equals("")) {
                    JOptionPane.showMessageDialog(null,
                            "Você não digitou os dados!");
                    return;
                }
            }

            String pathNFe = FiscalProperties.getInstance().getDirXML().concat(FiscalConstantHelper.NFE_PATH).concat(name);
            String pathNFCe = FiscalProperties.getInstance().getDirXML().concat(FiscalConstantHelper.NFCE_PATH).concat(name);
            String pathMDFe = FiscalProperties.getInstance().getDirXML().concat(FiscalConstantHelper.MDFE_PATH).concat(name);

            String pathNFeCancel = FiscalProperties.getInstance().getDirXML().concat(FiscalConstantHelper.NFE_PATH).concat(FiscalConstantHelper.CANCEL_PATH).concat(name);
            String pathNFCeCancel = FiscalProperties.getInstance().getDirXML().concat(FiscalConstantHelper.NFCE_PATH).concat(FiscalConstantHelper.CANCEL_PATH).concat(name);
            String pathMDFeCancel = FiscalProperties.getInstance().getDirXML().concat(FiscalConstantHelper.MDFE_PATH).concat(FiscalConstantHelper.CANCEL_PATH).concat(name);

            String pathCCe = FiscalProperties.getInstance().getDirXML().concat(FiscalConstantHelper.CCE_PATH).concat(name);
            String outputPath = FiscalProperties.getInstance().getDirApplication().concat(name);

            ZipHelper appZipNFe = new ZipHelper(pathNFe);
            appZipNFe.generateFileList(new File(pathNFe));
            appZipNFe.zipIt(outputPath.concat(FiscalConstantHelper.NFE_ZIP));

            ZipHelper appZipNFeCancel = new ZipHelper(pathNFeCancel);
            appZipNFeCancel.generateFileList(new File(pathNFeCancel));
            appZipNFeCancel.zipIt(outputPath.concat(FiscalConstantHelper.NFE_CANCEL_ZIP));

            ZipHelper appZipNFCe = new ZipHelper(pathNFCe);
            appZipNFCe.generateFileList(new File(pathNFCe));
            appZipNFCe.zipIt(outputPath.concat(FiscalConstantHelper.NFCE_ZIP));

            ZipHelper appZipNFCeCancel = new ZipHelper(pathNFCeCancel);
            appZipNFCeCancel.generateFileList(new File(pathNFCeCancel));
            appZipNFCeCancel.zipIt(outputPath.concat(FiscalConstantHelper.NFCE_CANCEL_ZIP));

            ZipHelper appZipMDFe = new ZipHelper(pathMDFe);
            appZipMDFe.generateFileList(new File(pathMDFe));
            appZipMDFe.zipIt(outputPath.concat(FiscalConstantHelper.MDFE_ZIP));

            ZipHelper appZipMDFeCancel = new ZipHelper(pathMDFeCancel);
            appZipMDFeCancel.generateFileList(new File(pathMDFeCancel));
            appZipMDFeCancel.zipIt(outputPath.concat(FiscalConstantHelper.MDFE_CANCEL_ZIP));

            ZipHelper appZipCCe = new ZipHelper(pathCCe);
            appZipCCe.generateFileList(new File(pathCCe));
            appZipCCe.zipIt(outputPath.concat(FiscalConstantHelper.CCE_ZIP));

            List<String> files = Lists.newArrayList(
                    outputPath.concat(FiscalConstantHelper.NFE_ZIP),
                    outputPath.concat(FiscalConstantHelper.NFE_CANCEL_ZIP),

                    outputPath.concat(FiscalConstantHelper.NFCE_ZIP),
                    outputPath.concat(FiscalConstantHelper.NFCE_CANCEL_ZIP),

                    outputPath.concat(FiscalConstantHelper.MDFE_ZIP),
                    outputPath.concat(FiscalConstantHelper.MDFE_CANCEL_ZIP),

                    outputPath.concat(FiscalConstantHelper.CCE_ZIP));

            log.info(EmailHelper.sendDocumentByEmailXML(files,
                    "Envio de XML",
                    FiscalProperties.getInstance().getCNPJ(),
                    FiscalProperties.getInstance().getCNPJ(),
                    email));

            files.forEach(f -> {
                try {
                    File file = new File(f);
                    if (file.delete())
                        log.info(file.getName() + " is deleted!");
                    else
                        log.info("Delete operation is failed.");
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            });
        });

        exitItem.addActionListener(e -> {
            tray.remove(trayIcon);
            System.exit(0);
        });

        trayIcon.setImageAutoSize(true);
    }

    private static void show(String message) {
        JOptionPane.showMessageDialog(null, message, BLACKFISH_LABS, JOptionPane.INFORMATION_MESSAGE);
    }

    private static Image createImage() {
        byte[] image = Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAIAAACQkWg2AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAAZdEVYdFNvZnR3YXJlAHBhaW50Lm5ldCA0LjAuMTJDBGvsAAAA2klEQVQ4T62SQRJDMBSGezAUN8CCGSdwADewMGNhqS0rt3ADx+onL6JB2+lMv9Wf5H3JS7i4P/InwfO8q4Ig6IWj4DjONE3zPD9W7opxHKVgESjS+yiapun7XpYNwzBIWIS6rl83q6qKHEXRTdF1HTWWwGyapm3bBkHAkEMQ1OrGXsiyrCzLMAwZcleEOI4pAmlvL8hAEIEgVwLyd4ETuA9BVvfC8uYrvu/LCUVR4CRJQraEPM+pMMgrMU8z+KrMFo7w7aQfg/XhTpH2DHr2g/COE4E/RacjrvsECVP7a7cfuQ8AAAAASUVORK5CYII=");
        ByteArrayInputStream bis = new ByteArrayInputStream(image);
        try {
            return ImageIO.read(bis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
