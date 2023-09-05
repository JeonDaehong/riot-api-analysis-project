package com.lolrpt.lol_statistices_service.service;

import com.lolrpt.lol_statistices_service.common.*;
import com.lolrpt.lol_statistices_service.dto.*;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiotApiRequestServiceImpl implements RiotApiRequestService {

    private final UserRepository loLUserRepository;
    private final UserChampionInfoRepository userChampionInfoRepository;

    @Override
    public UserMaster createUserMasterFromDto(TopRankLeagueItemDto topRankLeagueItemDto, String tier,
                                              SummonerDTO getPuuidResponseBodyDto, int number) {
        return UserMaster.builder()
                .summonerId(topRankLeagueItemDto.getSummonerId())
                .num(number)
                .summonerName(topRankLeagueItemDto.getSummonerName())
                .summonerTier(tier)
                .summonerRank(topRankLeagueItemDto.getRank())
                .puuid(getPuuidResponseBodyDto.getPuuid())
                .accountId(getPuuidResponseBodyDto.getAccountId())
                .createdDateTime(LocalDateTime.now())
                .updatedDateTime(LocalDateTime.now())
                .build();
    }

    @Override
    public UserChampionInfo createUserChampionInfoFromDto(ChampionMasteryDto championMasteryDto) {
        return UserChampionInfo.builder()
                .summonerId(championMasteryDto.getSummonerId())
                .championId(championMasteryDto.getChampionId())
                .proficiencyScore(championMasteryDto.getChampionPoints())
                .createdDateTime(LocalDateTime.now())
                .updatedDateTime(LocalDateTime.now())
                .build();
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

            ApiCountMethod.apiCountPlusMethod(); // Api 호출한 횟수 증가

            if (responseBodyDto != null) {

                // Challenger 50명에 대한 정보가 담긴 List
                List<TopRankLeagueItemDto> topRankLeagueItemDtoList = responseBodyDto.getEntries();

                List<UserMaster> loLUserMasterList = new ArrayList<>();
                int number = 1; // Player Number Check ( DB AutoIncrement X )
                for ( TopRankLeagueItemDto topRankLeagueItemDto : topRankLeagueItemDtoList ) {

                    ApiCountMethod.apiCountCheckMethod(); // API 호출 Count가 Max에 도달할 시 잠시 Thread를 멈춤. 그리고 전역 변수를 초기화 함.

                    // Challenger 1명의 SummonerId로 해당 User의 Puuid 가져오기
                    RestTemplate getPuuidRestTemplate = new RestTemplate();
                    String getPuuidUrl = CommonRiotKey.API_SERVER_URL
                                        + CommonRiotKey.apiUrl.GET_SUMMONER_INFO_BY_SUMMONER_ID
                                        + topRankLeagueItemDto.getSummonerId()
                                        + CommonRiotKey.REQUEST_API
                                        + CommonRiotKey.MY_RIOT_API_KEY;
                    ResponseEntity<SummonerDTO> getPuuidResponseEntity = restTemplate.getForEntity(getPuuidUrl, SummonerDTO.class);
                    SummonerDTO getPuuidResponseBodyDto = getPuuidResponseEntity.getBody();

                    ApiCountMethod.apiCountPlusMethod(); // Api 호출한 횟수 증가

                    // 문제가 없을 시, DB에 저장해야하는 Entity 만들기
                    if ( getPuuidResponseBodyDto != null ) {

                        UserMaster lolUserMaster = createUserMasterFromDto(topRankLeagueItemDto, responseBodyDto.getTier(), getPuuidResponseBodyDto, number++);
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
            Optional<List<UserMaster>> optionalLolUserMasterList = Optional.of(loLUserRepository.findAll());
            optionalLolUserMasterList.ifPresent(lolUserMasterList -> {
                ApiCountMethod.apiCountCheckMethod();
                lolUserMasterList
                        .forEach(this::requestChampionProficiency);
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
    public void requestChampionProficiency(@Param("userMaster") UserMaster userMaster) {

        try {

            String summonerId = userMaster.getSummonerId();
            String rank = userMaster.getSummonerRank();
            String tier = userMaster.getSummonerTier();

            // RestTemplate List 받는 방법. 먼저 Arr 받고, 그걸 List 변환. ( getForObject 활용 )
            RestTemplate restTemplate = new RestTemplate();
            String url = CommonRiotKey.API_SERVER_URL
                        + CommonRiotKey.apiUrl.GET_CHAMPION_PROFICIENCY_BY_SUMMONER_ID
                        + summonerId
                        + CommonRiotKey.REQUEST_API
                        + CommonRiotKey.MY_RIOT_API_KEY;
            ChampionMasteryDto[] responseEntityArr = restTemplate.getForObject(url, ChampionMasteryDto[].class);

            ApiCountMethod.apiCountPlusMethod();

            if (responseEntityArr != null) {

                // JPA 해당 summonerId 와 챔피언 아이디가 championMasteryDtoList 의 챔피언 아이디와 알맞을 경우 숙련도 점수 Update
                // 즉, Mybatis 기준으로 UPDATE TABLE SET 숙련도 = #{숙련도점수}, UPDT_DTTM = NOW() WHERE SUMMONER_ID = #{summonerId} AND CHAMP_ID = #{championId} 를 JPA 로 구현.
                for ( ChampionMasteryDto championMasteryDto : responseEntityArr) {

                    long thisChampionId = championMasteryDto.getChampionId();
                    int proficiencyScore = championMasteryDto.getChampionPoints();

                    int returnCount = userChampionInfoRepository.findByIdAndChampId(summonerId, thisChampionId);
                    if ( returnCount > 0 ) {

                        // 챔피언 숙련도 업데이트
                        userChampionInfoRepository.updateChampAndUpdatedAt(proficiencyScore, summonerId, thisChampionId);

                        // 장인 점수 받아오기
                        int artisanScore = 0; // artisanScoreCalculation( ??? );

                        // 장인 점수 업데이트
                        userChampionInfoRepository.updateArtisanScore(artisanScore, summonerId, thisChampionId);

                    } else {
                        
                        // 대상이 없다면 Insert Row
                        UserChampionInfo insertUserChampionInfo = createUserChampionInfoFromDto(championMasteryDto);
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


    /**
     * 쪼꼬롤빵에서 측정하는 장인점수 통계 공식 점수
     */
    @Override
    @Transactional
    public int artisanScoreCalculation(@Param("rank") String rank, @Param("tier") String tier, @Param("playCount") int playCount,
                                       @Param("winRate") double winRate, @Param("proficiency") int proficiency) {

        if ( playCount < 50 ) return 0; // 판수가 50판이 넘지 않으면, 장인 점수 = 0

        int artisanScore = 0;

        // Rank & Tier로 장인 점수 추가 게산
        switch (rank) {
            case Rank.DIAMOND:
                switch (tier) {
                    case Tier.IV_tier:
                        artisanScore += 0;
                        break;
                    case Tier.III_tier:
                        artisanScore += 30;
                        break;
                    case Tier.II_tier:
                        artisanScore += 60;
                        break;
                    case Tier.I_tier:
                        artisanScore += 90;
                        break;
                }
                break;
            case Rank.MASTER:
                artisanScore += 120;
                break;
            case Rank.GRANDMASTER:
                artisanScore += 150;
                break;
            case Rank.CHALLENGER:
                artisanScore += 210;
                break;
        }

        // 승률로 장인 점수 추가 계산
        int intWinRate = (int) winRate;
        int difference = Math.abs(intWinRate - 50);
        int rateScore = (intWinRate > 50) ? difference * 5 : -difference * 5;
        artisanScore += rateScore;

        // 판수로 장인 점수 추가 계산
        artisanScore += playCount;

        // 숙련도로 장인 점수 추가 계산
        artisanScore += (proficiency / 20000);

        return Math.max(artisanScore, 0); // 혹시 음수일 경우 0점 return
    }

    public void inititalUserChampionInfo() throws InterruptedException {
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

                    UserMaster lolUserMaster = createUserMasterFromDto(topRankLeagueItemDto, responseBodyDto.getTier(), getPuuidResponseBodyDto, checkNum);

                    System.out.println("User " + checkNum + " : " + lolUserMaster.toString());

                    // get match id by puuid (only solo rank)
                    // start time. end time, count, 지정할 수 있게 바꿔야하는건가
                    int start = 0;
                    MatchIdDto matchIdDto = new MatchIdDto();
                    for(;;) {

                        /* puuid에 대한 match id 전부 가져오기 */
                        RestTemplate getMatchIdRestTemplate = new RestTemplate();
                        String getMatchIdUrl = CommonRiotKey.apiUrl.GET_MATCH_ID_BY_SUMMONER_PUUID + getPuuidResponseBodyDto.getPuuid()
                                + "/ids?queue=420&start=" + start + "&count=100&type=ranked" + "&api_key=" + CommonRiotKey.MY_RIOT_API_KEY;
                        ResponseEntity<String> getMatchIdResponseEntity = getMatchIdRestTemplate.getForEntity(getMatchIdUrl, String.class);
                        String matchIds = getMatchIdResponseEntity.getBody();
                        if(matchIds.equals("[]")){break;}

                        List<String> matchIdList = Arrays.stream(matchIds.replaceAll("[\\[\\]\"\"]", "").split(","))
                                .map(String::trim)
                                .collect(Collectors.toList()); // String -> String[] -> List<String>
                        //matchIdDto = new MatchIdDto(matchIdList);
                        matchIdDto.addMatchId(matchIdList);
                        start += 100;
                    }

                    // DB insert
                    // 리스트에서 골라 넣는 작업 필요

                    SECOND_COUNT ++;
                    MINUTE_COUNT ++;

                    // get match data for each match id
                    RestTemplate getMatchRestTemplate = new RestTemplate();
                    for(String matchId : matchIdDto.getMatchIds()){
                        if ( SECOND_COUNT == ApiCount.SECOND_MAX_COUNT ) {
                            Thread.sleep(ApiCount.SECOND_TIME);
                            SECOND_COUNT = 0;
                        }

                        if ( MINUTE_COUNT == ApiCount.MINUTE_MAX_COUNT ) {
                            Thread.sleep(ApiCount.MINUTE_TIME);
                            MINUTE_COUNT = 0;
                        }

                        String getMatchUrl = CommonRiotKey.apiUrl.GET_MATCH_BY_MATCH_ID + matchId
                                + "/" + CommonRiotKey.REQUEST_API + CommonRiotKey.MY_RIOT_API_KEY;
                        ResponseEntity<MatchDto> getMatchResponseEntity = getMatchRestTemplate.getForEntity(getMatchUrl, MatchDto.class);
                        MatchDto getMatchResponseBodyDto = getMatchResponseEntity.getBody();
                        System.out.println(getMatchResponseBodyDto);

                        SECOND_COUNT ++;
                        MINUTE_COUNT ++;

                        // InfoDto.participants[] 안에 있는 data 사용
                        // championId로 DB table에 해당 챔피언 플레이 횟수 증가
                        // assists, deaths, kills를 해당 챔피언 row에 증가 -> kda
                        // win == true 면 해당 챔피언 win++ false면 lose++
                        ParticipantDto participantDto = getMatchResponseBodyDto.getInfo().getParticipants().get(0);
                        int assists = participantDto.getAssists();
                        int deaths = participantDto.getDeaths();
                        int kills = participantDto.getKills();
                        double kda = deaths == 0 ? (double) (assists + kills) / 1 : (double) (assists + kills) / deaths;
                        boolean win = participantDto.isWin();


                        System.out.println("assists = " + assists);
                        System.out.println("deaths = " + deaths);
                        System.out.println("kills = " + kills);
                        System.out.println("kda = " + kda);
                        System.out.println("win = " + win);
                    }
                } else {
                    System.out.println("User " + checkNum + " : API Call Fail ");

                }
                checkNum ++;

            }
        }
    }


}
