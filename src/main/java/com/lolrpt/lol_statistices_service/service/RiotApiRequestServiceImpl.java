package com.lolrpt.lol_statistices_service.service;

import com.lolrpt.lol_statistices_service.common.ApiCount;
import com.lolrpt.lol_statistices_service.common.ApiCountCheckGlobalValue;
import com.lolrpt.lol_statistices_service.common.CommonRiotKey;
import com.lolrpt.lol_statistices_service.dto.ChampionMasteryDto;
import com.lolrpt.lol_statistices_service.dto.SummonerDTO;
import com.lolrpt.lol_statistices_service.dto.TopRankLeagueItemDto;
import com.lolrpt.lol_statistices_service.dto.TopRankLeagueListDto;
import com.lolrpt.lol_statistices_service.dto.entity.UserChampionInfo;
import com.lolrpt.lol_statistices_service.dto.entity.UserMaster;
import com.lolrpt.lol_statistices_service.repository.UserChampionInfoRepository;
import com.lolrpt.lol_statistices_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiotApiRequestServiceImpl implements RiotApiRequestService {

    private final UserRepository loLUserRepository;
    private final UserChampionInfoRepository userChampionInfoRepository;

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

    /**
     * 모든 유저 정보 가져와서 DB에 넣기
     */
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

            apiCountPlusMethod(); // Api 호출한 횟수 증가

            if (responseBodyDto != null) {

                // Challenger 50명에 대한 정보가 담긴 List
                List<TopRankLeagueItemDto> topRankLeagueItemDtoList = responseBodyDto.getEntries();

                List<UserMaster> loLUserMasterList = new ArrayList<>();
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

                        UserMaster lolUserMaster = UserMaster.builder()
                                .summonerId(topRankLeagueItemDto.getSummonerId())
                                .num(number)
                                .summonerName(topRankLeagueItemDto.getSummonerName())
                                .summonerTier(responseBodyDto.getTier())
                                .summonerRank(topRankLeagueItemDto.getRank())
                                .puuid(getPuuidResponseBodyDto.getPuuid())
                                .accountId(getPuuidResponseBodyDto.getAccountId())
                                .createdDateTime(LocalDateTime.now())
                                .updatedDateTime(LocalDateTime.now())
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
     * 모든 유저의 챔피언 Table 숙련도 Update 해주기
     */
    @Override
    @Transactional
    public void championProficiencyUpdate() {

        try {
            Optional<List<UserMaster>> optionalLolUserMasterList = Optional.ofNullable(loLUserRepository.findAll());
            optionalLolUserMasterList.ifPresent(lolUserMasterList -> {
                apiCountCheckMethod();
                lolUserMasterList
                        .forEach(userMaster -> requestChampionProficiency(userMaster.getSummonerId()));
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("championProficiencyUpdate Method Exception Error : {} ", e.getMessage());
        }
    }

    /**
     * 해당 서머너 아이디 ( User )의 챔피언별 숙련도 입력
     * 주의해야 할 점 : 모든 챔피언의 정보를 다 넣을 것인가 ? 숙련도 높은것만 넣을 것인가 ?
     */
    @Override
    @Transactional
    public void requestChampionProficiency(@Param("summonerId") String summonerId) {

        try {

            // RestTemplate List 받는 방법. 먼저 Arr 받고, 그걸 List 변환. ( getForObject 활용 )
            RestTemplate restTemplate = new RestTemplate();
            String url = CommonRiotKey.API_SERVER_URL + CommonRiotKey.apiUrl.GET_CHAMPION_PROFICIENCY_BY_SUMMONER_ID + summonerId + CommonRiotKey.REQUEST_API + CommonRiotKey.MY_RIOT_API_KEY;
            ChampionMasteryDto[] responseEntityArr = restTemplate.getForObject(url, ChampionMasteryDto[].class);

            apiCountPlusMethod();

            if (responseEntityArr != null) {

                List<ChampionMasteryDto> championMasteryDtoList = Arrays.asList(responseEntityArr);

                // JPA 해당 summonerId 와 챔피언 아이디가 championMasteryDtoList 의 챔피언 아이디와 알맞을 경우 숙련도 점수 Update
                // 즉, Mybatis 기준으로 UPDATE TABLE SET 숙련도 = #{숙련도점수}, UPDT_DTTM = NOW() WHERE SUMMONER_ID = #{summonerId} AND CHAMP_ID = #{championId} 를 JPA 로 구현.
                for ( ChampionMasteryDto championMasteryDto : championMasteryDtoList ) {

                    String thisSummonerId = championMasteryDto.getSummonerId();
                    long thisChampionId = championMasteryDto.getChampionId();
                    int proficiencyScore = championMasteryDto.getChampionPoints();

                    int returnCount = userChampionInfoRepository.findByIdAndChampId(thisSummonerId, thisChampionId);

                    if ( returnCount > 0 ) {

                        // 챔피언 숙련도 업데이트
                        userChampionInfoRepository.updateChampAndUpdatedAt(proficiencyScore, thisSummonerId, thisChampionId);

                    } else {
                        
                        // 대상이 없다면 Insert Row
                        UserChampionInfo insertUserChampionInfo = UserChampionInfo.builder()
                                .summonerId(championMasteryDto.getSummonerId())
                                .championId(championMasteryDto.getChampionId())
                                .proficiencyScore(championMasteryDto.getChampionPoints())
                                .createdDateTime(LocalDateTime.now())
                                .updatedDateTime(LocalDateTime.now())
                                .build();

                        log.info(insertUserChampionInfo.toString());
                        userChampionInfoRepository.save(insertUserChampionInfo);

                    }
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            log.error("requestChampionProficiency Method Exception Error : {} ", e.getMessage());
        }
    }
}
