package com.lolrpt.lol_statistices_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class RiotApiTest {

    @Test
    void RiotApiCallTest() {

        String myRiotApiKey = ""; // Riot API Key
        String serverUrl = "https://kr.api.riotgames.com";
        String testApiUrl = "/lol/summoner/v4/summoners/by-name/";
        String name = "락도스";
        String summonerName = name.replaceAll(" ", "%20");

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Riot-Token", myRiotApiKey);
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

            String url = serverUrl + testApiUrl + summonerName + "?api_key=" + myRiotApiKey;;

            String responseBody = restTemplate.getForObject(url, String.class);

            System.out.println(responseBody);

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
