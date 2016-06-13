package m.petrolpal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.cast.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import m.petrolpal.Models.FuelStop;
import m.petrolpal.TabFragments.SummaryFragment;
import m.petrolpal.Tools.AlarmReceiver;
import m.petrolpal.Tools.DatabaseHelper;
import m.petrolpal.Tools.FragmentPagerAdapter;



public class TabsActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int ADD_FUEL_STOP = 0;

    public static final String ADD_REQUEST = "addfuelstop";
    private static final String FRAGMENT_UPDATE_FILTER = "fragmentupdater";



    private static final int SUMMARY_FRAG_ID = 0;
    private static final int LIST_FRAG_ID = 1;
    private static final int MAP_FRAG_ID = 2;
    private static final int STATS_FRAG_ID = 3;

    FloatingActionButton fab;
    public static FragmentManager fragmentManager;
    private DatabaseHelper dbhelper;
    private boolean isSorted = false;
    private ViewPager vp;

    //chromecast
    private MediaRouter mediaRouter;
    private MediaRouteSelector mediaRouteSelector;
    private CastDevice selectedDevice;
    private MediaRouter.Callback mediaRouterCallback;
    private Cast.Listener castListener;
    private GoogleApiClient apiClient;
    private GoogleApiClient.ConnectionCallbacks connectionCallbacks;
    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener;
    private PetrolChannel petrolChannel;
    private String sessionId;

    private boolean waitingForReconnect;
    private boolean appStarted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_tabs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //implement nav drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.tabs_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setTitle("PetrolPal");



        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TabsActivity.this, AddFuelStop.class);
                startActivityForResult(i, ADD_FUEL_STOP);
                overridePendingTransition(R.anim.slide_in_right, R.transition.fade_out);
            }
        });

        fragmentManager = getSupportFragmentManager();
        dbhelper = new DatabaseHelper(getApplicationContext());



        //attack viewpagers adapter for fragments
        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(new FragmentPagerAdapter(
                getSupportFragmentManager(), TabsActivity.this
        ));

        //setup layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.fuel_sliding_tabs);
        tabLayout.setupWithViewPager(vp);

        //chromecast


        //out router that connects and funnels cast connection
        mediaRouter = MediaRouter.getInstance(getApplicationContext());
        mediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.
                        categoryForCast(getResources().getString(R.string.cast_app_id))).build();
        mediaRouterCallback = new MediaRouterCallback();


        //settings up notification
        Calendar calendar = Calendar.getInstance();
        //at this time a notification will be sent
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        Intent intent = new Intent(TabsActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(TabsActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        AlarmManager alarmManager = (AlarmManager) TabsActivity.this.getSystemService(TabsActivity.this.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fuel, menu);

        //cast
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);        //create media menu
        MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(mediaRouteSelector);  //filter the selected devices desired

        MenuItem dummy = menu.findItem(R.id.action_add_dummy_stops);
        dummy.setVisible(false);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ArrayList<FuelStop> fs = new ArrayList<>(dbhelper.getAllFuelStops().values());

        switch (id){
            case R.id.action_settings:

                //startActivity(new Intent(TabsActivity.this, SettingsActivity.class));
                //overridePendingTransition(R.anim.slide_in_left, R.transition.fade_out);
                Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT);
                break;

            case R.id.action_sort:

                //sort array and

                String sort;
                if(fs.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Add stops to sort", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if(isSorted){
                    Collections.sort(fs, Collections.<FuelStop>reverseOrder());
                }else{
                    Collections.sort(fs);
                }

                //update to sort
                vp.getAdapter().notifyDataSetChanged();
                //todo sort

                return true;



            case R.id.action_add_dummy_stops:
                addDummyData();

            case R.id.action_remove_all_stops:
                for(FuelStop stop: fs){
                    dbhelper.removeStop(stop);
                }
                fs.clear();



            default:

        }
        return super.onOptionsItemSelected(item);
    }

    public void addDummyData(){
        //date d, double quantityBought, double overallCost, int odometer, Double latitude, Double longitude
        dbhelper.addStop(new FuelStop("11/11/1999", 111, 111, 111, -34.2, 55.55));
        dbhelper.addStop(new FuelStop("22/11/2003", 222, 222, 222, -22.2, 33.33));
        dbhelper.addStop(new FuelStop("22/11/2014", 333, 333, 333, -33.3, 22.22));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        switch(requestCode){
            case ADD_FUEL_STOP:
                if(resultCode == RESULT_OK && data.hasExtra(ADD_REQUEST)){

                    FuelStop fs = data.getParcelableExtra(ADD_REQUEST);
                    dbhelper.addStop(fs);

                    SummaryFragment summaryFragment = (SummaryFragment) fragmentManager.findFragmentById(SUMMARY_FRAG_ID);




                    /*
                    Intent i = new Intent(FRAGMENT_UPDATE_FILTER);
                    i.putExtra("key","data");
                    i.putExtra("fragmentno",SUMMARY_FRAG_ID); // Pass the unique id of fragment we talked abt earlier
                    this.sendBroadcast(i);
                    */

                    vp.getAdapter().notifyDataSetChanged();

                }

                Toast.makeText(this, "Stop added", Toast.LENGTH_SHORT);//TODO add stop name here

                break;
            default:
        }


    }

    public void doAlertDialog(String title, String message){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(title).setMessage(message);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        b.setCancelable(true);
        b.create().show();
    }

    public void doAlertDialog(int title, int message){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(getString(title)).setMessage(getString(message));
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        b.setCancelable(true);
        b.create().show();
    }




    //Navigation Drawer stuff
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_images) {
            // Handle the camera action
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivity(intent);


        } else if (id == R.id.nav_map) {
            Intent i = new Intent(TabsActivity.this, MapActivity.class);
            startActivity(i);
            overridePendingTransition(R.transition.fade_in, R.transition.fade_out);

        } else if (id == R.id.nav_settings) {
            Intent i = new Intent(TabsActivity.this, SettingsActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.transition.fade_out);

        } else if (id == R.id.nav_feedback) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Leave feedback");
            final EditText input = new EditText(this);

            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String REF_ID = "FEEDBACK";
                    FirebaseDatabase fbDb = FirebaseDatabase.getInstance();
                    DatabaseReference fbRef = fbDb.getReference(REF_ID);
                    fbRef.setValue(input.getText().toString());
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();






        }else if ( id == R.id.nav_licenses) {
            doAlertDialog(R.string.license_dialog_title, R.string.all_licenses);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.tabs_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.tabs_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
        }
    }



    //Chromecast stuff

    private class MediaRouterCallback extends MediaRouter.Callback{
        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
            selectedDevice = CastDevice.getFromBundle(info.getExtras());
            String routeId = info.getId();


        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
            //tearDown();
            selectedDevice = null;
        }



    }

    //perform action when searching
    @Override
    protected void onStart() {
        super.onStart();
        mediaRouter.addCallback(mediaRouteSelector, mediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);

        launchReceiver();
    }

    @Override
    protected void onStop() {
        mediaRouter.removeCallback(mediaRouterCallback);
        super.onStop();
    }


    private void launchReceiver(){
        try{

            //try and connect
            castListener = new Cast.Listener() {

                @Override
                public void onApplicationDisconnected(int errorCode) {
                    Log.d("CAST FAIL", "application has stopped");
                    teardown();
                }

            };


            //create
            Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions.builder(selectedDevice, castListener);
            connectionCallbacks = new ConnectionCallbacks();
            connectionFailedListener = new ConnectionFailedListener()  {

            };


            //connect the client with the device
            apiClient = new GoogleApiClient.Builder(this)
                    .addApi(Cast.API, apiOptionsBuilder.build())
                    .addConnectionCallbacks(connectionCallbacks)
                    .addOnConnectionFailedListener(connectionFailedListener)
                    .build();

            apiClient.connect();

        }catch(Exception e){
            Log.e("CAST FAIL", "Failed launchReceiver", e);
        }
    }


    private class ConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(@Nullable Bundle connectionHint) {
            if(apiClient == null){
                return;     //abrupt disconnect
            }
            if(waitingForReconnect){
                waitingForReconnect = false;

                //try and reconnect
                if ((connectionHint != null)
                        && connectionHint.getBoolean(Cast.EXTRA_APP_NO_LONGER_RUNNING)) {
                    Log.d(TAG, "App  is no longer running");
                    teardown();
                } else {
                    // Re-create channel which is how we communiucate with device
                    try {
                        Cast.CastApi.setMessageReceivedCallbacks(
                                apiClient,
                                petrolChannel.getNamespace(),
                                petrolChannel);
                    } catch (IOException e) {
                        Log.e(TAG, "Exception while creating channel", e);
                    }
                }


            }else{
                try{

                    //to actually send something  -- not quite done.
                    Cast.CastApi.launchApplication(apiClient, getResources().getString(R.string.cast_app_id), false)
                            .setResultCallback(
                                    new ResultCallback<Cast.ApplicationConnectionResult>() {
                                        @Override
                                        public void onResult(@NonNull Cast.ApplicationConnectionResult result) {
                                            Status status = result.getStatus();
                                            if(status.isSuccess()){
                                                ApplicationMetadata applicationMetadata = result.getApplicationMetadata();
                                                String sessionId = result.getSessionId();
                                                String appStatus = result.getApplicationStatus();
                                                boolean wasLaunched = result.getWasLaunched();

                                                appStarted = true;
                                                petrolChannel = new PetrolChannel();
                                                try{
                                                    Cast.CastApi.setMessageReceivedCallbacks(apiClient, petrolChannel.getNamespace(), petrolChannel);
                                                }catch (IOException e){
                                                    Log.d(TAG, "Exception creating channel", e);
                                                }


                                                //....



                                            }else{
                                                teardown();
                                            }
                                        }
                                    }
                            );
                }catch(Exception e ){
                    Log.d(TAG, "Failed to launch", e);
                }
            }
        }



        @Override
        public void onConnectionSuspended(int i) {
            waitingForReconnect = true;
        }

    }

    private class ConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener{
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            teardown();
        }
    }

    class PetrolChannel implements Cast.MessageReceivedCallback{
        @Override
        public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
            Log.d(TAG, "Message received" + message);
        }
        public String getNamespace(){
            return getString(R.string.namespace);
        }
    }

    //used to return everything to its original state and close channels
    private void teardown() {
        Log.d(TAG, "teardown");
        if (apiClient != null) {
            if (appStarted) {
                if (apiClient.isConnected() || apiClient.isConnecting()) {
                    try {
                        Cast.CastApi.stopApplication(apiClient, sessionId);
                        if (petrolChannel != null) {
                            Cast.CastApi.removeMessageReceivedCallbacks(
                                    apiClient,
                                    petrolChannel.getNamespace());
                            petrolChannel = null;
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception while removing channel", e);
                    }
                    apiClient.disconnect();
                }
                appStarted = false;
            }
            apiClient = null;
        }
        selectedDevice = null;
        waitingForReconnect = false;
        sessionId = null;
    }








}

