package m.petrolpal;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import m.petrolpal.Models.UserSettings;
import m.petrolpal.R;

import java.util.List;


public class SettingsActivity extends AppCompatActivity {

    UserSettings userSettings;
    RadioGroup currencyGroup;
    RadioButton checkedCurrency;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupActionBar();

        userSettings = UserSettings.getInstance();
        currencyGroup = (RadioGroup) findViewById(R.id.currencyRadioGroup);

        //compare currency symbols and set the initial one
        if (0 == userSettings.getCurrencySymbol().compareTo(getApplicationContext().getString(R.string.currency_euro))) {
            checkedCurrency = (RadioButton) findViewById(R.id.euroRadio);
            checkedCurrency.setChecked(true);
        } else if (0 == userSettings.getCurrencySymbol().compareTo(getApplicationContext().getString(R.string.currency_pound))) {
            checkedCurrency = (RadioButton) findViewById(R.id.poundRadio);
            checkedCurrency.setChecked(true);
        } else {
            checkedCurrency = (RadioButton) findViewById(R.id.dollarRadio);
            checkedCurrency.setChecked(true);
        }


        currencyGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkedCurrency = (RadioButton) group.findViewById(checkedId);

                if (checkedId == R.id.euroRadio) {
                    userSettings.setCurrencySymbol(getString(R.string.currency_euro));
                } else if (checkedId == R.id.poundRadio) {
                    userSettings.setCurrencySymbol(getString(R.string.currency_pound));
                } else {
                    userSettings.setCurrencySymbol(getString(R.string.currency_dollar));
                }

            }
        });


    }


    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
    }
}


