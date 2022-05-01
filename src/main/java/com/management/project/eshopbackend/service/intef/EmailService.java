package com.management.project.eshopbackend.service.intef;

import javax.mail.MessagingException;

public interface EmailService {
    void sendEmail(String subject, String recipient, String content) throws MessagingException;
}
