package com.user.management.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class Keep extends BaseEntity {

    @Lob
    private String content;

    private String ownerUsername;

//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
}
