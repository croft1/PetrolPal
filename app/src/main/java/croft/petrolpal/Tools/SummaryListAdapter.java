package croft.petrolpal.Tools;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import croft.petrolpal.Models.FuelStop;
import croft.petrolpal.R;

/**
 * Created by m on 5/24/2016.
 */
public class SummaryListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<FuelStop> fuelStops;

    private static class ViewHolder {
        TextView costLabel;
        TextView dateLabel;
        TextView literLabel;
    }

    public SummaryListAdapter(Context c, ArrayList<FuelStop> a) {
        this.context=c;
        this.fuelStops=a;
    }

    @Override
    public int getCount() {
        return fuelStops.size();
    }

    @Override
    public Object getItem(int position) {
        return fuelStops.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder vh;

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            if(position > fuelStops.size()){
                view = inflater.inflate(R.layout.item_list_end, null);

            }else{
                view = inflater.inflate(R.layout.item_fuel_group, null);
            }


            vh = new ViewHolder();


          //  vh.costLabel = (TextView) view.findViewById(R.id.groupCost);
            vh.dateLabel = (TextView) view.findViewById(R.id.groupDate);
          //  vh.literLabel = (TextView) view.findViewById(R.id.groupLiters);

            view.setTag(vh);
        }else {
            vh = (ViewHolder) view.getTag();
        }



        DateFormat dateFormat = new SimpleDateFormat("E, dd / MMM / yyyy ");
        Date d = fuelStops.get(position).getDate();




        vh.dateLabel.setText(dateFormat.format(d));


        return view;
    }
}

    // vh.literLabel.setText(Double.toString(fuelStops.get(position).getQuantityBought()) + "L");
    // vh.costLabel.setText("$" + Double.toString(fuelStops.get(position).getOverallCost()));

    //final double LOW = 105;
    //final double MED = 115;

        /*
        if(fuelStops.get(position).getCostPerLiter() < LOW){
            vh.costLabel.setTextColor(Color.parseColor("#00FF00"));
        }else if(fuelStops.get(position).getCostPerLiter() < MED){
            vh.costLabel.setTextColor(Color.parseColor("#FFF000"));
        }else{
            vh.costLabel.setTextColor(Color.parseColor("#FF0000"));
        }
*/
