package com.softwareengineering.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, length = 32)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", length = 32)
    private String email;

    @Column(name = "sex")
    private Character sex;

    @Column(name = "register_date", nullable = false)
    private Instant registerDate = Instant.now();

    @Column(name = "last_modify_date", nullable = false)
    private Instant lastModifyDate = Instant.now();

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sessionId;

    @PrePersist
    public void prePersist() {
        if (registerDate == null) {
            registerDate = Instant.now();
        }
        if (lastModifyDate == null) {
            lastModifyDate = Instant.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        lastModifyDate = Instant.now();
    }

}