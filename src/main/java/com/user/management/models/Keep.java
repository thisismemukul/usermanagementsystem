package com.user.management.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Keep extends BaseEntity {

    @Lob
    private String content;

    private String ownerUsername;

//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
}
