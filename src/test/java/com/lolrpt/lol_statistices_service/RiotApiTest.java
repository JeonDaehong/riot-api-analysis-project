package com.lolrpt.lol_statistices_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;

@SpringBootTest
public class RiotApiTest {

    @Test
    void RiotApiCallTest() {

        String myRiotApiKey = ""; // Riot API Key
        String serverUrl = "https://kr.api.riotgames.com";
        String testUrl = "/lol/summoner/v4/summoners/by-name/";

        String name = "";
        String summonerName = name.replaceAll(" ", "%20");

        try {
            // RequestURL 설정하기
            String urlstr = serverUrl + testUrl + summonerName + "?api_key=" + myRiotApiKey;
            URL url = new URL(urlstr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // GET 방식으로 데이터를 가져오기
            urlConnection.setRequestMethod("GET");


        } catch ( Exception e ) {

        }

    }

}
