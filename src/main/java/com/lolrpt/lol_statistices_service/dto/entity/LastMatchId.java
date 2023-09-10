package com.lolrpt.lol_statistices_service.dto.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_LAST_MATCH_ID")
@Getter
@ToString
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class LastMatchId extends Common {

    @Id
    @Column(name = "PUUID", nullable = false)
    private String puuid;

    @Column(name = "MATCH_ID", nullable = false)
    private String matchId;

    @Column(name = "LAST_DTTM")
    private LocalDateTime lastDateTime;

    @Builder
    public LastMatchId(String puuid, String matchId, LocalDateTime lastDateTime,
                       LocalDateTime createdDateTime, LocalDateTime updatedDateTime) {
        this.puuid = puuid;
        this.matchId = matchId;
        this.lastDateTime = lastDateTime;
        this.createdDateTime = createdDateTime;
        this.updatedDateTime = updatedDateTime;
    }

}

