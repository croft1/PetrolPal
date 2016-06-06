package m.petrolpal.TabFragments;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import m.petrolpal.Models.*;

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
    private ListView list;
    private m.petrolpal.Tools.SummaryListAdapter adapter;
    private DatabaseHelper dbhelper;



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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        new DownloadParseHtmlTask().execute();

        View view = inflater.inflate(R.layout.fragment_tab_summary, null);

        //just for testing page nubmers
        TextView t  = (TextView) view.findViewById(R.id.sumText);

        t.setText(t.getText() + " " + getArguments().getInt(ARG_PAGE));

        topPrice = (TextView) view.findViewById(R.id.topDayPrice);
        lowPrice = (TextView) view.findViewById(R.id.lowDayPrice);
        avePrice = (TextView) view.findViewById(R.id.aveDayPrice);
        list = (ListView) view.findViewById(R.id.summaryFuelList);


        dbhelper = new DatabaseHelper(getContext());


        if(dbhelper.getAllFuelStops().size() != 0){
            adapter = new SummaryListAdapter(getContext(), new ArrayList<>(dbhelper.getAllFuelStops().values()));
        }else{
            adapter = new SummaryListAdapter(getContext(), new ArrayList<FuelStop>());
        }

        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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


        @Override
        protected String[] doInBackground(Void... params) {

            String[] prices = {"0","0","0"};

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





/*

                //   SOURCE FORMAT
                // #132 comic name 2016-04-29
                //1 is the second value, as # is the 0th value
                comicId =  Integer.parseInt(origHeading.substring(1, origHeading.indexOf(" ",1)));
                if(currentComicId == 0){
                    Comic.setMaxComicId(comicId);       //opens on front page with most recent comic. if its not set, we set max
                }
                currentComicId = comicId;

                comicDate = origHeading.substring(origHeading.length() - SIZE_OF_DATE, origHeading.length());
                //from first space to the position before the date begins
                comicName = origHeading.substring(origHeading.indexOf(" ", 1), origHeading.length() - SIZE_OF_DATE);

                comicDescription = desc.select("p").text();
                comicDescription.replaceAll("<br>","\n");       //seem to return removed already
                comicDescription.replaceAll("<p>","");

                comicSrc = doc.select("img").first().absUrl("src");

                desc = doc.getElementById("descDiv2");

                comicTranscript = desc.select("p").text();
                comicTranscript.replaceAll("<br>","\n");

                currentComic = new Comic(comicId,comicName,comicSrc,comicDescription,comicTranscript,comicDate);



                */


            }catch(IOException e){
                e.printStackTrace();
                prices[AVE_PRICE_INDEX] = "Failed to get prices"; //if loading failed
                //Toast.makeText(getContext(), "Check network or restart app", Toast.LENGTH_SHORT);
            }catch(RuntimeException e){
                //Toast.makeText(getContext(), "RACV unavailable - trying another option", Toast.LENGTH_SHORT);

                //backup address - adelaide fuel
                String backupURI = "http://www.raa.com.au/motoring-and-road-safety/fuel/metropolitan-fuel-prices";
                try{

                    Document doc = Jsoup.connect(backupURI).get();
                    Elements pricesTable = doc.getElementsByClass("price");



                    //note select "th" for top price and tr for low and ave price. It's how google formatted it
                    prices[TOP_PRICE_INDEX] = "PRICES: ";   //RAA doesn't have highest price

                    prices[LOW_PRICE_INDEX] = pricesTable.get(0).toString()
                            .replaceAll("\\<.*?\\>","").replaceAll("[^0-9.c]","");
                    prices[AVE_PRICE_INDEX] = pricesTable.get(1).toString()
                            .replaceAll("\\<.*?\\>","").replaceAll("[^0-9.c]","");

                }catch(IOException exc){
                    e.printStackTrace();
                    prices[AVE_PRICE_INDEX] = "Failed to get prices"; //if loading failed
                    //Toast.makeText(getContext(), "Check network or restart app", Toast.LENGTH_SHORT);
                }




            }


            return prices;
        }

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected void onPostExecute(String[] prices) {

            topPrice.setText(prices[TOP_PRICE_INDEX]);
            lowPrice.setText(prices[LOW_PRICE_INDEX]);
            avePrice.setText(prices[AVE_PRICE_INDEX]);

            //new DownloadImageTask().execute();
        }
    }


/*


    //Params, Progress, Result
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {



        protected Bitmap doInBackground(String... urls) {

            comicView = (ImageView) findViewById(R.id.comicView);

            Bitmap image = null;
            try {
                InputStream in = new java.net.URL(currentComic.getImageUrl()).openStream();
                image = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("ERROR getting image", e.getMessage());
                Toast.makeText(getApplicationContext(), "Error getting image", Toast.LENGTH_SHORT);
                e.printStackTrace();
            }
            return image;
        }

        protected void onPostExecute(Bitmap result) {

            comicView.setImageBitmap(result);
            headingView.setText(currentComic.getName());
            descriptionView.setText(currentComic.getDesc());
            transcriptView.setText(currentComic.getTranscript());

            dateView.setText(currentComic.getDate().toString());

            setTitle("RED PANELS #" + currentComic.getId());


            nextB.setEnabled(true);
            prevB.setEnabled(true);

            setVisibilityOfUi(true);




*/



}
