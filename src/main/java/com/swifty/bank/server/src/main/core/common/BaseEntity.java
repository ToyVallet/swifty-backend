package com.swifty.bank.server.src.main.core.common;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
}