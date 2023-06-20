package com.lolrpt.lol_statistices_service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Getter
@ToString
@NoArgsConstructor
public class MiniSeriesDto {

    private int losses;
    private String progress;
    private int target;
    private int wins;

}
