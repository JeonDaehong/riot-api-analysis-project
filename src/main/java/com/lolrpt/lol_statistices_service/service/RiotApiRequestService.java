package com.lolrpt.lol_statistices_service.service;

import com.lolrpt.lol_statistices_service.dto.ChampionMasteryDto;
import com.lolrpt.lol_statistices_service.dto.SummonerDTO;
import com.lolrpt.lol_statistices_service.dto.TopRankLeagueItemDto;
import com.lolrpt.lol_statistices_service.dto.entity.UserChampionInfo;
import com.lolrpt.lol_statistices_service.dto.entity.UserMaster;
import org.springframework.data.repository.query.Param;

public interface RiotApiRequestService {

    UserMaster createUserMasterFromDto(TopRankLeagueItemDto topRankLeagueItemDto, String tier,
                                       SummonerDTO getPuuidResponseBodyDto, int number);

    UserChampionInfo createUserChampionInfoFromDto(ChampionMasteryDto championMasteryDto);

    void requestUserInfoEachTier();

    void requestChallengerLeaguesAPI();

    void championProficiencyUpdate();

    void requestChampionProficiency(UserMaster userMaster);

    int artisanScoreCalculation(String rank, String tier, int playCount, double winRate, int proficiency);

}
