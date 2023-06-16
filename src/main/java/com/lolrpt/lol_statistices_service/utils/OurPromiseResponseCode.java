package com.lolrpt.lol_statistices_service.utils;

/**
 * 성공 코드와, 실패 코드를 나누기 위해 만든 Class
 * RestController나 ResponseBody를 사용하지 않을 경우를 위해 만들어 둔 Class
 */
public class OurPromiseResponseCode {
    public static final String SUCCESS = "1000";
    public static final String GENERAL_SERVER_ERROR = "-1000";

    static class SignUpErrorResponseCode {
        public static final String OVERLAP_ERROR = "-100010";
    }
}
