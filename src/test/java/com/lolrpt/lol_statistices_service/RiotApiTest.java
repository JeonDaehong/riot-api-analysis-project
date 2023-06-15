package com.lolrpt.lol_statistices_service;

import com.lolrpt.lol_statistices_service.dto.UserInformationDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class RiotApiTest {

    /**
     * Riot API Request & Response Test
     */
    @Test
    void RiotApiCallTest() {

        String myRiotApiKey = ""; // Riot API Key
        String serverUrl = "https://kr.api.riotgames.com";
        String testApiUrl = "/lol/summoner/v4/summoners/by-name/";
        String name = "";
        String summonerName = name.replaceAll(" ", "%20");

        try {

            RestTemplate restTemplate = new RestTemplate();
            String url = serverUrl + testApiUrl + summonerName + "?api_key=" + myRiotApiKey;;
            ResponseEntity<UserInformationDto> responseEntity = restTemplate.getForEntity(url, UserInformationDto.class);
            UserInformationDto responseBodyDto = responseEntity.getBody();

            // 응답 상태코드 확인
            HttpStatus statusCode = responseEntity.getStatusCode();
            System.out.println("응답 상태코드: " + statusCode);

            // 응답 헤더 확인
            HttpHeaders headers = responseEntity.getHeaders();
            System.out.println("응답 헤더: " + headers);

            // 정보 확인
            if (responseBodyDto != null) {
                System.out.println("Response Ajax : " + responseBodyDto.toString());
                System.out.println("accountId : " + responseBodyDto.getAccountId());
                System.out.println("profileIconId : " + responseBodyDto.getProfileIconId());
                System.out.println("revisionDate : " + responseBodyDto.getRevisionDate());
                System.out.println("name : " + responseBodyDto.getName());
                System.out.println("id : " + responseBodyDto.getId());
                System.out.println("puuid : " + responseBodyDto.getPuuid());
                System.out.println("summonerLevel : " + responseBodyDto.getSummonerLevel());
            } else {
                System.out.println("Error : responseBodyDto --> null ");
            }

        } catch ( Exception e ) {

            e.printStackTrace();

        }
    }
}
