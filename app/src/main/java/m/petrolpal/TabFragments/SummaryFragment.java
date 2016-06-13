package m.petrolpal.TabFragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import m.petrolpal.Models.FuelStop;

import m.petrolpal.R;
import m.petrolpal.Tools.*;

/**
 * Created by Michaels on 3/5/2016.
 */
public class SummaryFragment extends android.support.v4.app.Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String RACV_FUEL_URL = "https://www.google.com.au/search?q=fuel+prices&oq=fuel+prices&aqs=chrome.0.0j69i60j0l4.2023j0j1&sourceid=chrome&ie=UTF-8";
    //"http://www.racv.com.au/wps/wcm/connect/racv/internet/primary/my+car/fuel/petrol+prices/search+for+petrol+prices+around+melbourne";

    private int mTab;
    private TextView topPrice;
    private TextView lowPrice;
    private TextView avePrice;
    private TextView lastFill;
    private TextView priceSource;
    private ListView list;
    private ImageView buyIndicator;

    private m.petrolpal.Tools.SummaryListAdapter adapter;
    private DatabaseHelper dbhelper;
    private static ArrayList<FuelStop> stops;
    private static final String FRAGMENT_UPDATE_FILTER = "fragmentupdater";



    public static SummaryFragment newInstance(int page){

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        SummaryFragment frag = new SummaryFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTab = getArguments().getInt(ARG_PAGE);

        dbhelper = new DatabaseHelper(getContext());
        stops = new ArrayList<>(dbhelper.getAllFuelStops().values());

        //updating the list...
       // getActivity().registerReceiver(new FragmentReceiver(), new IntentFilter(FRAGMENT_UPDATE_FILTER));

    }

    public class FragmentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        new DownloadParseHtmlTask().execute();

        View view = inflater.inflate(R.layout.fragment_tab_summary, null);

        //just for testing page nubmers
        //TextView t  = (TextView) view.findViewById(R.id.sumText);

        //t.setText(t.getText() + " " + getArguments().getInt(ARG_PAGE));

        topPrice = (TextView) view.findViewById(R.id.highDayPrice);
        //priceSource = (TextView) view.findViewById(R.id.priceSource);
        lowPrice = (TextView) view.findViewById(R.id.lowDayPrice);
        avePrice = (TextView) view.findViewById(R.id.aveDayPrice);
        lastFill = (TextView) view.findViewById(R.id.lastFillUp);
        list = (ListView) view.findViewById(R.id.summaryFuelList);
        buyIndicator = (ImageView) view.findViewById(R.id.trafficLight);

        topPrice.setVisibility(View.GONE);
        lowPrice.setVisibility(View.GONE);
        lastFill.setVisibility(View.GONE);


        if(stops.size() != 0){
            adapter = new SummaryListAdapter(getContext(), stops);
        }else{
            adapter = new SummaryListAdapter(getContext(), new ArrayList<FuelStop>());
        }

        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final FuelStop pendingDelete = stops.get(position);

                final View layout = getView().findViewById(R.id.summaryLayout);
                dbhelper.removeStop(stops.get(position));
                stops.remove(position);

                Snackbar snackbar = Snackbar
                        .make(layout, "Deleted Stop", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar snackbar1 = Snackbar.make(layout, "Fuel Stop Restored", Snackbar.LENGTH_SHORT);
                                dbhelper.addStop(pendingDelete);
                                stops.add(position, pendingDelete);
                                snackbar1.show();
                            }
                        })
                        .setCallback(new Snackbar.Callback(){
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);

                            }

                            @Override
                            public void onShown(Snackbar snackbar) {
                                super.onShown(snackbar);
                            }
                        });

                snackbar.show();





                return false;
            }
        });

        return view;


    }

    public void addDummyData(){
        //date d, double quantityBought, double overallCost, int odometer, Double latitude, Double longitude
        dbhelper.addStop(new FuelStop("11/11/1111", 111, 111, 111, 34.2, 55.55));
        dbhelper.addStop(new FuelStop("22/11/2222", 222, 222, 222, 22.2, 22.22));
        dbhelper.addStop(new FuelStop("22/11/3333", 333, 333, 333, 33.3, 22.22));
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();

    }

    public void refreshList(){
        if(adapter != null){
            adapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.notifyDataSetChanged();
    }

    //Params, Progress, Result
    private class DownloadParseHtmlTask extends AsyncTask<Void, Void, String[]> {

        //        Double highPrice;
//        Double avePrice;
//        Double lowPrice;
        String trendImageURL;
        String trafficLightURL;

        int TOP_PRICE_INDEX = 0;
        int LOW_PRICE_INDEX = 1;
        int AVE_PRICE_INDEX = 2;
        int PRICE_SOURCE_INDEX = 3;


        @Override
        protected String[] doInBackground(Void... params) {

            String[] prices = {"0","0","0",""};

            try {
                Log.d("Pre -  ", "Connecting to racv ");
                Document doc = Jsoup.connect(RACV_FUEL_URL).get();

                Log.d("Post -  ", "Connected to racv");

                // Get document (HTML page) title


                Element desc = doc.getElementById("todaysPrices");
                Elements pricesTable = doc.getElementsByClass("knowledge-webanswers_table__webanswers-table");
                Element d = doc.getElementById("price-trend");


                //note select "th" for top price and tr for low and ave price. It's how google formatted it
                prices[TOP_PRICE_INDEX] = pricesTable.select("tbody").select("tr").select("th").get(1).toString()
                        .replaceAll("\\<.*?\\>","").replaceAll("[^0-9.c]","");
                prices[LOW_PRICE_INDEX] = pricesTable.select("tbody").select("tr").get(1).select("td").get(1).toString()
                        .replaceAll("\\<.*?\\>","").replaceAll("[^0-9.c]","");
                prices[AVE_PRICE_INDEX] = pricesTable.select("tbody").select("tr").get(2).select("td").get(1).toString()
                        .replaceAll("\\<.*?\\>","").replaceAll("[^0-9.c]","");
                prices[PRICE_SOURCE_INDEX] = "RACV";



            }catch(IOException e){
                e.printStackTrace();
                prices[AVE_PRICE_INDEX] = "Failed to get prices"; //if loading failed
                //Toast.makeText(getContext(), "Check network or restart app", Toast.LENGTH_SHORT);
            }catch(RuntimeException e){
                //Toast.makeText(getContext(), "RACV unavailable - trying another option", Toast.LENGTH_SHORT);

                //backup address - adelaide fuel

                String backupURI = "http://www.mynrma.com.au/motoring-services/petrol-watch/fuel-prices.htm";
                try{

                    Document doc = Jsoup.connect(backupURI).get();



                    Element table = doc.getElementById("shopCon0");



                    //note select "th" for top price and tr for low and ave price. It's how google formatted it
                    prices[TOP_PRICE_INDEX] = table.select("tbody").select("tr").select("td").get(1).toString()
                            .replaceAll("\\<.*?\\>","").replaceAll("[^0-9.c]","");
                    prices[LOW_PRICE_INDEX] = table.select("tbody").select("tr").get(1).select("td").get(1).toString()
                            .replaceAll("\\<.*?\\>","").replaceAll("[^0-9.c]","");
                    prices[AVE_PRICE_INDEX] = table.select("tbody").select("tr").get(2).select("td").get(1).toString()
                            .replaceAll("\\<.*?\\>","").replaceAll("[^0-9.c]","");
                    prices[PRICE_SOURCE_INDEX] = "NRMA";

                }catch(IOException ex){
                    e.printStackTrace();
                    prices[AVE_PRICE_INDEX] = "Failed to get prices"; //if loading failed
                    //Toast.makeText(getContext(), "Check network or restart app", Toast.LENGTH_SHORT);
                    try{
                        backupURI = "http://www.raa.com.au/motoring-and-road-safety/fuel/metropolitan-fuel-prices";
                        Document doc = Jsoup.connect(backupURI).get();
                        Elements pricesTable = doc.getElementsByClass("price");



                        //note select "th" for top price and tr for low and ave price. It's how google formatted it
                        prices[TOP_PRICE_INDEX] = "PRICES: ";   //RAA doesn't have highest price

                        prices[LOW_PRICE_INDEX] = pricesTable.get(0).toString()
                                .replaceAll("\\<.*?\\>","").replaceAll("[^0-9.c]","");
                        prices[AVE_PRICE_INDEX] = pricesTable.get(1).toString()
                                .replaceAll("\\<.*?\\>","").replaceAll("[^0-9.c]","");
                        prices[PRICE_SOURCE_INDEX] = "RAA";


                    }catch(IOException exc){
                        e.printStackTrace();



                    }
                }





            }


            return prices;
        }

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected void onPostExecute(String[] prices) {



            final double LOW_THRESHOLD = 113.0;
            final double MED_THRESHOLD = 123.0;


            double avg = MED_THRESHOLD;


            try{

                avg = Integer.parseInt(prices[AVE_PRICE_INDEX]);
                if(Integer.parseInt(prices[LOW_PRICE_INDEX]) < 1){
                    topPrice.setVisibility(View.GONE);
                    lowPrice.setVisibility(View.GONE);
                }

            }catch(Exception e){
                e.printStackTrace();
            }

            if(avg <= LOW_THRESHOLD){
                buyIndicator.setImageResource(R.drawable.go);
            }else if (avg <= MED_THRESHOLD){
                buyIndicator.setImageResource(R.drawable.wait);
            }else{
                buyIndicator.setImageResource(R.drawable.stop);
            }


            topPrice.setText("Max: " + prices[TOP_PRICE_INDEX]);
            lowPrice.setText("Min: " + prices[LOW_PRICE_INDEX]);
            //priceSource.setText("Source: \n" + prices[PRICE_SOURCE_INDEX]);
            topPrice.setVisibility(View.VISIBLE);
            lowPrice.setVisibility(View.VISIBLE);
            //priceSource.setVisibility(View.VISIBLE);
            avePrice.setText("" + prices[AVE_PRICE_INDEX]);

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);

            ArrayList<FuelStop> f = new ArrayList<>(stops);
            //sort so we can get most recent date
            Collections.sort(f);


            Calendar stopDate = Calendar.getInstance();
            Calendar currentDate = Calendar.getInstance();
            if(f.size() > 0){
                stopDate.setTime(f.get(f.size() - 1).getDate());
                int daysBetween =  currentDate.get(Calendar.DAY_OF_YEAR) - stopDate.get(Calendar.DAY_OF_YEAR);
                lastFill.setText("Days since last fill: " + daysBetween);
                lastFill.setVisibility(View.VISIBLE);
            }







            //new DownloadImageTask().execute();
        }
    }




}
