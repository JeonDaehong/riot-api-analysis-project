package com.lolrpt.lol_statistices_service.repository;

import com.lolrpt.lol_statistices_service.dto.entity.UserChampionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserChampionInfoRepository extends JpaRepository<UserChampionInfo, Object> {

    /**
     * 챔피언 숙련도 업데이트
     */
    @Modifying
    @Query("UPDATE UserChampionInfo SET proficiencyScore = :proficiencyScore, updatedDateTime = CURRENT_TIMESTAMP WHERE summonerId = :summonerId AND championId = :championId")
    void updateChampAndUpdatedAt(@Param("proficiencyScore") int proficiencyScore, @Param("summonerId") String summonerId, @Param("championId") long championId);

    /**
     * 장인 점수 업데이트
     */
    @Modifying
    @Query("UPDATE UserChampionInfo SET artisanScore = :artisanScore, updatedDateTime = CURRENT_TIMESTAMP WHERE summonerId = :summonerId AND championId = :championId")
    void updateArtisanScore(@Param("artisanScore") int artisanScore, @Param("summonerId") String summonerId, @Param("championId") long championId);

    /**
     * DB에 소환사 + 챔피언 정보가 있는지 확인
     */
    @Query(value = "SELECT COUNT(*) FROM UserChampionInfo WHERE summonerId = :summonerId AND championId = :championId", nativeQuery = true)
    int findByIdAndChampId(@Param("summonerId") String summonerId, @Param("championId") long championId);



    @Query(value = "SELECT puuid FROM UserChampionInfo where puuid = :puuid")
    String findByPuuid(@Param("puuid") String puuid);

    @Modifying
    @Query(value = "UPDATE UserChampionInfo SET killCount = :kill, deathCount = :death, assistCount = :assist, playCount = :play_count, winCount = :win_count, lossCount = :defeat_count, winRate = :win_rate, kda = :kda WHERE puuid = :puuid AND championId = :champId")
    void updateUserChampionInfo(@Param("kill") int kill, @Param("death") int death, @Param("assist") int assist, @Param("play_count") int play_count, @Param("win_count") int win_count, @Param("defeat_count") int defeat_count, @Param("win_rate") double win_rate, @Param("kda") double kda);

}
