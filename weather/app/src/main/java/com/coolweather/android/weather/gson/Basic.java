package com.coolweather.android.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pc-zhs on 2017/11/6.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public  String weatherId;

    public Update update;

    public class Update {

        @SerializedName("loc")
        public String updateTime;
    }
}
