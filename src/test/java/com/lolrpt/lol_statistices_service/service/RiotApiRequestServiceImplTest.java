package com.lolrpt.lol_statistices_service.service;

import com.lolrpt.lol_statistices_service.common.ApiCount;
import com.lolrpt.lol_statistices_service.common.CommonRiotKey;
import com.lolrpt.lol_statistices_service.dto.SummonerDTO;
import com.lolrpt.lol_statistices_service.dto.TopRankLeagueItemDto;
import com.lolrpt.lol_statistices_service.dto.TopRankLeagueListDto;
import com.lolrpt.lol_statistices_service.dto.entity.LoLUserMaster;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

class RiotApiRequestServiceImplTest {

    @Test
    @DisplayName("Challenger User를 불러오는 API가 잘 작동하는지 확인한다.")
    void requestChallengerLeaguesAPITest() throws InterruptedException {

        // API Call Count
        int SECOND_COUNT = 0;
        int MINUTE_COUNT = 0;

        // Challenger User 불러오기
        RestTemplate restTemplate = new RestTemplate();
        String url = CommonRiotKey.API_SERVER_URL + CommonRiotKey.apiUrl.GET_SUMMONER_INFO_BY_TIER_CHALLENGER + CommonRiotKey.REQUEST_API + CommonRiotKey.MY_RIOT_API_KEY;
        ResponseEntity<TopRankLeagueListDto> responseEntity = restTemplate.getForEntity(url, TopRankLeagueListDto.class);
        TopRankLeagueListDto responseBodyDto = responseEntity.getBody();

        SECOND_COUNT ++;
        MINUTE_COUNT ++;

        if (responseBodyDto != null) {

            // Challenger 50명에 대한 정보가 담긴 List
            List<TopRankLeagueItemDto> topRankLeagueItemDtoList = responseBodyDto.getEntries();

            int checkNum = 1;

            for ( TopRankLeagueItemDto topRankLeagueItemDto : topRankLeagueItemDtoList ) {

                if ( SECOND_COUNT == ApiCount.SECOND_MAX_COUNT ) {
                    Thread.sleep(ApiCount.SECOND_TIME);
                    SECOND_COUNT = 0;
                }

                if ( MINUTE_COUNT == ApiCount.MINUTE_MAX_COUNT ) {
                    Thread.sleep(ApiCount.MINUTE_TIME);
                    MINUTE_COUNT = 0;
                }

                // Challenger 1명의 SummonerId로 해당 User의 Puuid 가져오기
                RestTemplate getPuuidRestTemplate = new RestTemplate();
                String getPuuidUrl = CommonRiotKey.API_SERVER_URL + CommonRiotKey.apiUrl.GET_SUMMONER_INFO_BY_SUMMONER_ID + topRankLeagueItemDto.getSummonerId() + CommonRiotKey.REQUEST_API + CommonRiotKey.MY_RIOT_API_KEY;
                ResponseEntity<SummonerDTO> getPuuidResponseEntity = restTemplate.getForEntity(getPuuidUrl, SummonerDTO.class);
                SummonerDTO getPuuidResponseBodyDto = getPuuidResponseEntity.getBody();

                SECOND_COUNT ++;
                MINUTE_COUNT ++;

                // 문제가 없을 시, DB에 저장해야하는 Entity 만들기
                if ( getPuuidResponseBodyDto != null ) {

                    LoLUserMaster lolUserMaster = LoLUserMaster.builder()
                            .summonerId(topRankLeagueItemDto.getSummonerId())
                            .num(checkNum)
                            .summonerName(topRankLeagueItemDto.getSummonerName())
                            .summonerTier(responseBodyDto.getTier())
                            .summonerRank(topRankLeagueItemDto.getRank())
                            .puuid(getPuuidResponseBodyDto.getPuuid())
                            .accountId(getPuuidResponseBodyDto.getAccountId())
                            .build();// Entity 선언

                    System.out.println("User " + checkNum + " : " + lolUserMaster.toString());

                } else {

                    System.out.println("User " + checkNum + " : API Call Fail ");

                }

                checkNum ++;

            }
        }
    }
}