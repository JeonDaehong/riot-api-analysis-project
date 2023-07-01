package com.lolrpt.lol_statistices_service.controller;

import com.lolrpt.lol_statistices_service.service.RiotApiRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RiotApiController {

    private final RiotApiRequestService riotApiRequestService;

    @GetMapping("/test.svc")
    public void testController() {
        riotApiRequestService.requestUserInfoEachTier();
    }

}
