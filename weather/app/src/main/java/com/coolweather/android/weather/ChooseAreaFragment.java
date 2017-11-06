package com.coolweather.android.weather;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.weather.db.City;
import com.coolweather.android.weather.db.County;
import com.coolweather.android.weather.db.Province;
import com.coolweather.android.weather.util.HttpUtil;
import com.coolweather.android.weather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.os.Build.VERSION_CODES.O;


/**
 * Created by pc-zhs on 2017/10/13.
 */

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static  final int LEVEL_COUNTY = 2;

    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>( );
    /*
    *省列表
     */
    private List<Province> provinceList;

    /*
    *市列表
     */
    private List<City> cityList;
    private  List<County> countyList;

    private Province selectedProvince;

    private City selectedCity;

    private int currentLevel;

   // @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);

        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return  view;
    }

    @Override
    public  void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener()
        {
            @Override
            public  void onItemClick(AdapterView<?> parent, View view,int position,
                                     long id)
            {
                if(currentLevel==LEVEL_PROVINCE)
                {
                    selectedProvince = provinceList.get(position);
                   queryProvinces( );
                }
                else if (currentLevel==LEVEL_CITY) {
                selectedCity = cityList.get(position);
                queryCounties ( );
                }
            }

        });
        backButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                if (currentLevel==LEVEL_COUNTY)
                    queryCities( );
                else if(currentLevel==LEVEL_CITY)
                    queryCounties();
            }
        });
    }
   /*
   * 查询全国所有的省，优先从数据库查询，如果没有查询再到
   * 服务器上查询
    */
    private void queryCities()
    {

        titleText.setText(selectedCity.getCityName( ));
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid=?",String.valueOf(selectedCity.getId())).
                                                                find(County.class);
        if(countyList.size() > 0) {
            dataList.clear();
            for (City city: cityList)
                dataList.add(city.getCityName());
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address ="http://guolin.tech/api/china";
            queryFromSever(address,"city");
        }
     }

    private void  queryProvinces()
    {

        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size( ) > 0)
        {
            dataList.clear( );
            for(Province province:provinceList) {
                dataList.add(province.getProvinceName( ));
            }
            adapter.notifyDataSetChanged( );
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else
        {
            String address ="http://guolin.tech/api/china";
            queryFromSever(address,"province");
        }
    }
    /*
    *查询选中市内的所有的县，优先从数据库查询，如果没有查询
    * 到再去服务器上查询
     */
    private void queryCounties()
    {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("city=?",String.valueOf(selectedCity.getId())).
                                                    find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county: countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else  {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china" + provinceCode + "/" + cityCode;
            queryFromSever(address,"county");
        }
    }

    private void queryFromSever(String address,final String type) {
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if("province".equals(type))
                {
                    result = Utility.handleProvniceResponse(responseText);
                }
                else if("city".equals(type))
                {
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                }
                else if("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                }

                if(result)
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if("province".equals(type))
                            {
                                queryProvinces();
                            } else  if("city".equals((type))) {
                                queryCities();
                            }
                            else if("county".equals(type))
                            {
                                queryCounties();
                            }
                        }
                    });
                }

            }
          //  @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}