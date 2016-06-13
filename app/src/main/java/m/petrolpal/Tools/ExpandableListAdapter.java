package m.petrolpal.Tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.ReflectPermission;
import java.lang.reflect.Type;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import m.petrolpal.Models.FuelStop;
import m.petrolpal.R;

/**
 * Created by Michaels on 10/5/2016.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<FuelStop> fuelStops;
    //private HashMap<String, List<String>> fuelStopsChild;

    public ExpandableListAdapter(Context context, ArrayList<FuelStop> fuelStops ){
        this.context = context;
        this.fuelStops = fuelStops;
    }

    private static class ViewHolder{
        //TextView dateItem;
        TextView costItem;
        TextView quanItem;
        TextView odomItem;
        TextView locationItem;
        TextView perLiterItem;
        ImageView rcptPhoto;

    }




    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View view, ViewGroup parent) {
        ViewHolder vh;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



        if(view == null){

            view = inflater.inflate(R.layout.item_fuel_stop, null);
            vh = new ViewHolder();

            // vh.dateItem = (TextView) view.findViewById(R.id.dateItem);
            vh.costItem = (TextView) view.findViewById(R.id.costItem);
            vh.quanItem = (TextView) view.findViewById(R.id.quanItem);
            vh.odomItem = (TextView) view.findViewById(R.id.odomItem);
            vh.locationItem = (TextView) view.findViewById(R.id.locationItem);
            vh.perLiterItem = (TextView) view.findViewById(R.id.perLiterItem);
            vh.rcptPhoto = (ImageView) view.findViewById(R.id.receiptExListIcon);

            view.setTag(vh);

        }else{
            vh = (ViewHolder) view.getTag();
        }



        // String date = fuelStops.get(groupPosition).getDate().toString();
        String cost = String.valueOf(round(fuelStops.get(groupPosition).getOverallCost()));
        String quan = String.valueOf(round(fuelStops.get(groupPosition).getQuantityBought()));
        String odom = String.valueOf(fuelStops.get(groupPosition).getOdometer());
        String perL = Double.toString(round(fuelStops.get(groupPosition).getCostPerLiter())) + "c";

        // DateFormat dateFormat = new SimpleDateFormat("E, dd / MMM / yyyy ");
        // Date d = fuelStops.get(groupPosition).getDate();


        //vh.dateItem.setText(dateFormat.format(d));
        vh.costItem.setText("Overall cost: " + cost);
        vh.quanItem. setText("Quantity bought: " + quan);
        vh.odomItem.setText("Odometer: " + odom);
        vh.perLiterItem.setText("Cost Per L: " + perL);

        //since getting
        vh.locationItem.setVisibility(View.GONE);

        if(fuelStops.get(groupPosition).hasImage()){
            try{

                File file = new File(new URI(fuelStops.get(groupPosition).getimageLocation()));

                vh.rcptPhoto.setImageBitmap(getScaledBitmap (file.getPath(), 80, 80));


            }catch(Exception e){
                e.printStackTrace();

            }



        }






        //get localised position (suburb, city etc)

        /*
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        try{

            List<Address> addresses = gcd.getFromLocation(
                    fuelStops.get(groupPosition).getLatitude(),
                    fuelStops.get(groupPosition).getLongitude(),
                    1);

            if(addresses.size() > 0){
                vh.locationItem.setText(addresses.get(0).getLocality());
            }

        }catch(IOException e){
            e.printStackTrace();
        }

        */

        return view;
    }

    public double round(double in){
        return  Math.round(in * 100.0) / 100.0;
    }



    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {



        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



        if(groupPosition == fuelStops.size() && view == null){
            view = inflater.inflate(R.layout.item_list_end, null);
            TextView total = (TextView) view.findViewById(R.id.totalStops);
            total.setText(groupPosition);   //child position is the index of last child
            return view;
        }

        if(view == null){

            if(fuelStops.size() < 1){
                view = inflater.inflate(R.layout.item_list_end, null);
                TextView emptyListText;
                emptyListText = (TextView) view.findViewById(R.id.totalStops);
                emptyListText.setText(R.string.no_stops_to_show);


            }else{
                if(groupPosition > fuelStops.size()){
                    view = inflater.inflate(R.layout.item_list_end, null);
                    TextView endOfListText;
                    endOfListText = (TextView) view.findViewById(R.id.totalStops);
                    endOfListText.setText("Total stops: " + fuelStops.size());
                }else{
                    view = inflater.inflate(R.layout.item_fuel_group, null);

                    TextView date = (TextView) view.findViewById(R.id.groupDate);
                    // TextView liters = (TextView) view.findViewById(R.id.groupLiters);


                    DateFormat dateFormat = new SimpleDateFormat("E, dd / MMM / yyyy ");
                    Date d = fuelStops.get(groupPosition).getDate();

                    date.setText(dateFormat.format(d));
                    //  cost.setText("$" + Double.toString(fuelStops.get(groupPosition).getOverallCost()));
                    //   liters.setText(Double.toString(fuelStops.get(groupPosition).getQuantityBought()) + "L");

                    date.setTypeface(null, Typeface.BOLD);
                }

            }

        }

        view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLight));

        // TextView cost = (TextView) view.findViewById(R.id.groupCost);

        return view;
    }



    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }



    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getGroupCount() {
        return fuelStops.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //TODO child count unsure - fuelStops.size() give us other objects
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.fuelStops.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.fuelStops.get(groupPosition);    //// TODO: 5/22/2016 may need to change
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition;
    }

    public void update(ArrayList<FuelStop> newList){

        fuelStops = newList;
        notifyDataSetChanged();
    }

    //http://stackoverflow.com/questions/10473823/android-get-image-from-gallery-into-imageview
    private Bitmap getScaledBitmap(String path, int width, int height){
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, sizeOptions);
        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path, sizeOptions);
    }



    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }





}
