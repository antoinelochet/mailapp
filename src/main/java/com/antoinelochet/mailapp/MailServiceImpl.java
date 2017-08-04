package com.antoinelochet.mailapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

/**
 * Created by Antoine on 04/08/2017.
 */
@Service
public class MailServiceImpl implements MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);
    private static final String ENABLED = "mail.enabled";

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private Environment env;

    @Override
    @Async
    public void sendEmail(final MimeMessage mimeMessage, final String to) {
        LOGGER.debug("Sending a new mail of type {} to {}", to);
        try {
            if (!Boolean.parseBoolean(this.env.getRequiredProperty(ENABLED))) {
                LOGGER.info("Mail send is disabled.");
                return;
            }
            this.mailSender.send(mimeMessage);
        } catch (MailException e) {
            LOGGER.error("Mail of type {} to {} could not be sent", to, e);
        }
    }
}
