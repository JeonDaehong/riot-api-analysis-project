package com.lolrpt.lol_statistices_service.repository;

import com.lolrpt.lol_statistices_service.dto.entity.LoLUserMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiotApiRequestRepository extends JpaRepository<LoLUserMaster, Object> {


}
