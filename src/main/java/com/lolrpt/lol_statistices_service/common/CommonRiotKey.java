package com.lolrpt.lol_statistices_service.common;

public class CommonRiotKey {

    public static final String MY_RIOT_API_KEY = "RGAPI-9dce3c66-12ee-4282-8e70-780be090d3bd"; // Riot API Key
    public static final String REQUEST_API = "?api_key=";
    public static final String API_SERVER_URL = "https://kr.api.riotgames.com";

    public static class apiUrl {

        public static final String GET_SUMMONER_INFO_BY_USER_NAME = "/lol/summoner/v4/summoners/by-name/";
        public static final String GET_SUMMONER_INFO_BY_SUMMONER_ID = "/lol/summoner/v4/summoners/";
        public static final String GET_SUMMONER_INFO_BY_TIER_CHALLENGER = "/lol/league/v4/challengerleagues/by-queue/RANKED_SOLO_5x5/";
        public static final String GET_SUMMONER_INFO_BY_TIER_GRANDMASTER = "/lol/league/v4/grandmasterleagues/by-queue/RANKED_SOLO_5x5/";
        public static final String GET_SUMMONER_INFO_BY_TIER_MASTER = "/lol/league/v4/masterleagues/by-queue/RANKED_SOLO_5x5/";
        public static final String GET_SUMMONER_INFO_BY_TIER_ETC = "/lol/league/v4/entries/RANKED_SOLO_5x5/";

    }

}
