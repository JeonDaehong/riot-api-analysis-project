package com.lolrpt.lol_statistices_service.common;

public class ApiCountCheckGlobalValue {

    // G_Value
    private static int secondCount = 0;
    private static int minuteCount = 0;

    public static int getSecondCount() {
        return secondCount;
    }

    public static void setSecondCount(int secondCountValue) {
        secondCount = secondCountValue;
    }

    public static int getMinuteCount() {
        return minuteCount;
    }

    public static void setMinuteCount(int minuteCountValue) {
        minuteCount = minuteCountValue;
    }

}
