package com.lolrpt.lol_statistices_service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Getter
@ToString
@NoArgsConstructor
public class TopRankLeagueListDto {

    private String leagueId;
    private List<TopRankLeagueItemDTO> entries;
    private String tier;
    private String name;
    private String queue;

}
