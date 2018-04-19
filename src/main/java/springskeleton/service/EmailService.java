package springskeleton.service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import springskeleton.config.property.AppProperties;
import springskeleton.config.property.MailProperties;
import springskeleton.controller.exception.ServerErrorException;
import springskeleton.util.Mail;

@Service
public class EmailService {

    private JavaMailSender emailSender;

    private SpringTemplateEngine templateEngine;

    private MailProperties mailProperties;

    private AppProperties appProperties;

    @Autowired
    public EmailService(JavaMailSender emailSender, SpringTemplateEngine templateEngine, MailProperties mailProperties, AppProperties appProperties) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
        this.mailProperties = mailProperties;
        this.appProperties = appProperties;
    }

    @Async
    public void send(final Mail mail) throws ServerErrorException {
        final MimeMessage message = this.emailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            Context context = this.buildContext(mail);
            final String html = this.templateEngine.process("email-template", context);
            this.setHelperData(mail, helper, html);
            this.emailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException exception) {
            throw new ServerErrorException("Error sending email to " + mail.getTo());
        }
    }

    private Context buildContext(final Mail mail) {
        Context context = new Context();
        context.setVariable("content", mail.getContent());
        context.setVariable("title", mail.getTitle());
        context.setVariable("footerContent", this.appProperties.getName());
        return context;
    }

    private void setHelperData(final Mail mail, final MimeMessageHelper helper, String html) throws MessagingException, UnsupportedEncodingException {
        helper.setTo(mail.getTo());
        helper.setText(html, true);
        helper.setSubject(mail.getSubject());
        helper.setFrom(new InternetAddress(this.mailProperties.getUsername(), this.appProperties.getName()));
    }

}
