package com.selvia.quiz20.Adpt;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by JACE on 2016-07-20.
 */
public class AdptChat extends BaseAdapter {
    ArrayList<ItemChat> arrayItem = new ArrayList<ItemChat>();

    public AdptChat(ArrayList<ItemChat> arrayItem) {
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