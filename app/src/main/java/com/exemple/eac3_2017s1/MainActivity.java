package com.exemple.eac3_2017s1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.location.LocationProvider.AVAILABLE;
import static android.location.LocationProvider.OUT_OF_SERVICE;
import static android.location.LocationProvider.TEMPORARILY_UNAVAILABLE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    private final int REQUEST_IMAGE_CAPTURE = 0;
    private final int REQUEST_VIDEO_CAPTURE = 1;
    private final int PERMISSION = 1;
    //
    FloatingActionButton photoFab;
    FloatingActionButton videoFab;
    private DBInterface db;
    private LocationManager gestorLoc;
    private Location location;
    private RecyclerView recyclerView;
    private Adaptador adaptador;
    //
    private List<Media> lista;
    private Media media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        photoFab = findViewById(R.id.fabPhoto);
        videoFab = findViewById(R.id.fabVideo);
        photoFab.setOnClickListener(this);
        videoFab.setOnClickListener(this);

        adaptador = new Adaptador(this);
        recyclerView = findViewById(R.id.rView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptador);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //iniciamos db y la pasamos al adaptador
        db = new DBInterface(this);
        adaptador.setDB(db);
        //iniciamos lista local
        lista = new ArrayList<>();

        //comprobamos si no hay datos guardados y si los hay los cargamos
        if (savedInstanceState != null) {
            if (savedInstanceState.getSerializable("media") != null)
                media = (Media) savedInstanceState.getSerializable("media");
            if (savedInstanceState.getParcelable("location") != null)
                location = savedInstanceState.getParcelable("location");
        }
        //pedimos permisos
        askPermissions();
        //desactivamos el control de content para target sdk 28 ejecutando en +24
        disableDeathOnFileUriExposure();
        // desactivamos fabs
        disableFabs();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (media != null) outState.putSerializable("media", media);
        if (location != null) outState.putParcelable("location", location);
        super.onSaveInstanceState(outState);
    }

    private void askPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION);
    }

    private void disableDeathOnFileUriExposure() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadDB() {
        db.open();
        lista.addAll(db.getAll());
        db.close();
        if (lista != null) {
            adaptador.setList(lista);
            adaptador.notifyDataSetChanged();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view == findViewById(R.id.fabPhoto)) {
            handlePhoto();
        } else {
            handleVideo();
        }
    }

    /**
     * Gestionamos video creando un file mp4, generando un objeto media y psandole file
     * al intent para grabar video
     */
    private void handleVideo() {
        File file = createFile("MP4");
        createMedia(file);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    /**
     * Gestionamos imagen creando un file jpg, generando un objeto media y pasandole
     * file al intent para tomar imagen
     */
    private void handlePhoto() {
        File file = createFile("JPG");
        createMedia(file);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Genera un objeto media en base a los datos del file generado y la localizacion actual
     *
     * @param file
     */
    private void createMedia(File file) {
        String fileString = file.toString();
        String name = fileString.substring(fileString.lastIndexOf('/') + 1, fileString.length());
        String path = fileString.substring(0, fileString.lastIndexOf('/'));
        String ext = name.substring(name.lastIndexOf("."), name.length());
        media = new Media();
        media.setName(name);
        media.setFile(path);
        media.setPhotoOrVideo((ext.contentEquals(".jpg")) ? REQUEST_IMAGE_CAPTURE : REQUEST_VIDEO_CAPTURE);
        media.setLatitude(location.getLatitude());
        media.setLongitude(location.getLongitude());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_VIDEO_CAPTURE)
                && resultCode == RESULT_OK) {
            // Introducimos el archivo media actual en la base de datos
            db.open();
            db.insert(media);
            // volvemos a refrescar toda la lista de la base de datos asi nos aseguramos que todos
            // los objetos media tienen id para posteriormente poder gestionar el borrado
            lista = db.getAll();
            db.close();
            // pasamos la nueva lista al adaptador y notificamos
            adaptador.setList(lista);
            adaptador.notifyDataSetChanged();
        } else {
            // si el intent no ha sido un exito quiere decir que el usuario ha cancelado y debemos
            // borrar el file que le pasamos al intent ya que se habra escrito sin datos
            File file = new File(media.getFile() + File.separator + media.getName());
            file.delete();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    // We have permissions: do all the things!!!
                    try {
                        // Go go database
                        loadDB();
                        // location Manager loading and updates from gps and network
                        gestorLoc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        gestorLoc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
                        //gestorLoc.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
                        // just to be sure that we have something fast and accurate
                        // if not we will wait for location onLocationChange callback anyway
                        // location = gestorLoc.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        //if (location == null){location = gestorLoc.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);}
                    } catch (SecurityException e) {
                        //
                    }
                } else {
                    //Finishes app
                    this.finishAffinity();
                }
        }
    }

    private File createFile(String type) {
        String prefix = null;
        String sufix = null;

        if (type.equals("JPG")) {
            prefix = "JPEG_";
            sufix = ".jpg";
        } else if (type.equals("MP4")) {
            prefix = "MP4_";
            sufix = ".mp4";
        }
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = prefix + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory("Multimedia");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    sufix,         /* sufix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (this.location == null) {
            enableFabs();
            showSnack("Location Available", Color.GREEN);
        }
        this.location = location;
    }

    /**
     * Activamos fabs
     */
    private void enableFabs() {
        photoFab.setEnabled(true);
        videoFab.setEnabled(true);
        photoFab.setAlpha(1f);
        videoFab.setAlpha(1f);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        String missatge = "";
        switch (i) {
            case OUT_OF_SERVICE:
                missatge = "GPS status: Out of service";
                break;
            case TEMPORARILY_UNAVAILABLE:
                missatge = "GPS status: Temporarily unavailable";
                break;
            case AVAILABLE:
                missatge = "GPS status: Available";
                break;
        }
        //Toast.makeText(this, missatge, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String s) {
        showSnack("GPS ON - Awaiting data...", Color.YELLOW);
    }

    @Override
    public void onProviderDisabled(String s) {
        location = null;
        disableFabs();
        showSnack("GPS OFF", Color.RED);
    }

    /**
     * Desactivamos fabs siempre y cuando no haya ubicacion
     */
    private void disableFabs() {
        if (location == null) {
            photoFab.setEnabled(false);
            videoFab.setEnabled(false);
            photoFab.setAlpha(0.5f);
            videoFab.setAlpha(0.5f);
        }
    }

    /**
     * Generamos un Snackbar que sabe mejor que una Tostada
     * @param msg Texto del mensaje a mostrar
     * @param color Color."color" doh!
     */
    public void showSnack(String msg, int color) {
        Snackbar snack = Snackbar.make(recyclerView, msg, Snackbar.LENGTH_SHORT);
        View view = snack.getView();
        TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(color);
        snack.show();
    }
}
