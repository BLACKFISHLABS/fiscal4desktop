package io.github.blackfishlabs.fiscal4desktop.common.helper;

import com.google.common.collect.Lists;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.isNullOrEmpty;

public class EmailHelper {

    private static final String EMAIL = "postmaster@blackfishlabs.com.br";
    private static final String PASSWORD = "ef12a50651131755f32651610c1b95c4";

    private static boolean validate(String email) {
        if (email.equals("no@mail.com")) {
            return false;
        }

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

    private static void send(final String sender, final String password, String recipient, String subject, String messageText, List<String> attachments) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.mailgun.org");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sender, password);
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse("dev.blackfishlabs@gmail.com"));
        message.setSubject(subject);

        BodyPart textPart = new MimeBodyPart();
        textPart.setContent(messageText, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);

        List<String> filesToSend = Lists.newArrayList();
        attachments.forEach(a -> {
            File f = new File(a);
            if (f.exists()) {
                filesToSend.add(a);
            }
        });

        if (!filesToSend.isEmpty()) {
            for (String attachment : filesToSend) {
                BodyPart attachmentPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachment);
                attachmentPart.setDataHandler(new DataHandler(source));
                attachmentPart.setFileName(source.getName());
                multipart.addBodyPart(attachmentPart);
            }
        }
        message.setContent(multipart);
        Transport.send(message);
    }

    public static String sendDocumentByEmail(final List<String> attachments,
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
            if (validate(EMAIL) && validate(email)) {
                try {
                    EmailHelper.send(EMAIL, PASSWORD, email, subject, message.toString(), attachments);
                    return "Email enviado para ".concat(email);
                } catch (MessagingException e) {
                    return "Não foi possível enviar o e-mail!"
                            .concat("\n")
                            .concat("Causa: ")
                            .concat(e.getMessage());
                }
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
            if (validate(EMAIL) && validate(email)) {
                try {
                    EmailHelper.send(EMAIL, PASSWORD, email, subject, message.toString(), attachments);
                    return "Email enviado para ".concat(email);
                } catch (MessagingException e) {
                    return "Não foi possível enviar o e-mail!"
                            .concat("\n")
                            .concat("Causa: ")
                            .concat(e.getMessage());
                }
            } else {
                return "Email inválido: " + email;
            }
        }
    }

}
