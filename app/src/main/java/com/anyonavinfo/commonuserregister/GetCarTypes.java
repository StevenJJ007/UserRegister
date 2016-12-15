package com.anyonavinfo.commonuserregister;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetCarTypes {

	public static String GetWords() throws Exception {
		String user_CarTypes = "http://115.159.59.209/cpad/getCarTypes.action?factoryName=%E4%BA%94%E8%8F%B1";

		URL aURL = new URL(user_CarTypes);
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

	public class CarTypes {
		String message = new String();
		ArrayList<String> Types = new ArrayList<String>();

		public String GetType(int number) {
			return Types.get(number);
		}
	}

	public CarTypes GetTypes(String word) {
		Gson gson = new Gson();
		java.lang.reflect.Type type = new TypeToken<CarTypes>() {
		}.getType();
		CarTypes data = null;
		try {
			data = gson.fromJson(word, type);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

	public static void doGetAsyn(
			final MainActivity.CallBack callBack) {
		new Thread() {
			public void run() {
				try {
					String result = GetWords();
					if (callBack != null) {
						callBack.onRequestComplete(result);
					}
				} catch (Exception e) {
					e.printStackTrace();

				}

			};
		}.start();
	}

	// public interface CallBack {
	// void onRequestComplete(String result);
	// }

}
