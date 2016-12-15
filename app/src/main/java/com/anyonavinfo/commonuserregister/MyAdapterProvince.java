package com.anyonavinfo.commonuserregister;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by shijj on 2016/5/4.
 */
public class MyAdapterProvince extends BaseAdapter {
    private List<ProvinceBean> mList;
    private LayoutInflater mInflater;

    public MyAdapterProvince(Context context, List<ProvinceBean> data) {
        mList = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.plist, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.pName.setText(mList.get(position).getpName());

        return convertView;
    }

   /* @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.plist, null);
            holder = new ViewHolder(convertView);
            TextView tv = (TextView) convertView.findViewById(R.id.p_name);
            tv.setTextColor(Color.RED);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;

    }*/

    static class ViewHolder {
        @InjectView(R.id.p_name)
        TextView pName;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}



