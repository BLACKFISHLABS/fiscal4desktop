package io.github.blackfishlabs.fiscal4desktop.common.properties;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

import static io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalPropertiesConstants.*;

@Slf4j
public final class FiscalProperties implements IPath, IParam {

    private static final String DIR_APP = System.getProperty("user.dir");
    private static final String DIR_PROPERTIES = DIR_APP + File.separator + "properties";
    private static final String FILE_PROPERTIES = DIR_PROPERTIES + File.separator + "fiscal.properties";

    public static FiscalProperties instance;

    private final Properties prop;

    public FiscalProperties() {
        prop = new Properties();
        try {
            prop.load(Files.newInputStream(Paths.get(FILE_PROPERTIES)));
            instance = this;
        } catch (IOException e) {
            log.warn("NO CONFIGURATION FILE FOUND!");
        }
    }

    public static synchronized FiscalProperties getInstance() {
        return Objects.isNull(instance) ? new FiscalProperties() : instance;
    }

    @Override
    public String getDirApplication() {
        return prop.getProperty(KEY_DIRECTORY_APPLICATION);
    }

    @Override
    public String getDirXML() {
        return prop.getProperty(KEY_DIRECTORY_XML);
    }

    @Override
    public String getDirPDF() {
        return prop.getProperty(KEY_DIRECTORY_PDF);
    }

    @Override
    public String getDirCertificate() {
        return prop.getProperty(KEY_DIRECTORY_CERTIFICATE);
    }

    @Override
    public String getDirImg() {
        return prop.getProperty(KEY_DIRECTORY_IMG);
    }

    @Override
    public String getUF() {
        return prop.getProperty(KEY_PARAM_UF);
    }

    @Override
    public String getProduction() {
        return prop.getProperty(KEY_PARAM_PRODUCTION);
    }

    @Override
    public String getCNPJ() {
        return prop.getProperty(KEY_PARAM_CNPJ);
    }

    @Override
    public String getPassword() {
        return prop.getProperty(KEY_PARAM_PASSWORD);
    }

    @Override
    public String getEmailAccountant() {
        return prop.getProperty(KEY_PARAM_EMAIL_ACCOUNTANT);
    }

    @Override
    public String getEmailCompany() {
        return prop.getProperty(KEY_PARAM_EMAIL_COMPANY);
    }

    @Override
    public String getEmailKey() {
        return prop.getProperty(KEY_PARAM_EMAIL_KEY);
    }

}

