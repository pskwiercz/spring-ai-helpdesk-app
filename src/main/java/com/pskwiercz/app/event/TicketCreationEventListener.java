package com.pskwiercz.app.event;

import com.pskwiercz.app.email.EmailNotificationService;
import com.pskwiercz.app.model.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketCreationEventListener implements ApplicationListener<TicketCreationEvent> {
    private final EmailNotificationService emailNotificationService;

    @Override
    public void onApplicationEvent(TicketCreationEvent event) {
        Ticket ticket = event.getTicket();
        try {
            // emailNotificationService.sendTicketNotificationEmail(ticket);
            log.info("++++++++++++++++ Emil send: {}", ticket.getConversation().getCustomer().getEmailAddress());
        } catch (MessagingException e) {
            log.error("Failed to send ticket notification email to the customer: {}", e.getMessage());
        }
    }
}