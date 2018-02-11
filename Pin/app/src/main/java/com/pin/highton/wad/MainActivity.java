package com.pin.highton.wad;

/**
 * Created by Seungyong Son on 2018-02-10.
 */


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;


import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pin.highton.wad.serverrequest.JSONTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Permission;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    double longitude; //경도
    double latitude;   //위도
    private EditText search;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 수동으로 위치 구하기
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        String locationProvider = LocationManager.NETWORK_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location currentLocation = locationManager.getLastKnownLocation(locationProvider);
        if (currentLocation != null) {
            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();
            Log.d("Main", "longtitude=" + latitude + ", latitude=" + longitude);

            FragmentManager fragmentManager = getFragmentManager();
            MapFragment mapFragment = (MapFragment) fragmentManager
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.

        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.toolbar_layout, null);
        actionBar.setCustomView(actionbar);
        return true;
    }


    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btnProfile :
                intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.btnSearch :
                intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                break;
            case R.id.searchW:
                if(search == null) {
                    search = (EditText) findViewById(R.id.searchSch);
                    search.setEnabled(true);
                    search.setHint("검색할 학교를 입력하세요.");
                }else{
                    if(!search.getText().toString().isEmpty()){
                        mMap.clear();
                        try {
                            String result = new JSONTask().execute("GET", "https://ward-api.herokuapp.com/school?name=" + search.getText().toString()).get();

                            if (result == null) {
                                break;
                            }

                            String[] results = result.split(" ");
                            JSONArray response = new JSONArray(results[1]);

                            if (response.length() == 0) break;

                            String schoolID = response.getJSONObject(0).getString("_id");
                            result = new JSONTask().execute("GET", "https://ward-api.herokuapp.com/school/user?schoolID=" + schoolID).get();

                            results = result.split(" ");
                            response = new JSONArray(results[1]);

                            for(int i = 0; i < response.length(); i++) {
                                JSONObject object = response.getJSONObject(i);

                                JSONArray ward = object.getJSONArray("ward");
                                if (ward.length() == 0) continue;

                                LatLng wak = new LatLng(ward.getDouble(0), ward.getDouble(1));
                                Log.d("와드", ward.getDouble(0) + " " + ward.getDouble(1));
                                MarkerOptions markerOptions = new MarkerOptions();

                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.KOREA);
                                List<Address> address;
                                String nowAddress = "";
                                try {
                                    if (geocoder != null) {
                                        //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
                                        //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정

                                        address = geocoder.getFromLocation(ward.getDouble(0), ward.getDouble(1), 1);

                                        if (address != null && address.size() > 0) {
                                            // 주소 받아오기
                                            String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                                            nowAddress  = currentLocationAddress;
                                        }
                                    }

                                } catch (IOException e) {
                                    Toast.makeText(getApplicationContext(), "주소를 가져 올 수 없습니다.", Toast.LENGTH_LONG).show();

                                    e.printStackTrace();
                                }

                                markerOptions.position(wak);
                                markerOptions.title("동창이 있는곳");
                                markerOptions.snippet(nowAddress);

                                mMap.addMarker(markerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(wak));
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

        }
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;
        LatLng SEOUL = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("나");
        markerOptions.snippet("현재 나의 위치");
        map.addMarker(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        //map.animateCamera(CameraUpdateFactory.zoomTo(10));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    private long pressedTime;
    @Override
    public void onBackPressed() {
        if ( pressedTime == 0 ) {
            Toast.makeText(getApplicationContext(), "한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
            pressedTime = System.currentTimeMillis();
        }
        else {
            int seconds = (int) (System.currentTimeMillis() - pressedTime);

            if ( seconds > 2000 ) {
                pressedTime = 0;
            }
            else {
                finish();
            }
        }
    }
}
