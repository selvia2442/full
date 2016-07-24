package com.selvia.quiz20.Adpt;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.selvia.quiz20.R;

/**
 * Created by Administrator on 2016-07-18.
 */
public class ItemRoom extends RelativeLayout {
    public int no, people, max;
    public String title, state, create_date;

    public ItemRoom(Context context, String no, String title, String people, String state, String create_date) {
        super(context);
        this.title = title;
        this.state = state;
        this.create_date = create_date;
        max = 2;

        try {

            this.no = Integer.parseInt(no);
            this.people = Integer.parseInt(people);
        } catch (Exception e) {
            this.no = -1;
            this.people = -1;
        }

        LayoutInflater ifl = LayoutInflater.from(context);
        ifl.inflate(R.layout.lvi_ready_shape, this, true);

        TextView textNo = (TextView) this.findViewById(R.id.textNo);
        TextView textTitle = (TextView) this.findViewById(R.id.textTitle);
        TextView textPeople = (TextView) this.findViewById(R.id.textPeople);
        TextView textTime = (TextView) this.findViewById(R.id.textTime);

        textNo.setText(no+". ");
        textTitle.setText(title);
        textPeople.setText(people+"/"+max);
        textTime.setText(create_date);
    }
}
