package com.lolrpt.lol_statistices_service.common;

import org.springframework.transaction.annotation.Transactional;

public class ApiCountMethod {

    /**
     * API 횟수 증가 메서드
     * API 호출 Count가 Max에 도달할 시 잠시 Thread를 멈춤. 그리고 전역 변수를 초기화 함.
     */
    public static void apiCountCheckMethod() {
        try {
            if ( ApiCountCheckGlobalValue.getSecondCount() == ApiCount.SECOND_MAX_COUNT ) {
                Thread.sleep(ApiCount.SECOND_TIME);
                ApiCountCheckGlobalValue.setSecondCount(0);
            }

            if ( ApiCountCheckGlobalValue.getMinuteCount() == ApiCount.MINUTE_MAX_COUNT ) {
                Thread.sleep(ApiCount.MINUTE_TIME);
                ApiCountCheckGlobalValue.setMinuteCount(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Api 호출 카운트 올려주는 메서드
     */
    public static void apiCountPlusMethod() {
        ApiCountCheckGlobalValue.setSecondCount(ApiCountCheckGlobalValue.getSecondCount() + 1);
        ApiCountCheckGlobalValue.setMinuteCount(ApiCountCheckGlobalValue.getMinuteCount() + 1);
    }

}
