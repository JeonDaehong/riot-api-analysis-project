package com.lolrpt.lol_statistices_service.service;

public interface UserChampionInfoService {

    void updateUserChampionInfo(String SummerId, String puuid, long ChampId, int kill, int death, int assist, boolean win);
}
