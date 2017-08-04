package com.antoinelochet.mailapp;

import javax.mail.internet.MimeMessage;

/**
 * Created by Antoine on 04/08/2017.
 */
public interface MailService {

    void sendEmail(MimeMessage mimeMessage, String to);
}
