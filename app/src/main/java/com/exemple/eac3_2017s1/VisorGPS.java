package com.exemple.eac3_2017s1;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

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
        String path = media.getFile() + File.separator + media.getName();
        ImageView imageView = findViewById(R.id.imageView);
        VideoView videoView = findViewById(R.id.videoView);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);


        if(media.getPhotoOrVideo() == 0) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(Drawable.createFromPath(path));
        } else {
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoPath(path);
            videoView.start();
        }

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
