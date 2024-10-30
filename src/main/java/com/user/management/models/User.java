package com.user.management.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor

@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User extends BaseEntity {

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Size(max = 50)
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, max = 120, message = "Password must be at least 8 characters")
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    // Account status fields
    @Column(nullable = false)
    private boolean enabled = false; // False until account is verified
    private boolean accountNonLocked = true;
    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;

    // Optional security field
    private int failedLoginAttempts = 0;

    private LocalDate accountExpiryDate;
    private LocalDate credentialsExpiryDate;
    private String twoFactorSecret;
    private boolean isTwoFactorEnabled = false;
    private String signUpMethod;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "role_id" )
    @JsonBackReference
    @ToString.Exclude
    private Role role;



    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginTime;
    private LocalDateTime passwordResetTime;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Password Encryption
    public void setPassword(String password) {
        this.password = new BCryptPasswordEncoder().encode(password);
    }

    // Account management methods
    public void lockAccount() {
        this.accountNonLocked = false;
    }

    public void unlockAccount() {
        this.accountNonLocked = true;
        this.failedLoginAttempts = 0;
    }

    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return getId() != null && getId().equals(((User) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
