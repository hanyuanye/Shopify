package com.shopify.hanyu.shopify;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ListActivity {
    private static String TAG = "MainActivity";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client;
    private SummaryListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SummaryListAdapter(this);
        client = new OkHttpClient();
        String[] params = {"https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6"};
        new callUrl().execute(params);
    }

    private class callUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            Request request = new Request.Builder().url(url[0]).build();
            if (request == null) {
                Log.d(TAG, "response is null");
            }
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            HashMap<String, Integer> provinces = getProvinceOrders(jsonString);
            adapter.addSectionHeaderItem("Orders by Province");
            for (String key : provinces.keySet()) {
                adapter.addItem(key + " Number of Orders: " + provinces.get(key).toString());
            }

            HashMap<String, ArrayList<String>> years = getYearOrders(jsonString);
            adapter.addSectionHeaderItem("Orders by Year");
            for (String key : years.keySet()) {
                adapter.addYearHeaderItem(key + " Number of Orders: " + years.get(key).size());
                for (int i = 0; i < years.get(key).size() && i < 10; i++) {
                    adapter.addItem(years.get(key).get(i));
                }
            }

            adapter.setJsonString(jsonString);
            setListAdapter(adapter);
        }
    }

    HashMap<String, Integer> getProvinceOrders(String jsonStr) {
        HashMap<String, Integer> provinceMap = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray orders = jsonObject.getJSONArray("orders");
            for (int i = 0; i < orders.length(); i++) {
                JSONObject order = orders.getJSONObject(i);
                if (order.isNull("shipping_address")) {
                    continue;
                }
                JSONObject address = order.getJSONObject("shipping_address");
                String province = address.getString("province");
                if (provinceMap.containsKey(province)) {
                    int counter = provinceMap.get(province) + 1;
                    provinceMap.remove(province);
                    provinceMap.put(province, counter);
                } else {
                    provinceMap.put(province, 1);
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return provinceMap;
    }

    HashMap<String, ArrayList<String>> getYearOrders(String jsonStr) {
        HashMap<String, ArrayList<String>> yearMap = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray orders = jsonObject.getJSONArray("orders");
            for (int i = 0; i < orders.length(); i++) {
                JSONObject order = orders.getJSONObject(i);
                String year = order.getString("created_at").substring(0, 4); //year is first four characters of "created_at"
                String data = order.getString("name") + " " + order.getString("total_price");
                if (yearMap.containsKey(year)) {
                    yearMap.get(year).add(data);
                } else {
                    ArrayList<String> datas = new ArrayList<>();
                    datas.add(data);
                    yearMap.put(year, datas);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return yearMap;
    }
}
