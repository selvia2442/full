package com.selvia.quiz20.Adpt;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016-07-18.
 */
public class AdptRoom extends BaseAdapter {
    ArrayList<ItemRoom> arrayItem = new ArrayList<ItemRoom>();

    public AdptRoom(ArrayList<ItemRoom> arrayItem) {
        super();
        this.arrayItem = arrayItem;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return arrayItem.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return arrayItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return arrayItem.get(position);
    }
}
