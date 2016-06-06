package m.petrolpal.TabFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

import m.petrolpal.Models.FuelStop;
import m.petrolpal.R;
import m.petrolpal.Tools.DatabaseHelper;
import m.petrolpal.Tools.ExpandableListAdapter;
import m.petrolpal.Tools.UpdateableFragment;

/**
 * Created by Michaels on 3/5/2016.
 */


public class ListFragment extends android.support.v4.app.Fragment implements UpdateableFragment {
    public static final String ARG_PAGE = "ARG_PAGE";       //argument to get page number
    private int mTab;

    private DatabaseHelper dbhelper;
    private ExpandableListAdapter adapter;
    private ExpandableListView exList;
    private ArrayList<FuelStop> fuelStops;


    public static ListFragment newInstance(int page){
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ListFragment frag = new ListFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void update(ArrayList<FuelStop> updatedList) {       //from custom interface
        fuelStops = updatedList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTab = getArguments().getInt(ARG_PAGE);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab_list, null);

        // TextView t  = (TextView) view.findViewById(R.id.listText);
        // t.setText("Fragment #" + getArguments().getInt(ARG_PAGE));

        exList = (ExpandableListView) view.findViewById(R.id.stopsExpandableList);

        dbhelper = new DatabaseHelper(getContext());
        if(dbhelper.getAllFuelStops().size() != 0){
            adapter = new ExpandableListAdapter(getContext(), new ArrayList<>(dbhelper.getAllFuelStops().values()));
        }else{
            adapter = new ExpandableListAdapter(getContext(), new ArrayList<FuelStop>());
        }

        exList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        exList = (ExpandableListView) getView().findViewById(R.id.stopsExpandableList);
        adapter = new ExpandableListAdapter(getActivity(), fuelStops);

    }



}

