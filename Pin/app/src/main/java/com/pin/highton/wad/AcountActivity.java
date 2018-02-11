package com.pin.highton.wad;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.pin.highton.wad.serverrequest.JSONTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by Seungyong Son on 2018-02-10.
 */

public class AcountActivity extends AppCompatActivity {

    private EditText et_id, et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acount);

        chkGpsService();

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }

        et_id = findViewById(R.id.id);
        et_password = findViewById(R.id.pass);
    }

    //GPS 설정 체크
    private boolean chkGpsService() {

        String gps = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        Log.d(gps, "aaaa");

        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {

            // GPS OFF 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
            gsDialog.setTitle("위치 서비스 설정");
            gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?");
            gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).create().show();
            return false;

        } else {
            return true;
        }
    }

    public void onClick(View v) throws ExecutionException, InterruptedException, JSONException {
        Intent intent;
        switch (v.getId()){
            case R.id.btnAcount :
                intent = new Intent(this, ResisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btnLogin :
                String id = et_id.getText().toString();
                String password = et_password.getText().toString();
                if (!id.isEmpty() && !password.isEmpty()) {
                    JSONObject request = new JSONObject();

                    request.accumulate("id", id);
                    request.accumulate("pw", password);

                    String repone = new JSONTask().execute("POST", "https://ward-api.herokuapp.com/login", request).get();
                    Log.e("--리스폰--", "" + repone);

                    if(repone == null){
                        Toast.makeText(getApplicationContext(), "로그인 실패!! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    String[] results = repone.split(" ");

                    int status = Integer.parseInt(results[0]);
                    JSONObject response = new JSONObject(results[1]);

                    if(status == 200){
                        JSONTask.accessToken = response.getString("accessToken");
                        intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "로그인 실패!! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
