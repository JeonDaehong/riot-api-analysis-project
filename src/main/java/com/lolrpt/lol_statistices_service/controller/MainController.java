package com.lolrpt.lol_statistices_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    @GetMapping("/mainPage")
    public String mainController(){
        return "mainPage";
    }
}
