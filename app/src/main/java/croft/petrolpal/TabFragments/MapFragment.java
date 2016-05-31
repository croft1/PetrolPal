package croft.petrolpal.TabFragments;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import croft.petrolpal.Models.FuelStop;
import croft.petrolpal.R;
import croft.petrolpal.Tools.DatabaseHelper;

/**
 * Created by Michaels on 3/5/2016.
 */
public class MapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mTab;

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    private static GoogleMap mMap;
    private static int latitude;
    private static int longitude;
    private static View view;


    public static MapFragment newInstance(int page){
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        MapFragment frag = new MapFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTab = getArguments().getInt(ARG_PAGE);





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        view = inflater.inflate(R.layout.fragment_tab_map, container, false);


        // TESTOMG
        latitude = -44;
        longitude = -113;

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map)).getMap();       //TODO getAsyncMap try it
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

                final LatLng BERWICK_CAMPUS = new LatLng(38.041, 145.339);
                final LatLng CAULFIELD_CAMPUS = new LatLng(37.877, 145.045);
                final LatLng CLAYTON_CAMPUS = new LatLng(37.912, 145.133);

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(point.latitude + 20, point.longitude + 20))
                        .title("Fuel Stop")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                DatabaseHelper dbhelper = new DatabaseHelper(getContext());

                ArrayList<FuelStop> fs = new ArrayList<>(dbhelper.getAllFuelStops().values());

                for(int i = 0; i < fs.size(); i++){

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(fs.get(i).getLatitude(), fs.get(i).getLongitude()))
                            .title("Fuel Stop")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }

                final LatLng BERWICK_CAMPUS = new LatLng(38.041, 145.339);
                final LatLng CAULFIELD_CAMPUS = new LatLng(37.877, 145.045);
                final LatLng CLAYTON_CAMPUS = new LatLng(37.912, 145.133);
                final int h =2 ;

                mMap.addMarker(new MarkerOptions()
                        .position(BERWICK_CAMPUS)
                        .title("Fuel Stop")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                int s = 1;


            }
        });



        return view;
    }


    //
    //knk


    private void setUpMap() {



        // For dropping a marker at a point on the Map




        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                LatLngBounds AUSTRALIA = new LatLngBounds(
                        new LatLng(-44, 113), new LatLng(-10, 154));

                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(AUSTRALIA, 5));

                final LatLng BERWICK_CAMPUS = new LatLng(38.041, 145.339);
                final LatLng CAULFIELD_CAMPUS = new LatLng(37.877, 145.045);
                final LatLng CLAYTON_CAMPUS = new LatLng(37.912, 145.133);

                mMap.addMarker(new MarkerOptions()
                        .position(BERWICK_CAMPUS)
                        .title("Fuel Stop")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            }
        });

        DatabaseHelper dbhelper = new DatabaseHelper(getContext());

        ArrayList<FuelStop> fs = new ArrayList<>(dbhelper.getAllFuelStops().values());

        for(int i = 0; i < fs.size(); i++){

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(fs.get(i).getLatitude(), fs.get(i).getLongitude()))
                    .title("Fuel Stop")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }


    }







}
