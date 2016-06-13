package m.petrolpal;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by m on 6/01/2016.
 */
public class PickLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    LatLng pickedPosition = new LatLng(-38.041, 145.339);    //default

    int timesPressed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tab_map);


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final GoogleMap map = mapFragment.getMap();


        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location;
        boolean gpsEnabled = false;
        boolean networkEnabled = false;
        timesPressed = 0;

        try{
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception e ){
            e.printStackTrace();
        }
        try{
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch (Exception e ){
            e.printStackTrace();
        }


        if(!gpsEnabled && !networkEnabled){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getResources().getString(R.string.gps_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.loc_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
                    //get gps
                }
            });
            dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }else{
            double latitude = -38.04;
            double longitude = 145.26;
            try{
                map.setMyLocationEnabled(true);
                location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                latitude = location.getLatitude();
                longitude = location.getLongitude();

            }catch (SecurityException e){

            }

            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);



            pickedPosition = new LatLng(latitude,longitude);

            map.addMarker(new MarkerOptions()
                    .position(pickedPosition)
                    .title("This stop")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));


            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Use current location?").setMessage("Should we quickly use your current location for this fuel stop?");
            b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();



                    sendLocation(true);



                }
            });
            b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            b.setCancelable(false);
            b.create().show();



         }



        map.moveCamera(CameraUpdateFactory.newLatLng(pickedPosition));
        map.animateCamera(CameraUpdateFactory.zoomTo(10));

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                timesPressed++;
                if(timesPressed > 3){
                    Toast.makeText(getApplicationContext(), "Hold down to select point", Toast.LENGTH_LONG);
                }
            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                map.clear();

                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Recent Stop")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                pickedPosition = latLng;

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.pick_location, menu);

        MenuItem confirm = menu.findItem(R.id.action_confirm_location);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_confirm_location:


                sendLocation(false);    //

                break;
            default:

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    private void sendLocation(boolean currentLocation){
        Bundle loc = new Bundle();
        loc.putParcelable("bundle", pickedPosition);
        Intent intent = new Intent(PickLocationActivity.this, AddFuelStop.class);
        intent.putExtra("position", loc);
        intent.putExtra("isCurrent", currentLocation);
        setResult(RESULT_OK, intent);
        finish();
    }





}
