package com.lolrpt.lol_statistices_service.service;

import org.springframework.data.repository.query.Param;

public interface RiotApiRequestService {

    void apiCountCheckMethod();

    void apiCountPlusMethod();

    void requestUserInfoEachTier();

    void requestChallengerLeaguesAPI();

    void championProficiencyUpdate();

    void requestChampionProficiency(@Param("summonerId") String summonerId);

    int artisanScoreCalculation(@Param("rank") String rank, @Param("tier") String tier, @Param("playCount") int playCount,
                                @Param("winRate") double winRate, @Param("proficiency") int proficiency);

}
