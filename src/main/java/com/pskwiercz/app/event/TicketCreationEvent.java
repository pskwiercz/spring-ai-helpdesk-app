package com.pskwiercz.app.event;

import com.pskwiercz.app.model.Ticket;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class TicketCreationEvent extends ApplicationEvent {
    private final Ticket ticket;

    public TicketCreationEvent(Ticket ticket) {
        super(ticket);
        this.ticket = ticket;
    }
}
