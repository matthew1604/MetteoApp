package com.example.meteoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URI;

public class WeatherActivity extends AppCompatActivity {

    String city, date, url, condition, image_url, day, forecast, temp_data, min, max;
    TextView city_name, current_date , current_condition, current_day, temp_number, temp_min, temp_max, temp_min_number, temp_max_number, error;
    ImageView condition_icon;
    Button btn_next, btn_previous, btn_back;
    int i = 0;
    boolean isValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheather);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        forecast = "fcst_day_0";
        new GetMeteo().execute();
        city_name = findViewById(R.id.city);
        current_day = findViewById(R.id.day);
        current_date = findViewById(R.id.date);
        current_condition = findViewById(R.id.condition);
        temp_number = findViewById(R.id.temp_number);
        temp_min = findViewById(R.id.temp_min);
        temp_max = findViewById(R.id.temp_max);
        temp_min_number = findViewById(R.id.temp_min_number);
        temp_max_number = findViewById(R.id.temp_max_number);
        error = findViewById(R.id.error);
        btn_back = findViewById(R.id.back);
        final Intent intent1 = new Intent(this, MainActivity.class);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent1);
            }
        });
        new  GetIcon().execute();
        condition_icon = findViewById(R.id.condition_icon);
        btn_next = findViewById(R.id.next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i<4){
                    btn_next.setEnabled(true);
                    i += 1;
                    forecast = "fcst_day_"+i;
                    new GetMeteo().execute();
                    new  GetIcon().execute();
                }
            }
        });
        btn_previous = findViewById(R.id.previous);
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( i>0) {
                    i -= 1;
                    forecast = "fcst_day_" + i;
                    new GetMeteo().execute();
                    new  GetIcon().execute();
                }
            }
        });

    }

    /**
     * Tache asynchrone
     */
    private class GetMeteo extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {

                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject infos = jsonObj.getJSONObject("city_info");
                    city = infos.getString("name");

                    JSONObject weather = jsonObj.getJSONObject(forecast);
                    day = weather.getString("day_long");
                    date = weather.getString("date");
                    condition = weather.getString("condition");
                    image_url = weather.getString("icon_big");
                    min = weather.getString("tmin")+"°C";
                    max = weather.getString("tmax")+"°C";

                    JSONObject temperature = jsonObj.getJSONObject("current_condition");
                    temp_data = temperature.getString("tmp")+"°C";
                    isValid = true;

                } catch (final JSONException e) {
                    Log.e("tag", "Erreur Json: " + e.getMessage());
                    isValid = false;
                }
            } else {
                Log.e("tag", "Pas de connexion.");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (isValid) {
                error.setVisibility(TextView.INVISIBLE);
                btn_back.setVisibility(TextView.INVISIBLE);
                temp_min.setVisibility(TextView.VISIBLE);
                temp_max.setVisibility(TextView.VISIBLE);
                btn_next.setVisibility(TextView.VISIBLE);
                btn_previous.setVisibility(TextView.VISIBLE);
                city_name.setText(city);
                current_day.setText(day);
                current_date.setText(date);
                current_condition.setText(condition);
                temp_number.setText(temp_data);
                temp_min_number.setText(min);
                temp_max_number.setText(max);
                if (i<4){
                    btn_next.setEnabled(true);
                }
                else {
                    btn_next.setEnabled(false);
                }
                if (i>0){
                    btn_previous.setEnabled(true);
                }
                else {
                    btn_previous.setEnabled(false);
                }
            }
            else {
                error.setVisibility(TextView.VISIBLE);
                btn_back.setVisibility(TextView.VISIBLE);
                temp_min.setVisibility(TextView.INVISIBLE);
                temp_max.setVisibility(TextView.INVISIBLE);
                btn_next.setVisibility(TextView.INVISIBLE);
                btn_previous.setVisibility(TextView.INVISIBLE);
            }

        }
    }


    private class GetIcon extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = image_url;
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            condition_icon.setImageBitmap(result);
        }
    }
}
