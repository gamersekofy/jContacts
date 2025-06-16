package com.uzair.jContacts.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "contacts",
        indexes = @Index(columnList = "lastName, firstName"),
        uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @NotBlank(message = "First name is required")
    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Email(message = "Invalid email format")
    @Column(unique = true, length = 100)
    private String email;

    @Pattern(regexp = "^\\+?[0-9\\s()-]{7,20}$", message = "Invalid phone number format")
    @Column(length = 20)
    private String phone;

    private String address;

    private String company;

    private String jobTitle;

    @Size(max = 1000)
    @Column(length = 1000)
    private String notes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastModified;
}
