package com.example.ankan.cryptocurrencies;

import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ankan.cryptocurrencies.R;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String BASE_URL = "https://min-api.cryptocompare.com";
    String IMAGE_URL = "https://www.cryptocompare.com";

    private RecyclerView recyclerView;
    private CurrencyAdapter adapter;
    private List<Currency> currencyList;
    private TextView batteryLevelTextView;
    private ResideMenu resideMenu;
    private ResideMenuItem itemTrending;
    private ResideMenuItem itemNews;
    private ResideMenuItem itemAll;
    private ResideMenuItem itemConverter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_view);

        setupMenu();

        currencyList = new ArrayList<>();
        adapter = new CurrencyAdapter(this, currencyList);
        BatteryLowReceiver batteryLowReceiver = new BatteryLowReceiver();
        IntentFilter batteryLowFilter = new IntentFilter(Intent.ACTION_BATTERY_LOW);
        registerReceiver(batteryLowReceiver, batteryLowFilter);
        batteryLevelTextView = findViewById(R.id.battery_level_textview);

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, filter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int batteryLevel = (int) (level / (float) scale * 100);
        batteryLevelTextView.setText("Battery Level: " + batteryLevel + "%");
        UsbConnectorReceiver usbConnectorReceiver = new UsbConnectorReceiver();
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbConnectorReceiver, usbFilter);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        String url = BASE_URL + "/data/top/totalvolfull?limit=10&tsym=USD";
        prepareCurrencies(url);
    }

    private void prepareCurrencies(String url) {

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray js = response.getJSONArray("Data");
                    for (int i = 0; i < js.length(); i++) {
                        JSONObject display = js.getJSONObject(i).getJSONObject("DISPLAY").getJSONObject("USD");
                        String image = IMAGE_URL + display.getString("IMAGEURL");
                        JSONObject coinInfo = js.getJSONObject(i).getJSONObject("CoinInfo");

                        Currency c = new Currency(coinInfo.getString("FullName"), coinInfo.getString("Name"), display.getString("PRICE"),
                                display.getString("LOWDAY"), display.getString("HIGHDAY"),
                                display.getString("OPENDAY"), image, coinInfo.getString("Algorithm"));
                        currencyList.add(c);

                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("******", "Error");
            }
        });

        queue.add(jsObjRequest);

    }

    private void setupMenu() {

        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.gradient_background);
        resideMenu.attachToActivity(this);

        itemTrending = new ResideMenuItem(this, R.drawable.trending, "Trending");
        itemNews = new ResideMenuItem(this, R.drawable.news, "News");
        itemAll = new ResideMenuItem(this, R.drawable.dashboard, "Major Cryptos");
        itemConverter = new ResideMenuItem(this, R.drawable.converter, "Converter");

        itemAll.setOnClickListener(this);
        itemNews.setOnClickListener(this);
        itemTrending.setOnClickListener(this);
        itemConverter.setOnClickListener(this);

        resideMenu.addMenuItem(itemTrending, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemNews, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemAll, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemConverter, ResideMenu.DIRECTION_LEFT);

        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        findViewById(R.id.btn_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });

        //resideMenu.closeMenu();
    }


    @Override
    public void onClick(View view) {
        if (view == itemAll) {
            Intent i = new Intent(MainActivity.this, AllCryptosActivity.class);
            startActivity(i);
            Toast.makeText(this, "All Cryptos", Toast.LENGTH_SHORT).show();
        } else if (view == itemTrending) {
            Toast.makeText(this, "Trending", Toast.LENGTH_SHORT).show();
        } else if (view == itemNews) {
            Intent i = new Intent(MainActivity.this, NewsActivity.class);
            startActivity(i);
            Toast.makeText(this, "News", Toast.LENGTH_SHORT).show();
        } else if (view == itemConverter) {
            Intent i = new Intent(MainActivity.this, ConverterActivity.class);
            startActivity(i);
            Toast.makeText(this, "Converter", Toast.LENGTH_SHORT).show();
        }

        resideMenu.closeMenu();
    }


    // What good method is to access resideMenu？
    public ResideMenu getResideMenu(){
        return resideMenu;
    }
}
