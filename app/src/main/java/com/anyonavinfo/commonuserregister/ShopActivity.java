package com.anyonavinfo.commonuserregister;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by shijj on 2016/4/28.
 */
public class ShopActivity extends Activity {
    @InjectView(R.id.province)
    Spinner pSpinner;/*选择省份*/
    @InjectView(R.id.city)
    Spinner cSpinner;/*选择城市*/
    @InjectView(R.id.show_shop)
    ListView shopListView;/*4S店列表*/
    private MyAdapterProvince provinceadapter = null;
    private MyAdapterCity cityadapter = null;
    private MyAdapterShop shopadapter = null;
    private List<ProvinceBean> beanList;//存放省
    private String PROVINCE_URL = HttpApi.URL_MAIN + HttpApi.CMD_PROVINCE + HttpApi.BRANDID;
    private String CITY_URL = "";
    private List<CityBean> cityList;//存放城市
    private List<SupporterBean> supporterList;//存放经销商
    private Bundle bundle;
    private Intent intent;
    private ProgressDialog proDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop);
        ButterKnife.inject(this);
        RequestDialog();
        new ProvinceAsyncTask().execute(PROVINCE_URL);

        pSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String code = beanList.get(position).getCode();
                Log.d("sjj--->", code);
                CITY_URL = HttpApi.URL_MAIN + HttpApi.CMD_CITY + HttpApi.BRANDID + HttpApi.PROVINCECODE + code;
                new CityAsyncTask().execute(CITY_URL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        cSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String result = cityList.get(position).getSupporter();
                Log.d("sjjddd--->", result);
                supporterList = new ArrayList<SupporterBean>();
                JSONArray jsonArray;
                SupporterBean supporterBean;
                try {
                    jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        supporterBean = new SupporterBean();
                        supporterBean.supporterCode = jsonObject.getString("storeId");
                        supporterBean.supporterName = jsonObject.getString("storeName");
                        supporterList.add(supporterBean);
                        Log.d("sjjjjj--->", supporterList.toString());
                        shopadapter = new MyAdapterShop(ShopActivity.this, supporterList);
                        shopListView.setAdapter(shopadapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        shopListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String name = supporterList.get(position).getSupporterName();
                String code = supporterList.get(position).getSupporterCode();
               /* bundle = new Bundle();
                bundle.putString("ID", supporterList.get(position).getSupporterCode());
                bundle.putString("NAME", supporterList.get(position).getSupporterName());
                Toast.makeText(ShopActivity.this, "您选择的4S店是:" + supporterList.get(position).getSupporterName(), Toast.LENGTH_SHORT).show();*/
                alert(name, code);
            }
        });

    }

    private void RequestDialog() {
        proDialog = ProgressDialog.show(this, "数据获取中...",
                "请稍后...", true, true);
    }

    private void alert(final String name, final String code) {
        Log.d("code", code);
        final Dialog dialog = new AlertDialog.Builder(this).setTitle("请确认您所选的4S店")
                .setMessage(name)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                intent = new Intent();
                                bundle = new Bundle();
                                bundle.putString("ID", code);
                                bundle.putString("NAME", name);
                                intent.putExtras(bundle);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                ).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    /**
     * 将url对应的数据转化为封装的ProvinceBean对象
     *
     * @param url
     * @return
     */
    private List<ProvinceBean> getJsonDataProvince(String url) {
        beanList = new ArrayList<ProvinceBean>();
        try {
            String jsonString = readStreamProvince(new URL(url).openStream());
            //Log.i("sjj", jsonString);
            JSONObject jsonObject;
            ProvinceBean bean;

            try {
                jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("message");
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    bean = new ProvinceBean();
                    bean.code = jsonObject.getString("provinceId");
                    bean.pName = jsonObject.getString("provinceName");
                    beanList.add(bean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {

        }
        return beanList;
    }

    /**
     * 将url对应的数据转化为封装的CityBean对象
     *
     * @param url
     * @return
     */
    private List<CityBean> getJsonDataCity(String url) {
        cityList = new ArrayList<CityBean>();
        //supporterList=new ArrayList<SupporterBean>();
        try {
            String jsonString = readStreamCity(new URL(url).openStream());
            //Log.i("sjj", jsonString);
            JSONObject jsonObject;
            CityBean bean;

            try {
                jsonObject = new JSONObject(jsonString);
                JSONArray message = jsonObject.getJSONArray("message");
                for (int i = 0; i < message.length(); i++) {
                    jsonObject = message.getJSONObject(i);
                    bean = new CityBean();
                    bean.cityCode = jsonObject.getString("cityId");
                    bean.cityName = jsonObject.getString("cityName");
                    bean.supporter = jsonObject.getString("storeVOList");
                    cityList.add(bean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {

        }
        return cityList;
    }

    /**
     * 通过InputStream解析网页返回省的数据
     *
     * @param is
     * @return
     */
    private String readStreamProvince(InputStream is) {
        InputStreamReader isr;
        String result = "";
        try {
            String line = "";
            isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("sjj--->", result.toString());
        return result;

    }

    /**
     * 通过InputStream解析网页返回市的数据
     *
     * @param is
     * @return
     */
    private String readStreamCity(InputStream is) {
        InputStreamReader isr;
        String result = "";
        try {
            String line = "";
            isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("sjj--->", result.toString());
        return result;

    }



    /**
     * 省
     */
    class ProvinceAsyncTask extends AsyncTask<String, Void, List<ProvinceBean>> {

        @Override
        protected List<ProvinceBean> doInBackground(String... params) {
            return getJsonDataProvince(params[0]);
        }

        @Override
        protected void onPostExecute(List<ProvinceBean> beans) {
            super.onPostExecute(beans);
            provinceadapter = new MyAdapterProvince(ShopActivity.this, beans);
            pSpinner.setAdapter(provinceadapter);

        }
    }

    /**
     * 市
     */
    class CityAsyncTask extends AsyncTask<String, Void, List<CityBean>> {

        @Override
        protected List<CityBean> doInBackground(String... params) {
            return getJsonDataCity(params[0]);
        }

        @Override
        protected void onPostExecute(List<CityBean> beans) {
            super.onPostExecute(beans);
            cityadapter = new MyAdapterCity(ShopActivity.this, beans);
            cSpinner.setAdapter(cityadapter);
            proDialog.dismiss();
        }
    }
}
