package com.pskwiercz.app.dto;

import com.pskwiercz.app.enums.TicketStatus;
import com.pskwiercz.app.model.Conversation;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketDTO {
    private Long id;
    private TicketStatus ticketStatus;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private Long productOrderNumber;
    private String referenceNumber;
    private String resolutionDetails;
    private Conversation conversation;
}
