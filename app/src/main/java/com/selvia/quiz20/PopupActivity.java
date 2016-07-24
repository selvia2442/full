package com.selvia.quiz20;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PopupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        TextView tv_content = (TextView) findViewById(R.id.tv_content);
        Button btn_1 = (Button) findViewById(R.id.btn_1);

        String data = getIntent().getStringExtra("data");

        tv_title.setText(getResources().getString(R.string.app_name));
        tv_content.setText(data);

        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
