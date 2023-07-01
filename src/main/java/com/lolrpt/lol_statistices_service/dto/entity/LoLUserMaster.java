package com.lolrpt.lol_statistices_service.dto.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TB_LOL_USER_MST")
@Getter
@ToString
@NoArgsConstructor
public class LoLUserMaster {

    @Id
    @Column(name = "SUMMONER_ID", nullable = false)
    private String summonerId;

    @Column(name = "NUM", nullable = false)
    private int num;

    @Column(name = "SUMMONER_NM", nullable = false)
    private String summonerName;

    @Column(name = "SUMMONER_RANK")
    private String summonerRank;

    @Column(name = "SUMMONER_TIER")
    private String summonerTier;

    @Column(name = "PUUID")
    private String puuid;

    @Column(name = "ACONT_ID", nullable = false)
    private String accountId;

    @Builder
    public LoLUserMaster(String summonerId, int num, String summonerName, String summonerRank, String summonerTier, String puuid, String accountId) {
        this.summonerId = summonerId;
        this.num = num;
        this.summonerName = summonerName;
        this.summonerRank = summonerRank;
        this.summonerTier = summonerTier;
        this.puuid = puuid;
        this.accountId = accountId;
    }
}
