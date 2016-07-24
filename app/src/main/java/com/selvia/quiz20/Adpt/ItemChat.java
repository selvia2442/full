package com.selvia.quiz20.Adpt;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.selvia.quiz20.R;

/**
 * Created by JACE on 2016-07-20.
 */
public class ItemChat extends LinearLayout {
    public boolean isI;
    public String contents;

    public ItemChat(Context context, boolean isI, String contents, String url) {
        super(context);
        this.isI = isI;
        this.contents = contents;

        LayoutInflater ifl = LayoutInflater.from(context);
        ifl.inflate(R.layout.lvi_chat_box, this, true);

        LinearLayout liYou = (LinearLayout) findViewById(R.id.liYou);
        LinearLayout liI = (LinearLayout) findViewById(R.id.liI);

        if(isI) {
            liYou.setVisibility(GONE);
            liI.setVisibility(VISIBLE);
            ImageView imageI = (ImageView) findViewById(R.id.imageI);
            TextView textI = (TextView) findViewById(R.id.textI);

            AQuery aq = new AQuery( getContext() );
            aq.id( imageI ).image( url );
            textI.setText(contents);
        } else {
            liI.setVisibility(GONE);
            liYou.setVisibility(VISIBLE);
            ImageView imageYou = (ImageView) findViewById(R.id.imageYou);
            TextView textYou = (TextView) findViewById(R.id.textYou);

            AQuery aq = new AQuery( getContext() );
            aq.id( imageYou ).image( url );
            textYou.setText(contents);
        }
    }
}
