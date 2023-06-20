package com.lolrpt.lol_statistices_service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Getter
@ToString
@NoArgsConstructor
public class MiniSeriesDTO {

    private int losses;
    private String progress;
    private int target;
    private int wins;

}
