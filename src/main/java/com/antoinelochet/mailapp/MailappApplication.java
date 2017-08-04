package com.antoinelochet.mailapp;

import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
@EnableAsync
public class MailappApplication {

	private static final String MAIL_USER = "mail.user";
	private static final String MAIL_PASSWORD = "mail.password";
	private static final String MAIL_HOST = "mail.host";
	private static final String MAIL_DEBUG = "mail.debug";
	private static final String MAIL_PORT = "mail.port";

	public static void main(String[] args) {
		SpringApplication.run(MailappApplication.class, args);
	}

	@Autowired
	private Environment env;

	@Bean
	public JavaMailSenderImpl mailSender() {
		final JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost(this.env.getRequiredProperty(MAIL_HOST));
		javaMailSender.setPort(Integer.parseInt(this.env.getRequiredProperty(MAIL_PORT)));
		javaMailSender.setUsername(this.env.getRequiredProperty(MAIL_USER));
		javaMailSender.setPassword(this.env.getRequiredProperty(MAIL_PASSWORD));
		final Properties javaMailProperties = new Properties();
		javaMailProperties.setProperty("mail.smtp.auth", "true");
		javaMailProperties.setProperty("mail.debug", this.env.getRequiredProperty(MAIL_DEBUG));
		javaMailProperties.setProperty("mail.smtp.starttls.enable", "true");
		javaMailProperties.setProperty("mail.smtp.ssl.trust", this.env.getRequiredProperty(MAIL_HOST));
		javaMailSender.setJavaMailProperties(javaMailProperties);

        Session session = Session.getInstance(javaMailProperties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(env.getRequiredProperty(MAIL_USER), env.getRequiredProperty(MAIL_PASSWORD));
                    }
                });
        javaMailSender.setSession(session);
		return javaMailSender;
	}

	@Bean
	public freemarker.template.Configuration freeMarkerConfiguration() throws TemplateException, IOException {
		final FreeMarkerConfigurationFactoryBean freeMarkerConfigurationFactoryBean = new FreeMarkerConfigurationFactoryBean();
		freeMarkerConfigurationFactoryBean.setTemplateLoaderPath("templates");
		return freeMarkerConfigurationFactoryBean.createConfiguration();
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(5);
		threadPoolTaskExecutor.setMaxPoolSize(10);
		threadPoolTaskExecutor.setQueueCapacity(25);
		return threadPoolTaskExecutor;
	}
}
