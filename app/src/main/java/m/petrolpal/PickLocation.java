package m.petrolpal;

import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by m on 6/01/2016.
 */
public class PickLocation extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    LatLng pickedPosition = new LatLng(-38.041, 145.339);    //default

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tab_map);


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        GoogleMap map = mapFragment.getMap();


        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location;
        boolean gpsEnabled = false;
        boolean networkEnabled = false;


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

                    sendLocation(pickedPosition);



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
        map.animateCamera(CameraUpdateFactory.zoomTo(5));


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    private void sendLocation(LatLng latLng){
        Bundle loc = new Bundle();
        loc.putParcelable("PICKED_POSITION", pickedPosition);
        Intent intent = new Intent(PickLocation.this, AddFuelStop.class);
        intent.putExtra("ADD_LOCATION_BUNDLE", pickedPosition);
        finish();
    }





}
