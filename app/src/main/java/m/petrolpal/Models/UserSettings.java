package m.petrolpal.Models;

import android.content.Context;

import m.petrolpal.R;

/**
 * Created by m on 6/13/2016.
 */
public class UserSettings {


    private static UserSettings instance;

    //default settings
    private UserSettings() {
        currencySymbol = "$";
    }

    //singleton to ensure consistency throughout
    public static UserSettings getInstance(){
        if(instance == null){
            instance = new UserSettings();
        }
        return instance;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }


    //changes symbol of currency when dollar amounts are shown
    public static void setCurrencySymbol(String currencySymbol) {
        UserSettings.currencySymbol = currencySymbol;
    }

    private static String currencySymbol;



    @Override
    public String toString() {
        return super.toString();
    }
}
