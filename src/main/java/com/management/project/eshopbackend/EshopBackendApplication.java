package com.management.project.eshopbackend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import service.intef.ImageStorageService;

import javax.annotation.Resource;
import java.util.Properties;

@SpringBootApplication
public class EshopBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(EshopBackendApplication.class, args);
  }


  @Override
  public void run(String... args) {
    imageStorageService.init();
  }

  @Resource
  ImageStorageService imageStorageService;

  @Value("${spring.mail.host}")
  private String host;

  @Value("${spring.mail.username}")
  private String username;

  @Value("${spring.mail.password}")
  private String password;

  @Value("${spring.mail.port}")
  private Integer port;

  @Bean
  public JavaMailSender javaMailService() {
    JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
    javaMailSender.setHost(this.host);
    javaMailSender.setPort(this.port);
    javaMailSender.setUsername(this.username);
    javaMailSender.setPassword(this.password);

    Properties javaMailProperties = new Properties();
    javaMailProperties.put("mail.smtp.starttls.enable", "true");
    javaMailProperties.put("mail.smtp.auth", "true");
    javaMailProperties.put("mail.transport.protocol", "smtp");
    javaMailProperties.put("mail.debug", "true");
    javaMailProperties.put("mail.smtp.ssl.trust", "*");
    javaMailSender.setJavaMailProperties(javaMailProperties);

    return javaMailSender;
  }
}
