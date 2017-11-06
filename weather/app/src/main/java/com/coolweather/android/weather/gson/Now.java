package com.coolweather.android.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pc-zhs on 2017/11/6.
 */

public class Now {

    @SerializedName("tmp")
    public String temprature;

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public  String info;
    }
}
