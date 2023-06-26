package com.lolrpt.lol_statistices_service.service;

import com.lolrpt.lol_statistices_service.common.CommonRiotKey;
import com.lolrpt.lol_statistices_service.dto.SummonerDTO;
import com.lolrpt.lol_statistices_service.dto.TopRankLeagueItemDto;
import com.lolrpt.lol_statistices_service.dto.TopRankLeagueListDto;
import com.lolrpt.lol_statistices_service.dto.entity.LoLUserMaster;
import com.lolrpt.lol_statistices_service.repository.RiotApiRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiotApiRequestServiceImpl implements RiotApiRequestService {

    private final RiotApiRequestRepository riotApiRequestRepository;

    @Override
    @Transactional
    public void requestUserInfoEachTier() {

        requestChallengerLeaguesAPI(); // Request Challenger User


    }

    @Override
    @Transactional
    public void requestChallengerLeaguesAPI() {

        try {

            // Challenger User 불러오기
            RestTemplate restTemplate = new RestTemplate();
            String url = CommonRiotKey.API_SERVER_URL + CommonRiotKey.apiUrl.GET_SUMMONER_INFO_BY_TIER_CHALLENGER + CommonRiotKey.REQUEST_API + CommonRiotKey.MY_RIOT_API_KEY;
            ResponseEntity<TopRankLeagueListDto> responseEntity = restTemplate.getForEntity(url, TopRankLeagueListDto.class);
            TopRankLeagueListDto responseBodyDto = responseEntity.getBody();

            if (responseBodyDto != null) {

                // Challenger 50명에 대한 정보가 담긴 List
                List<TopRankLeagueItemDto> topRankLeagueItemDtoList = responseBodyDto.getEntries();

                for ( TopRankLeagueItemDto topRankLeagueItemDto : topRankLeagueItemDtoList ) {

                    // Challenger 1명의 SummonerId로 해당 User의 Puuid 가져오기
                    RestTemplate getPuuidRestTemplate = new RestTemplate();
                    String getPuuidUrl = CommonRiotKey.API_SERVER_URL + CommonRiotKey.apiUrl.GET_SUMMONER_INFO_BY_SUMMONER_ID + topRankLeagueItemDto.getSummonerId() + CommonRiotKey.REQUEST_API + CommonRiotKey.MY_RIOT_API_KEY;
                    ResponseEntity<SummonerDTO> getPuuidResponseEntity = restTemplate.getForEntity(getPuuidUrl, SummonerDTO.class);
                    SummonerDTO getPuuidResponseBodyDto = getPuuidResponseEntity.getBody();

                    // 문제가 없을 시, DB에 저장해야하는 Entity 만들기
                    if ( getPuuidResponseBodyDto != null ) {

                        LoLUserMaster lolUserMaster = LoLUserMaster.builder()
                                .summonerId(topRankLeagueItemDto.getSummonerId())
                                .summonerName(topRankLeagueItemDto.getSummonerName())
                                .summonerTier(responseBodyDto.getTier())
                                .summonerRank(topRankLeagueItemDto.getRank())
                                .puuid(getPuuidResponseBodyDto.getPuuid())
                                .accountId(getPuuidResponseBodyDto.getAccountId())
                                .build();// Entity 선언

                        riotApiRequestRepository.save(lolUserMaster); // JPA Save

                    }
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
            log.error("requestChallengerLeaguesAPI Method Exception Error : {} ", e.getMessage());
            throw e;

        }
    }
}
