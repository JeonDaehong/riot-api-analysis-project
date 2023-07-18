package com.lolrpt.lol_statistices_service.dto.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@DiscriminatorColumn(name = "COMMON")
public class Common {

    @Column(name = "CRTE_DTTM")
    protected LocalDateTime createdDateTime;

    @Column(name = "UPDT_DTTM")
    protected LocalDateTime updatedDateTime;

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
