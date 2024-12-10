package com.user.management.models;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class AuditLog extends BaseEntity {
    private String action;
    private String username;
    private Long keepId;
    private String keepContent;
    private LocalDateTime timestamp;
}
