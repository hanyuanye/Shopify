package com.shopify.hanyu.shopify;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ProvinceInformationActivity extends ListActivity {
    private static final String TAG = "ProvinceActivity";
    private ProvinceInformationAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String json = intent.getStringExtra("json");
        ArrayList<ArrayList<String>> provinces = parseJson(json);
        for (int i = 0; i < provinces.size(); i++) {
            for (int j = 0; j < provinces.get(i).size(); j++) {
                Log.d(TAG, provinces.get(i).get(j));
            }
        }
        adapter = new ProvinceInformationAdapter(this);
        for (int i = 0; i < provinces.size(); i++) {
            for (int j = 0; j < provinces.get(i).size(); j++) {
                if (j == 0) {
                    adapter.addSectionHeaderItem(provinces.get(i).get(j));
                } else {
                    adapter.addItem(provinces.get(i).get(j));
                }
            }
        }
        setListAdapter(adapter);
    }

    //Returns sorted 2dArrayList showing provinces with corresponding project data
    private ArrayList<ArrayList<String>> parseJson(String json) {
        ArrayList<ArrayList<String>> provinces = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray orders = jsonObject.getJSONArray("orders");
            for (int i = 0; i < orders.length(); i++) {
                JSONObject order = orders.getJSONObject(i);
                String data = "Name: " + order.getString("name");
                data += ", Quantity: " + order.getString("number");
                data += ", Price: $" + order.getString("total_price");
                if (order.isNull("shipping_address")) {
                    continue;
                }
                JSONObject address = order.getJSONObject("shipping_address");
                String province = address.getString("province");
                int index = findProvince(province, provinces);
                if (index != provinces.size()) {
                    provinces.get(index).add(data);
                } else {
                    ArrayList<String> newProvince = new ArrayList<>();
                    newProvince.add(province);
                    newProvince.add(data);
                    provinces.add(newProvince);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        Collections.sort(provinces, new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> province1, ArrayList<String> province2) {
                return province1.get(0).compareTo(province2.get(0));
            }
        });
        return provinces;
    }

    //Provinces are stored in the first index of each Arraylist
    //Iterates through to find the index of the province, if it can be found. This is to make adding
    //data to the arraylist easier. Returns provinces.size() if no matching string has been found
    // This is similar to C++ std::find for vector containers.
    private int findProvince(String province, ArrayList<ArrayList<String>> provinces) {
        for (int i = 0; i < provinces.size(); i++) {
            String currProvince = provinces.get(i).get(0);
            if (province.equals(currProvince)) {
                return i;
            }
        }
        return provinces.size();
    }
}
