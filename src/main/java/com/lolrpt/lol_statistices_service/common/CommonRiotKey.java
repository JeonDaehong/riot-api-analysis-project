package com.lolrpt.lol_statistices_service.common;

public class CommonRiotKey {

    public static final String MY_RIOT_API_KEY = "RGAPI-9c105485-fbac-4461-b5cf-70ffe0b7c646"; // Riot API Key
    public static final String REQUEST_API = "?api_key=";
    public static final String API_SERVER_URL = "https://kr.api.riotgames.com";

    public static class apiUrl {

        public static final String GET_SUMMONER_INFO_BY_USER_NAME = "/lol/summoner/v4/summoners/by-name/";
        public static final String GET_SUMMONER_INFO_BY_SUMMONER_ID = "/lol/summoner/v4/summoners/";
        public static final String GET_SUMMONER_INFO_BY_TIER_CHALLENGER = "/lol/league/v4/challengerleagues/by-queue/RANKED_SOLO_5x5/";
        public static final String GET_SUMMONER_INFO_BY_TIER_GRANDMASTER = "/lol/league/v4/grandmasterleagues/by-queue/RANKED_SOLO_5x5/";
        public static final String GET_SUMMONER_INFO_BY_TIER_MASTER = "/lol/league/v4/masterleagues/by-queue/RANKED_SOLO_5x5/";
        public static final String GET_SUMMONER_INFO_BY_TIER_ETC = "/lol/league/v4/entries/RANKED_SOLO_5x5/";
        public static final String GET_CHAMPION_PROFICIENCY_BY_SUMMONER_ID = "/lol/champion-mastery/v4/champion-masteries/by-summoner/";
        public static final String GET_MATCH_ID_BY_SUMMONER_PUUID = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/";
        public static final String GET_MATCH_BY_MATCH_ID = "https://asia.api.riotgames.com/lol/match/v5/matches/";

    }

}
