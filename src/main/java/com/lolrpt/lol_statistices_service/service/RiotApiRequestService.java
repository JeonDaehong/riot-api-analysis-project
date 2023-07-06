package com.lolrpt.lol_statistices_service.service;

import org.springframework.data.repository.query.Param;

public interface RiotApiRequestService {

    void apiCountCheckMethod();

    void apiCountPlusMethod();

    void requestUserInfoEachTier();

    void requestChallengerLeaguesAPI();

    void championProficiencyUpdate();

    void requestChampionProficiency(@Param("summonerId") String summonerId);

}
