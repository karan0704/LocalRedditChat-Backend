package com.localredditchat.localredditchatbackend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "chat_metadata")
public class ChatMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatId; // unique chat identifier

    @ManyToMany
    @JoinTable(name = "chat_participants",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> participants;

    private LocalDateTime lastMessageTimestamp;

    private String lastMessagePreview;
}
