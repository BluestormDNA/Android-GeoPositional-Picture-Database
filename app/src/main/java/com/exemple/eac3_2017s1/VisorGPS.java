package com.exemple.eac3_2017s1;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;

/**
 * Created by BlueStorm on 01/11/2017.
 */

public class VisorGPS extends AppCompatActivity implements OnMapReadyCallback{

    Media media;
    GoogleMap googleMap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visor_gps);

        MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);

        media = (Media) getIntent().getSerializableExtra("media");
        ImageView imageView = findViewById(R.id.visor);
        imageView.setImageDrawable(Drawable.createFromPath(media.getFile()+ File.separator+media.getName()));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng loc = new LatLng(media.getLatitude(), media.getLongitude());
        this.googleMap.addMarker(new MarkerOptions().position(loc));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        this.googleMap.animateCamera( CameraUpdateFactory.zoomTo( 10 ) );
    }
}
