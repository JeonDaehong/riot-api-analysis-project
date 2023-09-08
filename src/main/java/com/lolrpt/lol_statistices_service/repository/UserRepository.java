package com.lolrpt.lol_statistices_service.repository;

import com.lolrpt.lol_statistices_service.common.Rank;
import com.lolrpt.lol_statistices_service.dto.entity.UserMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<UserMaster, Object> {

    @Query("SELECT puuid FROM UserMaster WHERE summonerTier IN ('DIAMOND', 'MASTER', 'GRANDMASTER', 'CHALLENGER')")
    List<String> getFilteredResults();

}
