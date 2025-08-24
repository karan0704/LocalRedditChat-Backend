package com.localredditchat.localredditchatbackend.repository;

import com.localredditchat.localredditchatbackend.model.ChatMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMetadataRepository extends JpaRepository<ChatMetadata, Long> {
    List<ChatMetadata> findByParticipants_IdOrderByLastMessageTimestampDesc(Long userId);
}
