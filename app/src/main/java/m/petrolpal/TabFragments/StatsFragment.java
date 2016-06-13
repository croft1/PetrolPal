package m.petrolpal.TabFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;

import m.petrolpal.Models.FuelStop;
import m.petrolpal.R;
import m.petrolpal.Tools.DatabaseHelper;
import m.petrolpal.Models.UserSettings;

/**
 * Created by Michaels on 3/5/2016.
 */
public class StatsFragment extends  android.support.v4.app.Fragment {


    public static final String ARG_PAGE = "ARG_PAGE";       //specific argument to get tab number
    private int mTab;


    private DatabaseHelper dbhelper;
    private TextView totalSpent;
    private TextView avgPerStop;
    private TextView quantityBought;
    private UserSettings userSettings;

    public static StatsFragment newInstance(int page){
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        StatsFragment frag = new StatsFragment();
        frag.setArguments(args);
        return frag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTab = getArguments().getInt(ARG_PAGE);

        userSettings = new UserSettings(getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_tab_stats, null);
        TextView t  = (TextView) view.findViewById(R.id.statText);
        t.setText("Fragment #" + getArguments().getInt(ARG_PAGE));

        dbhelper = new DatabaseHelper(getContext());
        ArrayList<FuelStop> f = new ArrayList<>(dbhelper.getAllFuelStops().values());



        //12 for each month

        //MONTH FREQUENCY
        final int TOTAL_MONTHS = 11;

        GraphView monthFrequencyGraph = (GraphView) view.findViewById(R.id.monthNumberGraph);

        int frequency[] = {0,0,0,0,0,0,0,0,0,0,0,0};

        for(int i = 0; i<f.size(); i++){
            Calendar cal = Calendar.getInstance();
            cal.setTime(f.get(i).getDate());
            frequency[cal.get(Calendar.MONTH)] = frequency[i]++;

        }

        DataPoint[] points = new DataPoint[TOTAL_MONTHS];
        for(int i = 0; i < TOTAL_MONTHS; i++){
            points[i] = new DataPoint(i, frequency[i]);
        }

        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(points);
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);    //http://www.android-graphview.org/documentation/category/bar-graph
            }
        });
        series.setDrawValuesOnTop(true);

        monthFrequencyGraph.setHorizontalScrollBarEnabled(true);
        monthFrequencyGraph.getViewport().setXAxisBoundsManual(true);
        monthFrequencyGraph.getViewport().setMinX(0);
        monthFrequencyGraph.getViewport().setMaxX(12);

        monthFrequencyGraph.addSeries(series);
        monthFrequencyGraph.setTitle("Stop Frequency Per Month");
        monthFrequencyGraph.getGridLabelRenderer().setHorizontalAxisTitle("Month");
        monthFrequencyGraph.getGridLabelRenderer().setVerticalAxisTitle("Frequency");
        monthFrequencyGraph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.GRAY);
        monthFrequencyGraph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.GRAY);
        monthFrequencyGraph.setTitleColor(Color.WHITE);
        monthFrequencyGraph.getGridLabelRenderer().setGridColor(Color.TRANSPARENT);

        StaticLabelsFormatter slf = new StaticLabelsFormatter(monthFrequencyGraph);
        // slf.setHorizontalLabels(new String[]{});
        // monthFrequencyGraph.getGridLabelRenderer().setLabelFormatter(slf);



        //DAY OF WK FREQUENCY  ################################################
        final int TOTAL_DAYS = 6;

        GraphView dayFrequencyGraph = (GraphView) view.findViewById(R.id.dayNumberGraph);
        dayFrequencyGraph.getViewport().setXAxisBoundsManual(true);
        dayFrequencyGraph.getViewport().setMinX(0);
        dayFrequencyGraph.getViewport().setMaxX(7);


        for(int i = 0; i<f.size(); i++){
            Calendar cal = Calendar.getInstance();
            cal.setTime(f.get(i).getDate());
            frequency[cal.get(Calendar.DAY_OF_WEEK)] = frequency[i]++;

        }

        points = new DataPoint[TOTAL_DAYS];
        for(int i = 0; i < TOTAL_DAYS; i++){
            points[i] = new DataPoint(i, frequency[i]);
        }

        series = new BarGraphSeries<DataPoint>(points);
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/50, (int) Math.abs(data.getY()*255/70), 100);    //http://www.android-graphview.org/documentation/category/bar-graph
            }
        });
        series.setDrawValuesOnTop(true);

        dayFrequencyGraph.setHorizontalScrollBarEnabled(true);
        //todo set hard size of the graphs
        dayFrequencyGraph.addSeries(series);
        dayFrequencyGraph.setTitle("Stop Frequency By Day");
        dayFrequencyGraph.getGridLabelRenderer().setHorizontalAxisTitle("Day Of Week");
        dayFrequencyGraph.getGridLabelRenderer().setVerticalAxisTitle("Frequency");
        dayFrequencyGraph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.GRAY);
        dayFrequencyGraph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.GRAY);
        dayFrequencyGraph.setTitleColor(Color.DKGRAY);
        dayFrequencyGraph.getGridLabelRenderer().setGridColor(Color.TRANSPARENT);

        slf = new StaticLabelsFormatter(dayFrequencyGraph);
        slf.setHorizontalLabels(new String[]{"M", "Tu", "W", "Th", "F", "Sa", "Su"});
        dayFrequencyGraph.getGridLabelRenderer().setLabelFormatter(slf);


        //DAY OF MONTH FREQUENCY ###########################################
        int MAX_DAYS_OF_MONTH = 30;

        ArrayList<Integer> freq = new ArrayList<>();
        if(freq.size() < 1){
            for(int i = 0; i<MAX_DAYS_OF_MONTH; i++) {
                freq.add(0);
            }
        }


        GraphView domGraph = (GraphView) view.findViewById(R.id.dayOfMonGraph);
        domGraph.getViewport().setXAxisBoundsManual(true);
        domGraph.getViewport().setMinX(0);
        domGraph.getViewport().setMaxX(31);


        for(int i = 0; i<f.size(); i++){

            Calendar cal = Calendar.getInstance();
            cal.setTime(f.get(i).getDate());
            int day = cal.get(Calendar.DAY_OF_MONTH);

            freq.set(day, i++);

        }


        ArrayList<DataPoint> dPoints = new ArrayList<DataPoint>();
        for(int i = 0; i < MAX_DAYS_OF_MONTH; i++){
            dPoints.add(new DataPoint(i, freq.get(i)));

        }

        series = new BarGraphSeries<DataPoint>(points);
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/50, (int) Math.abs(data.getY()*255/70), 100);    //http://www.android-graphview.org/documentation/category/bar-graph
            }
        });
        series.setDrawValuesOnTop(true);

        domGraph.setHorizontalScrollBarEnabled(true);
        //todo set hard size of the graphs
        domGraph.addSeries(series);
        domGraph.setTitle("Frequency of days in month");
        domGraph.getGridLabelRenderer().setHorizontalAxisTitle("Day Of Month");
        domGraph.getGridLabelRenderer().setVerticalAxisTitle("Frequency");
        domGraph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.GRAY);
        domGraph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.GRAY);
        domGraph.setTitleColor(Color.DKGRAY);
        domGraph.getGridLabelRenderer().setGridColor(Color.TRANSPARENT);

        slf = new StaticLabelsFormatter(domGraph);
        slf.setHorizontalLabels(new String[]{"1", "15", "30"});
        slf.setVerticalLabels(new String[]{"1", Integer.toString(freq.size()/ 2), Integer.toString(freq.size() + 1)});
        domGraph.getGridLabelRenderer().setLabelFormatter(slf);

        //###################Price Graph

        GraphView priceGraph = (GraphView) view.findViewById(R.id.priceGraph);
        priceGraph.getViewport().setXAxisBoundsManual(true);
        priceGraph.getViewport().setMinX(0);
        priceGraph.getViewport().setMaxX(2);


        //get price values in data points
        PointsGraphSeries<DataPoint> pointSeries = new PointsGraphSeries<>();
        for(int i = 0; i < f.size(); i++){
            pointSeries.appendData(new DataPoint(i, f.get(i).getCostPerLiter()), true, f.size());

        }

        priceGraph.addSeries(pointSeries);
        pointSeries.setShape(PointsGraphSeries.Shape.POINT);
        pointSeries.setColor(getResources().getColor(R.color.colorAccent));

        priceGraph.setHorizontalScrollBarEnabled(true);

        //todo set hard size of the graphs
        priceGraph.addSeries(series);
        priceGraph.setTitle("Purchased Fuel Price");
        priceGraph.getGridLabelRenderer().setHorizontalAxisTitle("Stop");
        priceGraph.getGridLabelRenderer().setVerticalAxisTitle("Price");
        priceGraph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.GRAY);
        priceGraph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.GRAY);
        priceGraph.setTitleColor(Color.DKGRAY);


        slf = new StaticLabelsFormatter(priceGraph);
        slf.setHorizontalLabels(new String[]{"1", Integer.toString(f.size())});
        slf.setVerticalLabels(new String[]{"1", Integer.toString(f.size() / 2), Integer.toString(f.size())});
        priceGraph.getGridLabelRenderer().setLabelFormatter(slf);




        //BOTTOM ANALYTICAL TEXT

        avgPerStop = (TextView) view.findViewById(R.id.statAvgPerStop);
        totalSpent = (TextView) view.findViewById(R.id.statTotalSpend);
        quantityBought = (TextView) view.findViewById(R.id.statTotalBought);

        double total = 0;
        double quan = 0;
        for(int i = 0; i < f.size(); i++){
            total += f.get(i).getOverallCost();
            quan += f.get(i).getQuantityBought();
        }
        total = Math.round(total * 100.0) / 100.0;
        quan = Math.round(quan * 100.0) / 100.0;


        avgPerStop.setText("Average Spend: " + userSettings.getCurrencySymbol() + Math.round((total/f.size()) * 100.0) / 100.0);
        totalSpent.setText("Total Spend: " + userSettings.getCurrencySymbol() + total);
        quantityBought.setText("Total Bought: " + userSettings.getCurrencySymbol() + quan);




        domGraph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        dayFrequencyGraph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        monthFrequencyGraph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        priceGraph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);



        return view;
    }


}
