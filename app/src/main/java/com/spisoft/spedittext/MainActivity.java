package com.spisoft.spedittext;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.spisoft.spsedittextview.SpsEditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SpsEditText SET = findViewById(R.id.set);
        SET.SetHint("TEST");
        SET.SetUses(this, true, true);
//        SET.SetBorder(getResources().getDrawable(R.drawable.background_button_shape_1),10);
        View bb = SET.ButtonPlusView("AA");
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"SSSS",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
