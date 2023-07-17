package com.lolrpt.lol_statistices_service.repository;

import com.lolrpt.lol_statistices_service.dto.entity.UserChampionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserChampionInfoRepository extends JpaRepository<UserChampionInfo, Object> {

    @Modifying
    @Query("UPDATE UserChampionInfo SET proficiencyScore = :proficiencyScore, updatedDateTime = CURRENT_TIMESTAMP WHERE summonerId = :summonerId AND championId = :championId")
    void updateChampAndUpdatedAt(@Param("proficiencyScore") int proficiencyScore, @Param("summonerId") String summonerId, @Param("championId") String championId);

}
