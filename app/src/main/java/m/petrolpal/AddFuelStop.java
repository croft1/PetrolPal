package m.petrolpal;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import m.petrolpal.Models.FuelStop;
import m.petrolpal.Tools.DatabaseHelper;

public class AddFuelStop extends AppCompatActivity {

    EditText inCost;
    EditText inOdom;
    EditText inQuantity;
    EditText inDate;
    EditText inLocation;
    ImageView iconCalendar;
    ImageView iconLocation;
    ImageView iconCamera;

    Button addButton;

    LatLng stopLatLng;
    String currentPhotoPath;

    InterstitialAd mInterstitialAd;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_LOCATION_FETCH = 2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fuel_stop);


        DatabaseHelper dbhelper = new DatabaseHelper(getApplicationContext());
        ArrayList <FuelStop> fuelStops = new
                ArrayList<>(dbhelper.getAllFuelStops().values());


        inCost = (EditText) findViewById(R.id.inputCost);
        inOdom = (EditText) findViewById(R.id.inputOdom);
        inQuantity = (EditText)  findViewById(R.id.inputQuantity);
        inDate = (EditText) findViewById(R.id.inputDate);
        inLocation = (EditText) findViewById(R.id.inputLoc);
        addButton = (Button) findViewById(R.id.addButton);
        iconCalendar = (ImageView) findViewById(R.id.addDateIcon);
        iconLocation = (ImageView) findViewById(R.id.addLocationIcon);
        iconCamera = (ImageView) findViewById(R.id.cameraView);

        //to open dialog on first click
        inDate.setClickable(true);
        inDate.setFocusable(false);
        inLocation.setClickable(true);
        inLocation.setFocusable(false);

        View.OnClickListener dateListener = new View.OnClickListener() {
            Calendar c = Calendar.getInstance();
            @Override
            public void onClick(View v) {
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, monthOfYear);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String dateFormat = sdf.format(c.getTime());
                        inDate.setText(sdf.format(c.getTime()));

                    }
                };



                new DatePickerDialog(AddFuelStop.this, date,
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        };

        View.OnClickListener addLocationListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddFuelStop.this, PickLocationActivity.class) ,REQUEST_LOCATION_FETCH);
                overridePendingTransition(R.anim.slide_in_right, R.transition.fade_out);
            }
        };

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.add_stop_interstitial_ad_unit));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();

            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(inputsEmpty()) {


                    FuelStop f = new FuelStop(
                            inDate.getText().toString(),
                            Double.valueOf(inQuantity.getText().toString()),
                            Double.valueOf(inCost.getText().toString()),
                            Integer.valueOf(inOdom.getText().toString()),
                            stopLatLng.latitude,
                            stopLatLng.longitude
                    );
                    f.setImageUri(currentPhotoPath);
                    Intent i = new Intent(AddFuelStop.this, TabsActivity.class);
                    i.putExtra(TabsActivity.ADD_REQUEST, f);
                    setResult(RESULT_OK, i);

                    /* for showing an add after creation
                    Intent adIntent = new Intent(AddFuelStop.this, InterstitialAdActivity.class);
                    startActivity(adIntent);
                    */

                    overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Fill in all fields", Toast.LENGTH_SHORT).show();

                }

            }

        });

        iconCamera.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              dispatchTakePictureIntent();
                          }
                      }
        );

        iconLocation.setOnClickListener(addLocationListener);
        inLocation.setOnClickListener(addLocationListener);

        iconCalendar.setOnClickListener(dateListener);
        inDate.setOnClickListener(dateListener);


//        inDate.getText().toString() != null &&
//                inCost.getText().toString() != null &&
//                inQuantity.getText().toString() != null &&
//                inOdom.getText().toString() != null);




    }


    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String imageFileName = "PPRCPT_" + timeStamp + "_";


        File folder = new File(Environment.getExternalStorageDirectory(), "Fuel Receipts");
        folder.mkdirs();

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                folder      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
       // currentPhotoPath = "file:" + image.getAbsolutePath();

        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            iconCamera.setImageBitmap(imageBitmap);

        }else if ( requestCode == REQUEST_LOCATION_FETCH && resultCode == RESULT_OK){
            Bundle bundle = data.getParcelableExtra("position");
            stopLatLng = bundle.getParcelable("bundle");
            boolean isCurrentLoc = data.getExtras().getBoolean("isCurrent");

            //text of in location field
            if(isCurrentLoc){
                inLocation.setText("Current Location");
            }else{
                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                try{
                    List<Address> addresses = gcd.getFromLocation(stopLatLng.latitude, stopLatLng.longitude, 1);
                    if (addresses.size() > 0)
                        inLocation.setText("Around " + addresses.get(0).getLocality());
                }catch(IOException e){
                    e.printStackTrace();
                }

            }

        }


    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            try{
                photoFile = createImageFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            if(photoFile != null){
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

            }
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
    }


    private boolean inputsEmpty(){

        boolean x = (inDate.getText().toString().equals("") ||
                inQuantity.getText().toString().equals("") ||
                inCost.getText().toString().equals("") ||
                inOdom.getText().toString().equals("") ||
                stopLatLng == null
        );
        return !x;






    }





}




