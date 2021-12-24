package io.github.blackfishlabs.fiscal4desktop.ui;

import com.google.common.collect.Lists;
import io.github.blackfishlabs.fiscal4desktop.common.helper.EmailHelper;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalConstantHelper;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalHelper;
import io.github.blackfishlabs.fiscal4desktop.common.helper.ZipHelper;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import io.github.blackfishlabs.fiscal4desktop.controller.StatusWebServiceController;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.FiscalStatusWebServiceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class TrayIconUI {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrayIconUI.class);
    private static final String LOGO_PNG = "logo_min.png";
    private static final String BLACKFISH_LABS = "BLACKFISH LABS";

    public static void createAndShowGUI() {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported!");
            return;
        }

        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(createImage(FiscalProperties.getInstance().getDirImg().concat(LOGO_PNG)));
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
                keystore.load(new FileInputStream(path), password.toCharArray());

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
                            cert.getSubjectDN().getName() +
                            "\n" +
                            "Válido a partir de..: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cert.getNotBefore()) +
                            "\n" +
                            "Válido até............: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cert.getNotAfter());
                    show(sb1);
                }

            } catch (Exception e2) {
                LOGGER.error(e2.getMessage());
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

            LOGGER.info(EmailHelper.sendDocumentByEmailXML(files,
                    "Envio de XML",
                    FiscalProperties.getInstance().getCNPJ(),
                    FiscalProperties.getInstance().getCNPJ(),
                    email));

            files.forEach(f -> {
                try {
                    File file = new File(f);
                    if (file.delete())
                        LOGGER.info(file.getName() + " is deleted!");
                    else
                        LOGGER.info("Delete operation is failed.");
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

    private static Image createImage(final String path) {
        final File file = new File(path);
        Image img = null;
        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            show("try create TrayIcon with null Image");
        }
        return img;
    }
}
