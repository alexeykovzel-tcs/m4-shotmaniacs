package com.shotmaniacs.utils;

import org.apache.commons.io.IOUtils;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;

public class EmailSender {
    private static final String SENDER_NAME = "Shotmaniacs";
    private static final String SENDER_EMAIL = "noreply@shotmaniacs.nl";

    public void sendConfirmSignup(String fullname, String... to) {
        String body = readFile("e-mail/confirm-signup.html").replace("%FULLNAME%", fullname);
        sendEmail("Confirm Signup", body, to);
    }

    public void sendResetPassword(String... to) {
        sendEmail("Reset Password", readFile("e-mail/reset-password.html"), to);
    }

    private String readFile(String path) {
        String text = null;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(path)) {
            if (in != null) text = IOUtils.toString(in, Charset.defaultCharset());
        } catch (IOException e) {
            System.out.println("[ERROR] Could not read a file: " + e.getMessage());
        }
        return text;
    }

    private void sendEmail(String subject, String body, String... to) {
        try {
            // Convert recipient addresses
            InternetAddress[] recipients = new InternetAddress[to.length];
            for (int i = 0; i < to.length; i++) {
                recipients[i] = new InternetAddress(to[i]);
            }

            // Start a session with e-mail server
            Session session = startSession();
            if (session == null) {
                System.out.println("[ERROR] Could not send an e-mail");
                return;
            }

            // Configure e-mail meta data
            Message message = new MimeMessage(startSession());
            message.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME));
            message.setRecipients(Message.RecipientType.TO, recipients);
            message.setSubject(subject);

            // Configure e-mail content
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(body, "text/html; charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);
            message.setContent(multipart);

            // Send e-mail
            Transport.send(message);
        } catch (MessagingException | IOException e) {
            System.out.println("[ERROR] Could not configure an e-mail: " + e.getMessage());
        }
    }

    private Session startSession() {
        // Load e-mail server properties
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("email.properties")) {
            props.load(in);
            // Launch a new session using server credentials
            return Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(props.getProperty("username"), props.getProperty("password"));
                }
            });
        } catch (IOException e) {
            return null;
        }
    }
}