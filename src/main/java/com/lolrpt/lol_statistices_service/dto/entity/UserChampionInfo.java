package com.lolrpt.lol_statistices_service.dto.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column(name = "PUUID", nullable = false)
    private String puuid;

    @Column(name = "CHAMP_ID", nullable = false)
    private long championId;

    @Column(name = "KILL_COUNT", nullable = false)
    private int killCount;

    @Column(name = "DEATH_COUNT", nullable = false)
    private int deathCount;

    @Column(name = "ASSIST_COUNT", nullable = false)
    private int assistCount;

    @Column(name = "PLAY_COUNT", nullable = false)
    private int playCount;

    @Column(name = "WIN_COUNT", nullable = false)
    private int winCount;

    @Column(name = "LOSS_COUNT", nullable = false)
    private int lossCount;

    @Column(name = "WIN_RATE", nullable = false)
    private double winRate;

    @Column(name = "KDA", nullable = false)
    private double kda;

    @Column(name = "PRFCN_SCORE", nullable = false)
    private int proficiencyScore;

    @Column(name = "ARTISAN_SCORE", nullable = false)
    private int artisanScore;

    @Builder
    public UserChampionInfo(String summonerId, String puuid, long championId,
                            LocalDateTime createdDateTime, LocalDateTime updatedDateTime) {
        this.summonerId = summonerId;
        this.puuid = puuid;
        this.championId = championId;
        this.createdDateTime = createdDateTime;
        this.updatedDateTime = updatedDateTime;
    }

}
