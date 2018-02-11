package com.pin.highton.wad;

import android.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pin.highton.wad.serverrequest.JSONTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng Wad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;
        // Add a marker in Sydney, Australia, and move the camera.ㅠ
        LatLng sydney = new LatLng(37.566535, 126.97796919999999);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(sydney);
        markerOptions.title("대한민국");
        markerOptions.snippet("와드를 검색창에 검색해 주세요.");
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        map.animateCamera(CameraUpdateFactory.zoomTo(5));
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
        View actionbar = inflater.inflate(R.layout.searchbar_layout, null);

        actionBar.setCustomView(actionbar);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("맵검색", "Place: " + place.getName() + " " + place.getAddress() + " " + place.getLatLng());

                LatLng latlng = place.getLatLng();
                Wad = new LatLng(latlng.latitude, latlng.longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(Wad);
                markerOptions.title((String) place.getName() + "(" + place.getAddress() + ")");
                markerOptions.snippet("이곳을 와드로 설정할려면 누르세요.");
                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(Wad));
                //map.animateCamera(CameraUpdateFactory.zoomTo(10));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        //36.3913788,127.36310370000001
                mMap.setOnInfoWindowClickListener(MapsActivity.this);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("맵검색", "An error occurred: " + status);
            }
        });
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        double lat, lng;
        lat = Wad.latitude;
        lng = Wad.longitude;

        String result = null;

        JSONObject request = new JSONObject();

        try {
            request.accumulate("lat", lat);
            request.accumulate("lng", lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            result = new JSONTask().execute("PUT", "https://ward-api.herokuapp.com/user/ward", request).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(result == null) Toast.makeText(this, "와드 추가 실패!!!", Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "성공!!!", Toast.LENGTH_SHORT).show();
        finish();
    }
}

