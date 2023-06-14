package com.lolrpt.lol_statistices_service;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;


public class RiotApiTest {

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
            String responseBody = restTemplate.getForObject(url, String.class);

            System.out.println(responseBody);

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
