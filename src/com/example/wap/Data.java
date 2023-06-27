package com.example.wap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Data extends Activity {
	private EditText searchInput;
	private Button btnSearch;
	private TextView cityName, dama, bad, max, min, rtbt, press;
	private ImageView imgMain;
	public static String tempe, url, response, imgStatus;
	public static double damaInt;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {

				String result = (String) msg.obj;

				try {

					JSONObject jsonObject = new JSONObject(result);
					// دریافت اطلاعات وضعیت هوا
					JSONArray weatherArray = jsonObject.getJSONArray("weather");
					JSONObject weatherObject = weatherArray.getJSONObject(0);
					String weatherMain = weatherObject.getString("main");
					// دریافت اطلاعات دما و کمترین دما و بیشترین دما
					JSONObject mainObject = jsonObject.getJSONObject("main");
					double temp = mainObject.getDouble("temp");
					double tempMin = mainObject.getDouble("temp_min");
					double tempMax = mainObject.getDouble("temp_max");
					// رطوبت
					double humidity = mainObject.getInt("humidity");
					// فشار
					double pressure = mainObject.getInt("pressure");
					// دریافت اطلاعات سرعت باد و وضعیت باد
					JSONObject windObject = jsonObject.getJSONObject("wind");
					double speed = windObject.getDouble("speed");
					
					// دریافت شهر ورودی از کاربر و نمایش آن
					searchInput = (EditText) findViewById(R.id.EditSearch);
					cityName.setText(searchInput.getText().toString());
					// نمایش دما
					imgStatus = weatherMain.toString();
					max = (TextView) findViewById(R.id.DamaTextMax);
					int intmax = (int) tempMax;
					max.setText(intmax - 273 + "");

					min = (TextView) findViewById(R.id.DamaTextMin);
					int intmin = (int) tempMin;
					min.setText(intmin - 273 + "");

					rtbt = (TextView) findViewById(R.id.rtbtText);
					rtbt.setText(humidity + "");

					press = (TextView) findViewById(R.id.pressText);
					press.setText(pressure + "");
					
					bad = (TextView) findViewById(R.id.BadText);
					bad.setText(speed + "");

					imgMain = (ImageView) findViewById(R.id.imageMain);
					String mainStatus = weatherMain + "";
					String Clear = "Clear", Rain = "Rain", Cloudy = "Clouds";

					if (mainStatus.equals(Clear)) {
						imgMain.setImageResource(R.drawable.sunny);
					}
					if (mainStatus.equals(Rain)) {
						imgMain.setImageResource(R.drawable.rainy);
					}
					if (mainStatus.equals(Cloudy)) {
						imgMain.setImageResource(R.drawable.cloudy);
					}

					dama = (TextView) findViewById(R.id.DamaText);
					int intValue = (int) temp;
					damaInt = intValue - 273;

					tempe = damaInt + "";
					String str1 = tempe;
					String str2 = "°C";
			
//چسبوندن بهم
					String combinedString = String.format(str1, str2);
					dama.setText(combinedString);

				} catch (JSONException e) {
                   //مدیریت ارور 
				}

	}  				else {
				// Handle the case where data retrieval failed
			}
		 }
	     };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data);
		searchInput = (EditText) findViewById(R.id.EditSearch);
		btnSearch = (Button) findViewById(R.id.BtnSearch);
		cityName = (TextView) findViewById(R.id.CityText);

		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchInput = (EditText) findViewById(R.id.EditSearch);
					String baseUrl = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s";
				//چسبوندن شهر جدید و ای پی آی، اطلاعات رو بگیریم از طریق شهر جدید
					String apiKey = "cad7ec124945dcfff04e457e76760d90";

					String url = String.format(baseUrl, searchInput.getText().toString(), apiKey);
				getDataFromApi(url);
			}
		});
	}

	private void getDataFromApi(final String url) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection = null;
				BufferedReader reader = null;
				try {
					URL apiUrl = new URL(url);
					connection = (HttpURLConnection) apiUrl.openConnection();
					connection.setRequestMethod("GET");

					int responseCode = connection.getResponseCode();
					if (responseCode == HttpURLConnection.HTTP_OK) {
						InputStream inputStream = connection.getInputStream();
						reader = new BufferedReader(new InputStreamReader(
								inputStream));
						StringBuilder stringBuilder = new StringBuilder();
						String line;
						while ((line = reader.readLine()) != null) {
							stringBuilder.append(line);
						}
						response = stringBuilder.toString();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				Message message = handler.obtainMessage();
				if (response != null) {
					message.what = 1;
					message.obj = response;
				} else {
					message.what = 0;
				}
				handler.sendMessage(message);
			}
		});

		thread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
