package com.lolrpt.lol_statistices_service.repository;

import com.lolrpt.lol_statistices_service.dto.entity.UserMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserMaster, Object> {

}
