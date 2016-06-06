package m.petrolpal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import m.petrolpal.Models.FuelStop;
import m.petrolpal.TabFragments.SummaryFragment;
import m.petrolpal.Tools.DatabaseHelper;
import m.petrolpal.Tools.FragmentPagerAdapter;

public class TabsActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    public static final int ADD_FUEL_STOP = 0;

    public static final String ADD_REQUEST = "addfuelstop";

    FloatingActionButton fab;
    public static FragmentManager fragmentManager;
    private DatabaseHelper dbhelper;
    private boolean isSorted = false;

    private ListView summaryLv;
    private ListView expListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_tabs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.tabs_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setTitle("Fuel Logger");

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




        ViewPager vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(new FragmentPagerAdapter(
                getSupportFragmentManager(), TabsActivity.this
        ));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.fuel_sliding_tabs);
        tabLayout.setupWithViewPager(vp);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fuel, menu);
        MenuItem refresh = menu.findItem(R.id.action_settings);
        refresh.setEnabled(true);

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

            case R.id.action_feedback:

                for(FuelStop toDelete : fs) {
                    dbhelper.removeStop(toDelete);

                    Toast.makeText(getBaseContext(), (toDelete.getId() + " deleted"), Toast.LENGTH_SHORT);
                }
                return true;
            case R.id.action_sort:

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

                //todo sort

                return true;
            case R.id.action_licenses:
                doAlertDialog(R.string.licenses_title, R.string.all_licenses);

            case R.id.action_add_dummy_stops:
                addDummyData();

            case R.id.action_remove_all_stops:
                for(FuelStop stop: fs){
                    dbhelper.removeStop(stop);
                }
                fs.clear();
                updateLists();


            default:

        }
        return super.onOptionsItemSelected(item);
    }

    public void addDummyData(){
        //date d, double quantityBought, double overallCost, int odometer, Double latitude, Double longitude
        dbhelper.addStop(new FuelStop("11/11/1999", 111, 111, 111, 34.2, 55.55));
        dbhelper.addStop(new FuelStop("22/11/2003", 222, 222, 222, 22.2, 22.22));
        dbhelper.addStop(new FuelStop("22/11/2014", 333, 333, 333, 33.3, 22.22));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case ADD_FUEL_STOP:
                if(resultCode == RESULT_OK && data.hasExtra(ADD_REQUEST)){

                    FuelStop fs = data.getParcelableExtra(ADD_REQUEST);
                    dbhelper.addStop(fs);
                    updateLists();

                }

                Toast.makeText(getApplicationContext(), "Stop added", Toast.LENGTH_SHORT);//TODO add stop name here

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


    public void updateLists(){

        /*
        summaryLv = (ListView) findViewById(R.id.summaryFuelList);
        ((BaseAdapter) summaryLv.getAdapter()).notifyDataSetChanged();

        expListView = (ListView) findViewById(R.id.stopsExpandableList);
        ((BaseAdapter) expListView.getAdapter()).notifyDataSetChanged();


*/


    }


    //Navigation Drawer stuff
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_images) {
            // Handle the camera action
        } else if (id == R.id.nav_map) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_feedback) {

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


}

