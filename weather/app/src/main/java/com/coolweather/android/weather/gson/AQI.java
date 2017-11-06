package com.coolweather.android.weather.gson;

/**
 * Created by pc-zhs on 2017/11/6.
 */

public class AQI {

    public AQICity city;

    public class AQICity {
        public String  api;
        public String pm25;
    }
}
