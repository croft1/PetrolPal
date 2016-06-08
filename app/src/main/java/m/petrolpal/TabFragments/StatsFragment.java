package m.petrolpal.TabFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import m.petrolpal.Models.FuelStop;
import m.petrolpal.R;
import m.petrolpal.Tools.DatabaseHelper;

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


    public static StatsFragment newInstance(int page){
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        StatsFragment frag = new StatsFragment();
        frag.setArguments(args);
        return frag;
    }

    public void updateView(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTab = getArguments().getInt(ARG_PAGE);


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
        domGraph.getGridLabelRenderer().setLabelFormatter(slf);

        avgPerStop = (TextView) view.findViewById(R.id.statAvgPerStop);
        totalSpent = (TextView) view.findViewById(R.id.statTotalSpend);
        quantityBought = (TextView) view.findViewById(R.id.statTotalBought);

        double total = 0;
        double quan = 0;
        for(int i = 0; i < f.size(); i++){
            total += f.get(i).getOverallCost();
            quan += f.get(i).getQuantityBought();
        }

        avgPerStop.setText("Average Spend: $" + (total/f.size()));
        totalSpent.setText("Total Spend: $" + total);
        quantityBought.setText("Total Bought: $" + quan);



        return view;
    }


}
