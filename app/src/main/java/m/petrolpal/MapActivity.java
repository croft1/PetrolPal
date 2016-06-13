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

    final LatLng BERWICK_CAMPUS = new LatLng(37.041, 145.339);
    final LatLng CAULFIELD_CAMPUS = new LatLng(37.877, 145.045);
    final LatLng CLAYTON_CAMPUS = new LatLng(37.912, 145.133);



    public static MapActivity newInstance(int page){
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
        try{
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
        }catch (SecurityException e){
            Toast.makeText(this, "Location isn't available.", Toast.LENGTH_LONG);
        }


        // TESTOMG
        latitude = -44;
        longitude = -113;



        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        fuelStops = new ArrayList<>(db.getAllFuelStops().values());



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
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

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = googleMap;

            // Check if we were successful in obtaining the map.
            if (mMap != null)
                setUpMap();
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                mMap.addMarker(new MarkerOptions()
                        .position(point)
                        .title("Fuel Stop")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                //when a marker is placed, create a stop
                Intent i = new Intent(MapActivity.this, AddFuelStop.class);
                startActivityForResult(i, ADD_FUEL_STOP);
                overridePendingTransition(R.anim.slide_in_right, R.transition.fade_out);

            }
        });





    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        if(mMap != null){

            LatLngBounds VIC = new LatLngBounds(
                    new LatLng(-37.863, 144.942), new LatLng(-37.796, 145.023));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(VIC.getCenter()));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        }

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

        DatabaseHelper dbhelper = new DatabaseHelper(getApplicationContext());
        ArrayList<FuelStop> fs = new ArrayList<>(dbhelper.getAllFuelStops().values());

        //add markers for each stop
        for(int i = 0; i < fs.size(); i++){

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
}
