package com.lolrpt.lol_statistices_service.dto.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "TB_USER_CHAMP_INFO")
@Getter
@ToString
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class UserChampionInfo extends Common {

    @Id
    @Column(name = "SUMMONER_ID", nullable = false)
    private String summonerId;

    @Column(name = "CHAMP_ID", nullable = false)
    private long championId;

    @Column(name = "PRFCN_SCORE", nullable = false)
    private int proficiencyScore;

    @Builder
    public UserChampionInfo(String summonerId, long championId, int proficiencyScore) {
        this.summonerId = summonerId;
        this.championId = championId;
        this.proficiencyScore = proficiencyScore;
    }

}
