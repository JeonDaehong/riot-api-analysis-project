package com.lolrpt.lol_statistices_service.service;

import com.lolrpt.lol_statistices_service.common.ApiCount;
import com.lolrpt.lol_statistices_service.common.ApiCountCheckGlobalValue;
import com.lolrpt.lol_statistices_service.common.CommonRiotKey;
import com.lolrpt.lol_statistices_service.dto.SummonerDTO;
import com.lolrpt.lol_statistices_service.dto.TopRankLeagueItemDto;
import com.lolrpt.lol_statistices_service.dto.TopRankLeagueListDto;
import com.lolrpt.lol_statistices_service.dto.entity.LoLUserMaster;
import com.lolrpt.lol_statistices_service.repository.LoLUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiotApiRequestServiceImpl implements RiotApiRequestService {

    private final LoLUserRepository loLUserRepository;

    /**
     * API 횟수 증가 메서드
     * API 호출 Count가 Max에 도달할 시 잠시 Thread를 멈춤. 그리고 전역 변수를 초기화 함.
     */
    @Override
    @Transactional
    public void apiCountCheckMethod() {
        try {
            if ( ApiCountCheckGlobalValue.getSecondCount() == ApiCount.SECOND_MAX_COUNT ) {
                Thread.sleep(ApiCount.SECOND_TIME);
                ApiCountCheckGlobalValue.setSecondCount(0);
            }

            if ( ApiCountCheckGlobalValue.getMinuteCount() == ApiCount.MINUTE_MAX_COUNT ) {
                Thread.sleep(ApiCount.MINUTE_TIME);
                ApiCountCheckGlobalValue.setMinuteCount(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("requestChallengerLeaguesAPI Method Exception Error : {} ", e.getMessage());
        }
    }

    /**
     * Api 호출 카운트 올려주는 메서드
     */
    public void apiCountPlusMethod() {
        ApiCountCheckGlobalValue.setSecondCount(ApiCountCheckGlobalValue.getSecondCount() + 1);
        ApiCountCheckGlobalValue.setMinuteCount(ApiCountCheckGlobalValue.getMinuteCount() + 1);
    }

    @Override
    @Transactional
    public void requestUserInfoEachTier() {

        requestChallengerLeaguesAPI(); // Request Challenger User


    }

    /**
     * Challenger 유저 가져오기
     */
    @Override
    @Transactional
    public void requestChallengerLeaguesAPI() {

        try {

            // Challenger User 불러오기
            RestTemplate restTemplate = new RestTemplate();
            String url = CommonRiotKey.API_SERVER_URL + CommonRiotKey.apiUrl.GET_SUMMONER_INFO_BY_TIER_CHALLENGER + CommonRiotKey.REQUEST_API + CommonRiotKey.MY_RIOT_API_KEY;
            ResponseEntity<TopRankLeagueListDto> responseEntity = restTemplate.getForEntity(url, TopRankLeagueListDto.class);
            TopRankLeagueListDto responseBodyDto = responseEntity.getBody();

            // Api 호출한 횟수 증가
            ApiCountCheckGlobalValue.setSecondCount(ApiCountCheckGlobalValue.getSecondCount() + 1);
            ApiCountCheckGlobalValue.setMinuteCount(ApiCountCheckGlobalValue.getMinuteCount() + 1);

            if (responseBodyDto != null) {

                // Challenger 50명에 대한 정보가 담긴 List
                List<TopRankLeagueItemDto> topRankLeagueItemDtoList = responseBodyDto.getEntries();

                List<LoLUserMaster> loLUserMasterList = new ArrayList<>();
                int number = 1; // Player Number Check ( DB AutoIncrement X )
                for ( TopRankLeagueItemDto topRankLeagueItemDto : topRankLeagueItemDtoList ) {

                    apiCountCheckMethod(); // API 호출 Count가 Max에 도달할 시 잠시 Thread를 멈춤. 그리고 전역 변수를 초기화 함.

                    // Challenger 1명의 SummonerId로 해당 User의 Puuid 가져오기
                    RestTemplate getPuuidRestTemplate = new RestTemplate();
                    String getPuuidUrl = CommonRiotKey.API_SERVER_URL + CommonRiotKey.apiUrl.GET_SUMMONER_INFO_BY_SUMMONER_ID + topRankLeagueItemDto.getSummonerId() + CommonRiotKey.REQUEST_API + CommonRiotKey.MY_RIOT_API_KEY;
                    ResponseEntity<SummonerDTO> getPuuidResponseEntity = restTemplate.getForEntity(getPuuidUrl, SummonerDTO.class);
                    SummonerDTO getPuuidResponseBodyDto = getPuuidResponseEntity.getBody();

                    apiCountPlusMethod(); // Api 호출한 횟수 증가

                    // 문제가 없을 시, DB에 저장해야하는 Entity 만들기
                    if ( getPuuidResponseBodyDto != null ) {

                        LoLUserMaster lolUserMaster = LoLUserMaster.builder()
                                .summonerId(topRankLeagueItemDto.getSummonerId())
                                .num(number)
                                .summonerName(topRankLeagueItemDto.getSummonerName())
                                .summonerTier(responseBodyDto.getTier())
                                .summonerRank(topRankLeagueItemDto.getRank())
                                .puuid(getPuuidResponseBodyDto.getPuuid())
                                .accountId(getPuuidResponseBodyDto.getAccountId())
                                .build();// Entity 선언

                        log.info(lolUserMaster.toString());
                        loLUserMasterList.add(lolUserMaster);
                    }
                    number ++;
                }
                loLUserRepository.saveAll(loLUserMasterList); // JPA Save ( saveAll(); )
            }

        } catch (Exception e) {

            e.printStackTrace();
            log.error("requestChallengerLeaguesAPI Method Exception Error : {} ", e.getMessage());

        }
    }

    /**
     * 모든 유저의 챔피언 Table에 숙련도 Update 해주기
     */
    @Override
    @Transactional
    public void championProficiencyUpdate() {

        try {

            Optional<List<LoLUserMaster>> optionalLolUserMasterList = Optional.ofNullable(loLUserRepository.findAll());
            optionalLolUserMasterList.ifPresent(lolUserMasterList -> {
                apiCountCheckMethod();
                lolUserMasterList
                        .forEach(userMaster -> requestChampionProficiency(userMaster.getSummonerId()));
            });

        } catch (Exception e) {
            e.printStackTrace();
            log.error("requestChallengerLeaguesAPI Method Exception Error : {} ", e.getMessage());
        }
    }

    /**
     * 해당 서머너 아이디 ( User )의 챔피언별 숙련도 입력
     * 주의해야 할 점 : 모든 챔피언의 정보를 다 넣을 것인가 ? 숙련도 높은것만 넣을 것인가 ?
     */
    @Override
    @Transactional
    public void requestChampionProficiency(@Param("summonerId") String summonerId) {



        apiCountPlusMethod();

    }

}
