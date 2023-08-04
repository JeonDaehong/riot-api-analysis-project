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
    @Modifying
    @Query("SELECT COUNT(UserChampionInfo.summonerId) FROM UserChampionInfo WHERE summonerId = :summonerId AND championId = :championId")
    int findByIdAndChampId(@Param("summonerId") String summonerId, @Param("championId") long championId);

}
