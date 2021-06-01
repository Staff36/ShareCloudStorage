package Handlers;

import lombok.extern.log4j.Log4j;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


@Log4j
public class MailSender {
    private String username;
    private String password;
    private Properties properties;

    public MailSender(){
        this.username = "testforjava.tronin@gmail.com";
        this.password = "TestForJava";
        properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");
    }

    public void send(String subject, String text, String toMail) throws MessagingException {
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toMail));
        message.setSubject(subject);
        message.setText(text);
        Transport.send(message);
    }


}
