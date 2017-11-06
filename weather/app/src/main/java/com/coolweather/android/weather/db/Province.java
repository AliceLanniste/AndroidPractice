package com.coolweather.android.weather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/10/12.
 */

public class Province extends DataSupport {

    private int id;
    private String provinceName;
    private int provinceCode;

    public int getId() {
        return  this.id;
    }

    public String getProvinceName() {
        return this.provinceName;
    }

    public  int getProvinceCode() {
        return this.provinceCode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProvinceName(String provinceName)
    {
        this.provinceName = provinceName;
    }

    public void setProvinceICode(int provinceCode)
    {
        this.provinceCode = provinceCode;
    }
}
