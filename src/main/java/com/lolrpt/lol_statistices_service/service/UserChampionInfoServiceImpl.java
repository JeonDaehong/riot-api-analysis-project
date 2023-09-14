package com.lolrpt.lol_statistices_service.service;

import com.lolrpt.lol_statistices_service.dto.entity.UserChampionInfo;
import com.lolrpt.lol_statistices_service.repository.UserChampionInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserChampionInfoServiceImpl implements UserChampionInfoService{

    private final UserChampionInfoRepository userChampionInfoRepository;

    @Override
    public void createUserChampionInfo(String SummerId, String puuid, long champId){
        UserChampionInfo.builder()
                .summonerId(SummerId)
                .puuid(puuid)
                .championId(champId)
                .createdDateTime(LocalDateTime.now())
                .updatedDateTime(LocalDateTime.now())
                .build();
    }
    @Override
    public void updateUserChampionInfo(String SummerId, String puuid, long champId, int kill, int death, int assist, boolean win){

        // 1. UserChampionInfo 안에 puuid에 해당하는 ChampId가 존재하는지 확인
        // 1.1 존재하지 않는다면 그대로 insert 후 return
        String check_puuid = userChampionInfoRepository.findByPuuid(puuid);
        if (check_puuid.equals("")) {
            createUserChampionInfo(SummerId, puuid, champId);
        }
        // 2. 존재하다면 해당 row에서 kill, death, assist에 kill, death, assist ++
        // 2.1 더해진 kill, death, assist로  KDA계산해서 update
        // 2.2 win == true 면 해당 row의 win count에 ++ 아니라면 defeat count에 ++
        // return
        else {

        }
    }
}
