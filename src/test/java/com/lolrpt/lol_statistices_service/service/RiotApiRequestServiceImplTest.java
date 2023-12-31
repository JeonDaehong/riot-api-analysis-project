package com.lolrpt.lol_statistices_service.service;

import com.lolrpt.lol_statistices_service.common.ApiCount;
import com.lolrpt.lol_statistices_service.common.CommonRiotKey;
import com.lolrpt.lol_statistices_service.dto.*;
import com.lolrpt.lol_statistices_service.dto.entity.UserMaster;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

                    UserMaster lolUserMaster = UserMaster.builder()
                            .summonerId(topRankLeagueItemDto.getSummonerId())
                            .num(checkNum)
                            .summonerName(topRankLeagueItemDto.getSummonerName())
                            .summonerTier(responseBodyDto.getTier())
                            .summonerRank(topRankLeagueItemDto.getRank())
                            .puuid(getPuuidResponseBodyDto.getPuuid())
                            .accountId(getPuuidResponseBodyDto.getAccountId())
                            .build();// Entity 선언

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
                    System.out.println(matchIdDto);

                    SECOND_COUNT ++;
                    MINUTE_COUNT ++;

                    // get match data for each match id
                    RestTemplate getMatchRestTemplate = new RestTemplate();

                    for(String matchId : matchIdDto.getMatchIds()){
                        try{
                            String getMatchUrl = CommonRiotKey.apiUrl.GET_MATCH_BY_MATCH_ID + matchId
                                    + "/" + CommonRiotKey.REQUEST_API + CommonRiotKey.MY_RIOT_API_KEY;
                            ResponseEntity<MatchDto> getMatchResponseEntity = getMatchRestTemplate.getForEntity(getMatchUrl, MatchDto.class);
                            MatchDto getMatchResponseBodyDto = getMatchResponseEntity.getBody();
                            System.out.println(getMatchResponseBodyDto);

                            SECOND_COUNT ++;
                            MINUTE_COUNT ++;

                            int index = 0;
                            for(ParticipantDto participantDto : getMatchResponseBodyDto.getInfo().getParticipants()) {
                                if(participantDto.getPuuid().equals(getPuuidResponseBodyDto.getPuuid())){
                                    break;
                                }
                                index++;
                            }

                            ParticipantDto participantDto = getMatchResponseBodyDto.getInfo().getParticipants().get(index);
                            String puuid = participantDto.getPuuid();
                            String SummonerName = participantDto.getSummonerName();
                            int championId = participantDto.getChampionId();
                            int assists = participantDto.getAssists();
                            int deaths = participantDto.getDeaths();
                            int kills = participantDto.getKills();
                            double kda = deaths == 0 ? (double) (assists + kills) / 1 : (double) (assists + kills) / deaths;
                            boolean win = participantDto.isWin();

                            System.out.println("SummonerName = " + SummonerName);
                            System.out.println("puuid = " + puuid);
                            System.out.println("championId = " + championId);
                            System.out.println("assists = " + assists);
                            System.out.println("deaths = " + deaths);
                            System.out.println("kills = " + kills);
                            System.out.println("kda = " + kda);
                            System.out.println("win = " + win);

                        } catch (HttpClientErrorException ex){
                            if(ex.getStatusCode() == HttpStatus.NOT_FOUND){
                                System.out.println("Ignoring 404 Not Found");
                            }
                        }
                    }
                } else {
                    System.out.println("User " + checkNum + " : API Call Fail ");
                }
                checkNum ++;
            }
        }
    }

    @Test
    @DisplayName("get match id test")
    void requestMatchIdTest(){

        RestTemplate getMatchIdRestTemplate = new RestTemplate();
        String getMatchIdUrl = CommonRiotKey.apiUrl.GET_MATCH_ID_BY_SUMMONER_PUUID + "5U3sFTO_OZ7s7hzmz1-FLKah7IrcP4f6yUMbZpWj7Ilef-vApd1GW9nc23Cf0pJshvc9mX_L1sE3-Q"
                + "/ids?queue=420&start=0&count=20" + "&api_key=" + CommonRiotKey.MY_RIOT_API_KEY;

        ResponseEntity<String> getMatchIdResponseEntity = getMatchIdRestTemplate.getForEntity(getMatchIdUrl, String.class);
        String matchIds = getMatchIdResponseEntity.getBody();
        System.out.println("Match ID "  + " : " + matchIds);
        matchIds = matchIds.replaceAll("[\\[\\]\"\"]", "");
        String[] matchIdSplit =  matchIds.split(",");
        System.out.println("Match ID "  + " : " + matchIdSplit[0]);
        List<String> matchIdList = new ArrayList<>();

        for(String str : matchIdSplit){
            matchIdList.add(str.trim());
        }
        MatchIdDto getMatchIdResponseBodyDto = new MatchIdDto(matchIdList);
        System.out.println(getMatchIdResponseBodyDto);
    }

    @Test
    @DisplayName("get Match data Test")
    void requestMatchTest(){

        RestTemplate getMatchRestTemplate = new RestTemplate();
        String getMatchUrl = CommonRiotKey.apiUrl.GET_MATCH_BY_MATCH_ID + "KR_6581034670"
                + "/" + CommonRiotKey.REQUEST_API + CommonRiotKey.MY_RIOT_API_KEY;
        ResponseEntity<MatchDto> getMatchResponseEntity = getMatchRestTemplate.getForEntity(getMatchUrl, MatchDto.class);
        MatchDto getMatchResponseBodyDto = getMatchResponseEntity.getBody();
        System.out.println(getMatchResponseBodyDto);
    }

    @Test
    @DisplayName("닉네임 변천사")
    void nicknameHistoryTest() throws InterruptedException {
        int SECOND_COUNT = 0;
        int MINUTE_COUNT = 0;
        int start = 0;
        MatchIdDto matchIdDto = new MatchIdDto();
//        String target_puuid = "3aNxkVCVPvg-wYWpkQwj_rfMuw0vLAFru-08QD-McvsJimMVx8D_AeH3L7YKnEYZhyNC1yW4CC-UBg";
        String target_puuid = "cszFI8fk6Hap39UoIKVxTRUeWwCQJr-OaCHybVGV9DYFgracukESsKv8FrdJHNY-okZhMCV8tVyOtw";
        List<String> nickName = new ArrayList<String>();
        for (;;) {
            if ( SECOND_COUNT == ApiCount.SECOND_MAX_COUNT ) {
                Thread.sleep(ApiCount.SECOND_TIME);
                SECOND_COUNT = 0;
            }

            if ( MINUTE_COUNT == ApiCount.MINUTE_MAX_COUNT ) {
                Thread.sleep(ApiCount.MINUTE_TIME);
                MINUTE_COUNT = 0;
            }

            SECOND_COUNT++;
            MINUTE_COUNT++;

            /* puuid에 대한 match id 전부 가져오기 */
            RestTemplate getMatchIdRestTemplate = new RestTemplate();
            String getMatchIdUrl = CommonRiotKey.apiUrl.GET_MATCH_ID_BY_SUMMONER_PUUID + target_puuid
                    + "/ids?queue=420&start=" + start + "&count=100&type=ranked" + "&api_key=" + CommonRiotKey.MY_RIOT_API_KEY;
            ResponseEntity<String> getMatchIdResponseEntity = getMatchIdRestTemplate.getForEntity(getMatchIdUrl, String.class);
            String matchIds = getMatchIdResponseEntity.getBody();
            if (matchIds.equals("[]")) {
                break;
            }

            List<String> matchIdList = Arrays.stream(matchIds.replaceAll("[\\[\\]\"\"]", "").split(","))
                    .map(String::trim)
                    .collect(Collectors.toList()); // String -> String[] -> List<String>
            //matchIdDto = new MatchIdDto(matchIdList);
            matchIdDto.addMatchId(matchIdList);
            start += 100;
        }

        // get match data for each match id
        RestTemplate getMatchRestTemplate = new RestTemplate();
        for (String matchId : matchIdDto.getMatchIds()) {
            SECOND_COUNT++;
            MINUTE_COUNT++;

            if ( SECOND_COUNT == ApiCount.SECOND_MAX_COUNT ) {
                Thread.sleep(ApiCount.SECOND_TIME);
                SECOND_COUNT = 0;
            }

            if ( MINUTE_COUNT == ApiCount.MINUTE_MAX_COUNT ) {
                Thread.sleep(ApiCount.MINUTE_TIME);
                MINUTE_COUNT = 0;
            }

            MatchDto getMatchResponseBodyDto = null;

            try{
                String getMatchUrl = CommonRiotKey.apiUrl.GET_MATCH_BY_MATCH_ID + matchId
                        + "/" + CommonRiotKey.REQUEST_API + CommonRiotKey.MY_RIOT_API_KEY;
                ResponseEntity<MatchDto> getMatchResponseEntity = getMatchRestTemplate.getForEntity(getMatchUrl, MatchDto.class);
                getMatchResponseBodyDto = getMatchResponseEntity.getBody();
                System.out.println(getMatchResponseBodyDto);

                int index = 0;
                for(ParticipantDto participantDto : getMatchResponseBodyDto.getInfo().getParticipants()) {
                    if(participantDto.getPuuid().equals(target_puuid)){
                        break;
                    }
                    index++;
                }

                ParticipantDto participantDto = getMatchResponseBodyDto.getInfo().getParticipants().get(index);
                String puuid = participantDto.getPuuid();
                String summonerId = participantDto.getSummonerId();
                String SummonerName = participantDto.getSummonerName();
                int championId = participantDto.getChampionId();
                int assists = participantDto.getAssists();
                int deaths = participantDto.getDeaths();
                int kills = participantDto.getKills();
                double kda = deaths == 0 ? (double) (assists + kills) / 1 : (double) (assists + kills) / deaths;
                boolean win = participantDto.isWin();

                System.out.println("SummonerId = " + summonerId);
                System.out.println("SummonerName = " + SummonerName);
                System.out.println("puuid = " + puuid);
                System.out.println("championId = " + championId);
                System.out.println("assists = " + assists);
                System.out.println("deaths = " + deaths);
                System.out.println("kills = " + kills);
                System.out.println("kda = " + kda);
                System.out.println("win = " + win);

                if(!nickName.contains(SummonerName)){
                    nickName.add(SummonerName);
                }
            } catch (HttpClientErrorException ex){
                if(ex.getStatusCode() == HttpStatus.NOT_FOUND){
                    System.out.println("Ignoring 404 Not Found");
                }
            }
        }
        System.out.println("nickname history = " + nickName);
    }
}