package croft.petrolpal.Tools;

import java.util.ArrayList;

import croft.petrolpal.Models.FuelStop;

/**
 * Created by m on 5/22/2016.
 */
public interface UpdateableFragment {
    public void update(ArrayList<FuelStop> updatedList);
}
