package com.shopify.hanyu.shopify;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by hanyu on 5/3/2018.
 */

public class SummaryListAdapter extends BaseAdapter {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER_SEPARATOR = 1;
    private static final int TYPE_YEAR_SEPARATOR = 2;
    private String mJsonString;

    private ArrayList<String> mData = new ArrayList<String>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();
    private TreeSet<Integer> yearHeader = new TreeSet<Integer>();

    private LayoutInflater mInflater;
    private Context mContext;

    public SummaryListAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setJsonString(String jsonString) {
        mJsonString = jsonString;
    }

    public void addItem(final String item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final String item) {
        mData.add(item);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    public void addYearHeaderItem(final String item) {
        mData.add(item);
        yearHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (sectionHeader.contains(position)) return TYPE_HEADER_SEPARATOR;
        if (yearHeader.contains(position)) return TYPE_YEAR_SEPARATOR;
        return TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.section_content_item, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.content);
                    break;
                case TYPE_HEADER_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.section_header_item, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.sectionHeader);
                    if (mData.get(position).equals("Orders by Province")) {
                        holder.textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent provinceIntent = new Intent(mContext, ProvinceInformationActivity.class);
                                provinceIntent.putExtra("json", mJsonString);
                                mContext.startActivity(provinceIntent);
                            }
                        });
                    }
                    break;
                case TYPE_YEAR_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.year_header_item, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.year_header);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(mData.get(position));

        return convertView;
    }

    public static class ViewHolder {
        public TextView textView;
    }
}
