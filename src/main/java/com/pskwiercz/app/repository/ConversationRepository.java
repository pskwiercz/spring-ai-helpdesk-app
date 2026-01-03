package com.pskwiercz.app.repository;

import com.pskwiercz.app.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}
