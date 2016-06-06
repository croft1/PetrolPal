package m.petrolpal;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    Button fuelB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);


        //nav drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        fuelB =  (Button) findViewById(R.id.fuelButton);

        fuelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, TabsActivity.class));
                overridePendingTransition(0, 0);
            }
        });






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fuel, menu);

        MenuItem dummy = menu.findItem(R.id.action_add_dummy_stops);
        MenuItem license = menu.findItem(R.id.action_licenses);
        MenuItem refresh = menu.findItem(R.id.action_add_refresh);
        MenuItem feedback = menu.findItem(R.id.action_feedback);
        MenuItem remove = menu.findItem(R.id.action_remove_all_stops);
        MenuItem sort = menu.findItem(R.id.action_sort);
        MenuItem settings = menu.findItem(R.id.action_settings);

        dummy.setVisible(false);
        refresh.setVisible(false);
        remove.setVisible(false);
        sort.setVisible(false);

        feedback.setVisible(true);
        license.setVisible(true);
        settings.setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:

               // startActivity(new Intent(MainActivity.this, SettingsActivity.class));
               // overridePendingTransition(R.anim.slide_in_left, R.transition.fade_out);

                break;
            case R.id.action_feedback:

                return true;

            case R.id.action_licenses:
                doAlertDialog(R.string.licenses_title, R.string.all_licenses);

            default:

        }
        return super.onOptionsItemSelected(item);
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
//nav drawer stuff
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_images) {
            // Handle the camera action
        } else if (id == R.id.nav_map) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_feedback) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
