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
public class MatchIdDto {
    List<String> matchIds;

    public MatchIdDto(List<String> matchIds) {this.matchIds = matchIds;}
}
