package com.pskwiercz.app.email;

import com.pskwiercz.app.model.Ticket;
import com.pskwiercz.app.model.Customer;
import com.pskwiercz.app.services.customer.ICustomerService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService {
    private final EmailService emailService;
    private final ICustomerService customerService;


    public void sendTicketNotificationEmail(Ticket ticket) {
        Customer customer = customerService.getCustomerByEmail(ticket.getConversation().getCustomer().getEmailAddress());
        String customerName = customer.getFullName();
        String customerEmail = customer.getEmailAddress();
        String customerPhone = customer.getPhoneNumber();
        String senderName = "Customer Support Service";
        String subject = "Support Ticket Created";
        String ticketDetails = ticket.getConversation().getConversationSummary();
        String ticketTile = ticket.getConversation().getConversationTitle();
        String referenceNumber = ticket.getReferenceNumber();
        String htmlBody = null;
        try {
            htmlBody = loadEmailTemplate(customerName, customerEmail, customerPhone, ticketDetails, ticketTile, referenceNumber);
        } catch (IOException e) {
            log.error("Error loading email template : {} ", e.getMessage());
        }
        try {
            emailService.sendNotificationEmail(customerEmail, subject, senderName, htmlBody);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Error sending email notification : {} ", e.getMessage());
        }
    }

    public String loadEmailTemplate(String customerName, String customerEmail, String customerPhone, String ticketDetails, String ticketTile, String referenceNumber) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/ticket-notification-template.html");
        String template = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        template = template.replace("{{customerName}}", customerName);
        template = template.replace("{{customerEmail}}", customerEmail);
        template = template.replace("{{customerPhone}}", customerPhone);
        template = template.replace("{{ticketTile}}", ticketTile);
        template = template.replace("{{ticketDetails}}", ticketDetails);
        template = template.replace("{{ticketReferenceNumber}}", referenceNumber);
        return template;
    }
}
