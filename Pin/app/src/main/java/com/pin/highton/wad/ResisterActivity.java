package com.pin.highton.wad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.pin.highton.wad.serverrequest.JSONTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by Seungyong Son on 2018-02-10.
 */

public class ResisterActivity extends AppCompatActivity {

    private EditText et_id, et_password, ret_password, et_name, et_year, et_month, et_day, et_phone, et_shcool;
    private String id, pass, repass, name, birth, sex , phone, school;
    private String DD = "5a7f2619c2fcd07cf4a8c876", DG = "5a7f2622c2fcd07cf4a8c877", SR = "5a7f262cc2fcd07cf4a8c878";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_id = findViewById(R.id.id);
        et_password = findViewById(R.id.pass);
        ret_password = findViewById(R.id.repass);
        et_name = findViewById(R.id.name);
        et_year = findViewById(R.id.year);
        et_month = findViewById(R.id.month);
        et_day = findViewById(R.id.day);
        et_phone = findViewById(R.id.phone);
        et_shcool = findViewById(R.id.school);
    }

    public void onClick(View v) throws ExecutionException, InterruptedException, JSONException {
        Intent intent;
        switch (v.getId()){
            case R.id.man :
                sex = "M";
                v.setBackgroundResource(R.drawable.green_rape);
                findViewById(R.id.woman).setBackgroundResource(R.drawable.rape);
                break;
            case R.id.woman :
                sex = "F";
                v.setBackgroundResource(R.drawable.green_rape);
                findViewById(R.id.man).setBackgroundResource(R.drawable.rape);
                break;
            case R.id.register :
                id = et_id.getText().toString();
                pass = et_password.getText().toString();
                repass = ret_password.getText().toString();
                name = et_name.getText().toString();
                birth = et_year.getText().toString() + "-" + et_month.getText().toString() + "-" + et_day.getText().toString();
                phone = et_phone.getText().toString();
                school = et_shcool.getText().toString();

                Log.e("--입력--", id + " " + pass + " " + repass + " " + name + " " + birth + " " + phone + " " + school + " " + sex);

                if (!id.isEmpty() && !pass.isEmpty() && !name.isEmpty() && !birth.isEmpty() && !sex.isEmpty() && !phone.isEmpty() && !school.isEmpty()) {
                    if(!pass.equals(repass)){
                        Toast.makeText(getApplicationContext(), "정확한 정보를 입력해주세요!!!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    JSONObject request = new JSONObject();

                    request.accumulate("id", id);
                    request.accumulate("pw", pass);
                    request.accumulate("name", name);
                    request.accumulate("sex", sex);
                    request.accumulate("birthday", birth);
                    request.accumulate("email", "*");
                    request.accumulate("phone", phone);
                    request.accumulate("schools", new JSONArray());

                    String schoolID = null;

                    switch (school){
                        case "대덕소프트웨어마이스터고등학교":
                            schoolID = DD;
                            break;
                        case "대구소프트웨어고등학교":
                            schoolID = DG;
                            break;
                        case "선린인터넷고등학교":
                            schoolID = SR;
                            break;
                    }

                    // Toast.makeText(getApplicationContext(), "입력하신 학교는 현재 학교 리스트에 없습니다. 일단 가입을 환영합니다.", Toast.LENGTH_SHORT).show();

                    String result = new JSONTask().execute("POST", "https://ward-api.herokuapp.com/register", request).get();

                    if(result == null) {
                        Toast.makeText(getApplicationContext(), "회원가입 실패!! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    String[] results = result.split(" ");

                    int status = Integer.parseInt(results[0]);
                    JSONObject response = new JSONObject(results[1]);

                    if (status == 201) {
                        if (schoolID != null) {
                            request = new JSONObject();
                            request.accumulate("schoolID", schoolID);

                            JSONTask.accessToken = response.getString("accessToken");

                            result = new JSONTask().execute("POST", "https://ward-api.herokuapp.com/user/school", request).get();

                            if (result == null) {
                                Toast.makeText(getApplicationContext(), "로그인 실패!! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "현재 학교에 등록되어 있지 않지만 아무쪼록 회원가입 축하드립니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "정확한 정보를 입력해주세요!!!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
