package io.github.blackfishlabs.fiscal4desktop.common.helper;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.isNullOrEmpty;

@Slf4j
public class EmailHelper {

    private static void sendEmailSendGrid(String emailFrom,
                                          String emailTo,
                                          String subject,
                                          String messageText,
                                          List<String> attachments) {
        if (isNullOrEmpty(FiscalProperties.getInstance().getEmailCompany())) {
            log.warn("Arquivo de Propriedade: Preencher parametro de email da empresa!");
            return;
        }

        if (isNullOrEmpty(FiscalProperties.getInstance().getEmailKey())) {
            log.warn("Arquivo de Propriedade: Preencher parametro de chave do email!");
            return;
        }


        Email from = new Email(emailFrom);
        Email to = new Email(emailTo);

        Content content = new Content("text/html", messageText);
        Mail mail = new Mail(from, subject, to, content);

        if (!attachments.isEmpty()) {
            for (String attachment : attachments) {
                File file = new File(attachment);
                if (file.exists()) {
                    try {
                        DataSource source = new FileDataSource(attachment);

                        Attachments myAttach = new Attachments();
                        myAttach.setContent(Base64.encodeBase64String(IOUtils.toByteArray(source.getInputStream())));
                        myAttach.setType(source.getContentType());
                        myAttach.setFilename(source.getName());
                        mail.addAttachments(myAttach);
                    } catch (IOException e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                    }
                }
            }
        }

        SendGrid sg = new SendGrid(FiscalProperties.getInstance().getEmailKey());
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
        }
    }

    public static String sendDocumentByEmailXML(final List<String> attachments,
                                             final String event,
                                             final String enterprise,
                                             final String cnpj,
                                             final String email) {

        String subject = event.concat(" - ")
                .concat(enterprise)
                .concat(" - Documentos Fiscais");

        StringBuilder message = new StringBuilder();
        message.append("<html>");
        message.append("<body>");

        message.append("Documentos Fiscais em Anexo.")
                .append("<br>")
                .append("CNPJ: ".concat(FiscalHelper.CNPJMask(cnpj)))
                .append("<br>")
                .append("<br>")
                .append("<br>")
                .append("<hr>")
                .append("Não responda este e-mail.")
                .append("<br>")
                .append("BLACKFISH LABS - https://blackfishlabs.github.io");

        message.append("</body>");
        message.append("</html>");

        if (isNullOrEmpty(email)) {
            return "Email não cadastrado.";
        } else {
            if (validate(FiscalProperties.getInstance().getEmailCompany()) && validate(email)) {
                EmailHelper.sendEmailSendGrid(FiscalProperties.getInstance().getEmailCompany(), email, subject, message.toString(), attachments);
                return "Email enviado para ".concat(email);
            } else {
                return "Email inválido: " + email;
            }
        }
    }

    static String sendDocumentByEmail(final List<String> attachments,
                                      final String event,
                                      final String enterprise,
                                      final String number,
                                      final String cnpj,
                                      final String key,
                                      final String email) {

        String subject = event.concat(" - ")
                .concat(enterprise)
                .concat(" - Documento Fiscal Número ")
                .concat(number);

        StringBuilder message = new StringBuilder();
        message.append("<html>");
        message.append("<body>");

        message.append("Documento Fiscal em Anexo.")
                .append("<br>")
                .append("<br>")
                .append(enterprise)
                .append("<br>")
                .append("CNPJ: ".concat(FiscalHelper.CNPJMask(cnpj)))
                .append("<br>")
                .append("Chave de Acesso: ")
                .append(key)
                .append("<br>")
                .append("<br>")
                .append("<br>")
                .append("<br>")
                .append("<hr>")
                .append("Não responda este e-mail.")
                .append("<br>")
                .append("BLACKFISH LABS - https://blackfishlabs.github.io");

        message.append("</body>");
        message.append("</html>");

        if (isNullOrEmpty(email)) {
            return "Email não cadastrado.";
        } else {
            if (validate(FiscalProperties.getInstance().getEmailCompany()) && validate(email)) {
                EmailHelper.sendEmailSendGrid(FiscalProperties.getInstance().getEmailCompany(), email, subject, message.toString(), attachments);
                return "Email enviado para ".concat(email);
            } else {
                return "Email inválido: " + email;
            }
        }
    }

    private static boolean validate(String email) {
        if (email.equals("no@mail.com")) return false;
        if (email.trim().length() < 6) {
            return false;
        } else {
            Pattern pattern;
            Matcher matcher;
            final String EMAIL_PATTERN
                    = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(email);

            return matcher.matches();
        }
    }

}
