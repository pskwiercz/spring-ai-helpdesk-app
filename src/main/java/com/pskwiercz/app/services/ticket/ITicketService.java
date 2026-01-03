package com.pskwiercz.app.services.ticket;

import com.pskwiercz.app.dto.TicketDTO;
import com.pskwiercz.app.model.Conversation;
import com.pskwiercz.app.model.Ticket;

import java.util.List;

public interface ITicketService {

    Ticket createTicketForConversation(Conversation conversation);

    TicketDTO getTicketById(Long ticketId);

    TicketDTO resolveTicket(Long ticketId, String resolutionDetails);

    List<TicketDTO> getAllTicket();

}
