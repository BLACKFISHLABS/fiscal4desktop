package io.github.blackfishlabs.fiscal4desktop.common.properties;

public final class FiscalPropertiesConstants {

    static final String KEY_DIRECTORY_APPLICATION = "dir.application";
    static final String KEY_DIRECTORY_CERTIFICATE = "dir.certificate";
    static final String KEY_DIRECTORY_XML = "dir.xml";
    static final String KEY_DIRECTORY_PDF = "dir.pdf";
    static final String KEY_DIRECTORY_IMG = "dir.img";

    static final String KEY_PARAM_UF = "param.uf";
    static final String KEY_PARAM_PRODUCTION = "param.production";
    static final String KEY_PARAM_CNPJ = "param.cnpj";
    static final String KEY_PARAM_PASSWORD = "param.password";
    static final String KEY_PARAM_EMAIL_ACCOUNTANT = "param.email.accountant";

    private FiscalPropertiesConstants() {
        throw new AssertionError();
    }
}

