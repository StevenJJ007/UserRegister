package com.anyonavinfo.commonuserregister;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Inquire extends Activity {
    private EditText user_4Sname;
    private Button btnSure;
    private ListView list;
    private ArrayList<HashMap<String, Object>> mylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inquire);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());

        user_4Sname = (EditText) findViewById(R.id.user_4Sname);
        list = (ListView) findViewById(R.id.MyListView);
        btnSure = (Button) findViewById(R.id.sure);
        btnSure.setOnClickListener(onClick);

        list.setOnItemClickListener(itemClick);
    }

    AdapterView.OnItemClickListener itemClick = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            // TODO Auto-generated method stub
            switch (arg0.getId()) {
                case R.id.MyListView:
                    @SuppressWarnings("unchecked")
                    HashMap<String, String> map = (HashMap<String, String>) list.getItemAtPosition(arg2);
                    String orgId = map.get("orgId");
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("orgId", orgId);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                default:
                    break;
            }
        }
    };

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            try {
                String keyword = user_4Sname.getText().toString().trim();
                if (keyword != "") {
                    Set4sInfo(keyword);
                } else {
                    Context self = null;
                    new AlertDialog.Builder(self).setTitle("错误！")
                            .setMessage("请输入正确的4s店关键字!")
                            .setPositiveButton("确定", null).show();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    private String GetOrdID(String keyword) throws IOException {
        String strIn = java.net.URLEncoder.encode(keyword, "UTF-8");
        String user_4SnameUrl = "http://115.159.59.209/cpad/getSSSSInfo.action?ssssName="
                + strIn;

        URL aURL = new URL(user_4SnameUrl);
        HttpURLConnection urlConn = (HttpURLConnection) aURL.openConnection();
        urlConn.setRequestMethod("GET");
        urlConn.setConnectTimeout(10000);
        urlConn.connect();
        InputStream stream = urlConn.getInputStream();

        BufferedReader tBufferedReader = new BufferedReader(
                new InputStreamReader(stream));
        StringBuffer is = new StringBuffer();
        String str = new String("");
        while ((str = tBufferedReader.readLine()) != null) {
            is.append(str);
        }
        tBufferedReader.close();
        return is.toString();
    }

    private void Set4sInfo(String word) throws IOException {
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<user_4sInfos>() {
        }.getType();
        user_4sInfos InfoList = gson.fromJson(GetOrdID(word), type);

        if (InfoList.message.equals("success")) {

            mylist = new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i < InfoList.data.size(); i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("name", InfoList.Get4sInfo(i).name);
                map.put("orgId", InfoList.Get4sInfo(i).ssssId);
                mylist.add(map);
            }

            SimpleAdapter mSchedule = new SimpleAdapter(this, mylist,
                    R.layout.mylistview, new String[]{"name", "orgId"},
                    new int[]{R.id.name, R.id.orgId});
            list.setAdapter(mSchedule);
        } else {
            new AlertDialog.Builder(this).setTitle("错误！")
                    .setMessage("请输入正确的4s店关键字！").setPositiveButton("确定", null)
                    .show();
        }
    }

    private class data {
        public String name = new String();
        public String ssssId = new String();
    }

    private class user_4sInfos {
        String message = new String();
        ArrayList<data> data = new ArrayList<data>();

        public data Get4sInfo(int number) {
            return data.get(number);
        }
    }
}
