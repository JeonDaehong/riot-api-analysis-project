package com.lolrpt.lol_statistices_service;

import com.lolrpt.lol_statistices_service.common.CommonRiotKey;
import com.lolrpt.lol_statistices_service.dto.UserInformationDto;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("RIOT API가 잘 연결 되는지 확인하여야 한다.")
    void RiotApiCallTest() {

        String name = "락도스";
        String summonerName = name.replaceAll(" ", "%20");

        try {

            RestTemplate restTemplate = new RestTemplate();
            String url = CommonRiotKey.API_SERVER_URL + CommonRiotKey.apiUrl.GET_SUMMONER_INFO_BY_USER_NAME + summonerName + CommonRiotKey.REQUEST_API + CommonRiotKey.MY_RIOT_API_KEY;
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
                System.out.println("Error : ResponseBodyDto --> Null ");
            }

        } catch ( Exception e ) {

            e.printStackTrace();
            System.out.println("Error : Server Error ");

        }
    }
}
