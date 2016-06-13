package m.petrolpal;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.nearby.messages.PublishCallback;

import java.util.ArrayList;

import m.petrolpal.Models.FuelStop;
import m.petrolpal.Tools.DatabaseHelper;

/**
 * Created by Michaels on 3/5/2016.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String ARG_PAGE = "ARG_PAGE";

    private static GoogleMap mMap;
    private static int latitude;
    private static int longitude;
    private static View view;
    private static LocationManager locationManager;
    public static final int ADD_FUEL_STOP = 0;
    public static final String ADD_REQUEST = "add_stop";

    private ArrayList<FuelStop> fuelStops;

    private LatLng pickedPosition = new LatLng(-38.041, 145.339);


    public static MapActivity newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        MapActivity map = new MapActivity();

        return map;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tab_map);


        final long MIN_TIME = 400;
        final float MIN_DISTANCE = 1000;

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
        } catch (SecurityException e) {
            Toast.makeText(this, "Location isn't available.", Toast.LENGTH_LONG);
        }


        // TESTOMG
        latitude = -44;
        longitude = -113;


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        fuelStops = new ArrayList<>(db.getAllFuelStops().values());

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = mapFragment.getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null)
                setUpMap();
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                mMap.clear();
                pickedPosition = point;
                mMap.addMarker(new MarkerOptions()
                        .position(point)
                        .title("Fuel Stop")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

                //when a marker is placed, create a stop
                sendLocation();
                finish();


            }
        });

        mMap.getUiSettings().setZoomControlsEnabled(true);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADD_FUEL_STOP:
                if (resultCode == RESULT_OK && data.hasExtra(ADD_REQUEST)) {
                    FuelStop fs = data.getParcelableExtra(ADD_REQUEST);
                    DatabaseHelper dbhelper = new DatabaseHelper(getApplicationContext());
                    dbhelper.addStop(fs);
                }
                break;
            default:

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //not getting called!?


    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        if (mMap != null) {

            LatLngBounds VIC = new LatLngBounds(
                    new LatLng(-37.863, 144.942), new LatLng(-37.796, 145.023));

            updateCamera(VIC.getCenter());
            updateStopMarkers();
        }

    }

    private void updateCamera(LatLng center) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(center));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateStopMarkers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStopMarkers();
    }

    private void setUpMap() {

        LatLngBounds VIC = new LatLngBounds(
                new LatLng(-37.863, 144.942), new LatLng(-37.796, 145.023));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(VIC.getCenter()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                LatLngBounds AUSTRALIA = new LatLngBounds(
                        new LatLng(-44, 113), new LatLng(-10, 154));
                LatLngBounds VIC = new LatLngBounds(
                        new LatLng(-44, 113), new LatLng(-10, 154));
            }
        });


        //add markers for each stop
        updateStopMarkers();


    }

    public void updateStopMarkers() {
        DatabaseHelper dbhelper = new DatabaseHelper(getApplicationContext());
        ArrayList<FuelStop> fs = new ArrayList<>(dbhelper.getAllFuelStops().values());
        for (int i = 0; i < fs.size(); i++) {

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(fs.get(i).getLatitude(), fs.get(i).getLongitude()))
                    .title("Fuel Stop")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
    }

    private void sendLocation() {
        Bundle loc = new Bundle();
        loc.putParcelable("bundle", pickedPosition);
        Intent intent = new Intent(MapActivity.this, AddFuelStop.class);
        intent.putExtra("newPos", loc);
        setResult(RESULT_OK, intent);
        startActivityForResult(intent, ADD_FUEL_STOP);
    }


}