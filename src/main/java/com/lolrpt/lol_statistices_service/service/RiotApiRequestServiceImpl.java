package com.lolrpt.lol_statistices_service.service;

import com.lolrpt.lol_statistices_service.common.CommonRiotKey;
import com.lolrpt.lol_statistices_service.dto.TopRankLeagueItemDto;
import com.lolrpt.lol_statistices_service.dto.TopRankLeagueListDto;
import com.lolrpt.lol_statistices_service.dto.entity.LoLUserMaster;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RiotApiRequestServiceImpl implements RiotApiRequestService {

    @Override
    @Transactional
    public void requestUserInfoEachTier() {

        requestChallengerLeaguesAPI(); // Request Challenger User


    }

    @Override
    @Transactional
    public void requestChallengerLeaguesAPI() {

        RestTemplate restTemplate = new RestTemplate();
        String url = CommonRiotKey.API_SERVER_URL + CommonRiotKey.apiUrl.GET_SUMMONER_INFO_BY_TIER_CHALLENGER + CommonRiotKey.REQUEST_API + CommonRiotKey.MY_RIOT_API_KEY;
        ResponseEntity<TopRankLeagueListDto> responseEntity = restTemplate.getForEntity(url, TopRankLeagueListDto.class);
        TopRankLeagueListDto responseBodyDto = responseEntity.getBody();

        if (responseBodyDto != null) {

            List<TopRankLeagueItemDto> topRankLeagueItemDtoList = responseBodyDto.getEntries();

            for ( TopRankLeagueItemDto topRankLeagueItemDto : topRankLeagueItemDtoList ) {

                LoLUserMaster lolUserMaster = LoLUserMaster.builder()
                        .summonerId(topRankLeagueItemDto.getSummonerId())
                        .summonerName(topRankLeagueItemDto.getSummonerName())
                        .summonerTier(responseBodyDto.getTier())
                        .summonerRank(topRankLeagueItemDto.getRank())
                        .build();// Entity 선언
            }

        }

    }
}
