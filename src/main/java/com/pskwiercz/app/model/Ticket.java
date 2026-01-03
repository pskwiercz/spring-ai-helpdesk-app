package com.pskwiercz.app.model;

import com.pskwiercz.app.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Ref_Number")
    private String referenceNumber;
    @Column(name = "PO_Number")
    private Long productOrderNumber;
    private String resolutionDetails;
    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    @OneToOne
    @JoinColumn(name = "conversation_Id")
    private Conversation conversation;
}
