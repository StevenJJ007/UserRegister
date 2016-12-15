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
public class MyAdapterShop extends BaseAdapter {

    private List<SupporterBean> mList;
    private LayoutInflater mInflater;
    public MyAdapterShop(Context context, List<SupporterBean> data) {
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
            convertView = mInflater.inflate(R.layout.supporter_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.shopName.setText(mList.get(position).getSupporterName());
        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.s_name)
        TextView shopName;


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
