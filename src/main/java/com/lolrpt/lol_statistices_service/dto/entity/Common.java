package com.lolrpt.lol_statistices_service.dto.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass
public class Common {

    @Column(name = "CRTE_DTTM")
    private LocalDateTime createdDateTime;

    @Column(name = "UPDT_DTTM")
    private LocalDateTime updatedDateTime;

    @PrePersist
    protected void onCreate() {
        createdDateTime = LocalDateTime.now();
        updatedDateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDateTime = LocalDateTime.now();
    }

}
