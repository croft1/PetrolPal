package m.petrolpal.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Michaels on 29/4/2016.
 */
public class FuelStop implements Parcelable, Comparable<FuelStop> {

    public static final String TABLE_NAME = "fuelstop";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COST = "cost";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_ODOMETER = "odometer";
    public static final String COLUMN_LATITUDE= "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    public static final String CREATE_STATEMENT = "CREATE TABLE " +
            TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COLUMN_COST + " REAL NOT NULL, " +
            COLUMN_DATE + " TEXT NOT NULL, " +
            COLUMN_QUANTITY + " REAL NOT NULL, " +
            COLUMN_ODOMETER + " INTEGER NOT NULL, " +
            COLUMN_LATITUDE + " REAL NOT NULL, " +
            COLUMN_LONGITUDE + " REAL NOT NULL" +
            ")";

    @Override
    public int compareTo(FuelStop another) {
        if(getDate() == null || another.getDate() == null){
            return 0;
        }

        return getDate().compareTo(another.getDate());
    }

    public static final SimpleDateFormat DATE_FORMAT_DEFAULT = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);

    private long id;
    private Date date;

    private double overallCost;
    private double quantityBought;
    private int odometer;
    private Double latitude;
    private Double longitude;



    private String imageLocation;

    private static int generatedId = 999;


    public FuelStop(long id, String date, double quantityBought, double overallCost, int odometer, Double latitude, Double longitude) {
        //called coming from a db for example

        this.id = id;

        try{
            this.date = DATE_FORMAT_DEFAULT.parse(date);
        }catch(ParseException e){
            e.printStackTrace();
            this.date = new Date(0);
        }
        this.overallCost = overallCost;
        this.odometer = odometer;
        this.latitude = latitude;
        this.longitude = longitude;
        this.quantityBought = quantityBought;

        generatedId++;      //to keep track of which id we're up to when creating objects already stored in the db
    }

    public FuelStop(String ddMMyyyy, double quantityBought, double overallCost, int odometer, Double latitude, Double longitude) {

        id = generatedId++;
        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        Date date = new Date(seconds);
        try{
            date = DATE_FORMAT_DEFAULT.parse(ddMMyyyy);
        }catch(ParseException e){
            e.printStackTrace();
            this.date = new Date(0);
        }

        this.date = date;
        this.overallCost = overallCost;
        this.odometer = odometer;
        this.latitude = latitude;
        this.longitude = longitude;
        this.quantityBought = quantityBought;
    }


    public FuelStop(Parcel in) {
        this.id = in.readLong();
        try{
            this.date = DATE_FORMAT_DEFAULT.parse(in.readString());
        }catch(ParseException e){
            e.printStackTrace();
            this.date = new Date(0);
        }
        this.date = new Date(in.readLong());
        this.quantityBought = in.readDouble();
        this.overallCost = in.readDouble();
        this.odometer = in.readInt();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();


    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
       // parcel.writeLong(date.getTime());
        parcel.writeString(DATE_FORMAT_DEFAULT.format(date)) ;
        parcel.writeDouble(quantityBought);
        parcel.writeDouble(overallCost);
        parcel.writeInt(odometer);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }



    public static final Creator<FuelStop> CREATOR = new Creator<FuelStop>() {
        @Override
        public FuelStop createFromParcel(Parcel in) {
            return new FuelStop(in);
        }
        @Override
        public FuelStop[] newArray(int size) {
            return new FuelStop[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public int getOdometer() {
        return odometer;
    }

    public double getQuantityBought() {
        return quantityBought;
    }

    public double getOverallCost() {
        return overallCost;
    }

    public Date getDate() {
        return date;
    }

    public long getId() {
        return id;
    }

    public double getCostPerLiter(){
        return ( quantityBought / overallCost);
    }

    public String getimageLocation() {
        return imageLocation;
    }

    public void setImageUri(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    //image of receipt





}
