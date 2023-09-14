package com.lolrpt.lol_statistices_service.service;

public interface UserChampionInfoService {

    void createUserChampionInfo(String SummerId, String puuid, long champId);

    void updateUserChampionInfo(String SummerId, String puuid, long ChampId, int kill, int death, int assist, boolean win);
}
