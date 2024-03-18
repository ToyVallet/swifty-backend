package com.swifty.bank.server.core.domain;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;

@Getter
@MappedSuperclass
public class BaseEntity {
    @CreatedDate
    private LocalDate createdDate;
    @LastModifiedDate
    private LocalDate lastModifiedDate;
    private boolean isDeleted = false;

    @PrePersist
    public void onPrePersist() {
        this.createdDate = LocalDate.now();
        this.lastModifiedDate = this.createdDate;
    }

    @PreUpdate
    public void onPreUpdate() {
        this.lastModifiedDate = LocalDate.now();
    }

    public void delete() {
        this.isDeleted = true;
    }
}