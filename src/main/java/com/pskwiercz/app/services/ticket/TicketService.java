package com.pskwiercz.app.services.ticket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pskwiercz.app.dto.TicketDTO;
import com.pskwiercz.app.enums.TicketStatus;
import com.pskwiercz.app.model.Conversation;
import com.pskwiercz.app.model.Ticket;
import com.pskwiercz.app.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.text.RandomStringGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService implements ITicketService {
    private final TicketRepository ticketRepository;
    private final ObjectMapper objectMapper;


    @Override
    public Ticket createTicketForConversation(Conversation conversation) {
        Ticket ticket = new Ticket();
        ticket.setConversation(conversation);
        ticket.setTicketStatus(TicketStatus.OPENED);
        ticket.setResolvedAt(null);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setReferenceNumber(generateRandomAlphanumeric());
        return ticket;
    }

    @Override
    public TicketDTO getTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .map(ticket -> objectMapper.convertValue(ticket, TicketDTO.class))
                .orElseThrow(() -> new EntityNotFoundException("Ticket with id " + ticketId + " not found"));
    }

    @Override
    public TicketDTO resolveTicket(Long ticketId, String resolutionDetails) {
        return null;
    }

    @Override
    public List<TicketDTO> getAllTicket() {
        return ticketRepository.findAll()
                .stream()
                .map(ticket -> objectMapper.convertValue(ticket, TicketDTO.class))
                .collect(Collectors.toList());
    }

    private String generateRandomAlphanumeric() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z').filteredBy(Character::isLetterOrDigit)
                .get();
        return generator.generate(10).toUpperCase();
    }
}