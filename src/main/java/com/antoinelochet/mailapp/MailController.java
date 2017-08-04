package com.antoinelochet.mailapp;

import freemarker.template.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Antoine on 04/08/2017.
 */
@RestController
public class MailController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailController.class);
    private static final String FROM = "mail.from";
    private static final String FROM_NAME = "mail.from.name";
    private static final String ENCODING_UTF_8 = StandardCharsets.UTF_8.displayName();

    @Autowired
    private Environment environment;

    @Autowired
    private MailService mailService;

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private Configuration freeMarkerConfiguration;

    @PostMapping(value = "contact")
    public void sendMail(@RequestParam("apikey") String apiKey, @RequestParam("email") String email, @RequestParam("name") String name, @RequestParam("message") String message) {
        final String recipient = this.environment.getRequiredProperty(apiKey);
        MimeMessagePreparator preparator = mimeMessage -> {
            final MimeMessageHelper mail = new MimeMessageHelper(mimeMessage, ENCODING_UTF_8);
            mail.setTo(recipient);
            mail.setFrom(this.environment.getRequiredProperty(FROM), this.environment.getRequiredProperty(FROM_NAME));
            final Map<String, Object> model = new HashMap<>();
            model.put("email", email);
            model.put("name", name);
            model.put("message", message);
            final String text = FreeMarkerTemplateUtils.processTemplateIntoString(this.freeMarkerConfiguration.getTemplate("contact.vm"), model);
            mail.setText(text, true);
            mail.setSubject("Nouveau contact depuis github.io !");
        };
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        try {
            preparator.prepare(mimeMessage);
        } catch (Exception e) {
            LOGGER.error("Mail to {} could not be sent", recipient, e);
        }
        LOGGER.debug("Sending a new github.io mail for client {}", recipient);
        this.mailService.sendEmail(mimeMessage, recipient);
    }
}
