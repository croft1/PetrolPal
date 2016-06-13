package m.petrolpal.Models;

import android.content.Context;

import m.petrolpal.R;

/**
 * Created by m on 6/13/2016.
 */
public class UserSettings {

    private static Context context;

    //default settings
    public UserSettings(Context c) {
        context = c;
        currencySymbol = context.getString(R.string.currency_dollar);
    }

    public static String getCurrencySymbol() {
        return currencySymbol;
    }

    public static void setCurrencySymbol(String currencySymbol) {
        UserSettings.currencySymbol = currencySymbol;
    }

    private static String currencySymbol;



    @Override
    public String toString() {
        return super.toString();
    }
}
