package com.pin.highton.wad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pin.highton.wad.serverrequest.JSONTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

/**
 * Created by Seungyong Son on 2018-02-10.
 */

public class ProfileActivity extends AppCompatActivity{

    private EditText proSchool, proPhone, proBirth;
    private TextView proName;

    private String strSchool, strPhone, strBirth, strName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        proName = (TextView) findViewById(R.id.pro_name);
        proSchool = (EditText)findViewById(R.id.pro_school);
        proPhone = (EditText)findViewById(R.id.pro_phone);
        proBirth = (EditText)findViewById(R.id.pro_birth);

        String result = null;

        try {
            result = new JSONTask().execute("GET", "https://ward-api.herokuapp.com/user/profile").get();
            if (result == null) return;

            String[] results = result.split(" ");
            JSONObject response = new JSONObject(results[1]);

            strName = response.getString("name");
            strPhone = response.getString("phone");
            strBirth = response.getString("birthday");

            String[] birthdays = strBirth.split("T");
            birthdays = birthdays[0].split("-");

            strBirth = birthdays[0] + "년 " + birthdays[1] + "월 " + birthdays[2] + "일";

            JSONArray schools = response.getJSONArray("schools");
            if (schools.length() > 0) strSchool = schools.getJSONObject(0).getString("name");
        } catch (Exception e) {
            e.printStackTrace();
        }

        proName.setText(strName);
        proSchool.setText(strSchool);
        proPhone.setText(strPhone);
        proBirth.setText(strBirth);
    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.change_profile:
                if(!proPhone.isEnabled()){
                   // proSchool.setEnabled(true);
                    proPhone.setEnabled(true);
                    proBirth.setEnabled(true);
                }else{
                    strSchool = proSchool.getText().toString();
                    strPhone = proPhone.getText().toString();
                    strBirth = proBirth.getText().toString();
                    proSchool.setEnabled(false);
                    proPhone.setEnabled(false);
                    proBirth.setEnabled(false);

                    strBirth = strBirth.replaceAll(" ", "").replaceAll("(년|월|일)", "-");
                    strBirth = strBirth.substring(0, strBirth.length() - 1);

                    proBirth.setText(strBirth);
                    String result = null;

                    try {
                        JSONObject request = new JSONObject();

                        request.accumulate("name", strName);
                        request.accumulate("phone", strPhone);
                        request.accumulate("birthday", strBirth);

                        result = new JSONTask().execute("PUT", "https://ward-api.herokuapp.com/user/profile", request).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "수정 실패! 다시 해주세요1!!", Toast.LENGTH_SHORT);
                    } else {
                        Toast.makeText(getApplicationContext(), "수정 성공!!!", Toast.LENGTH_SHORT);
                        finish();
                    }
                }
                break;
        }
    }
}
