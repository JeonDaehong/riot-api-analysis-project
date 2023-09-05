package com.lolrpt.lol_statistices_service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@ToString
public class MatchIdDto {
    List<String> matchIds;

    public MatchIdDto(){ this.matchIds = new ArrayList<String>();}
    public MatchIdDto(List<String> matchIds) {this.matchIds = matchIds;}

    public void addMatchId(List<String> matchIds) { this.matchIds.addAll(matchIds);}
}
