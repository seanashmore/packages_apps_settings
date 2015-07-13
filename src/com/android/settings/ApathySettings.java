package com.android.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;
import android.provider.Settings;

/**
 * Created by Sean Ashmore on 12/07/15.
 */
public class ApathySettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "ApathySettings";

    private final String KEY_BATTERY_PERCENTAGE = "show_battery_percentage";

    private SwitchPreference mBatteryPercentagePreference;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.apathy_settings);

        mBatteryPercentagePreference = findAndInitSwitchPref(KEY_BATTERY_PERCENTAGE);

        if(mBatteryPercentagePreference != null){
            Log.i(TAG,"Found battery percentage switch preference OK");
            mBatteryPercentagePreference.setOnPreferenceChangeListener(this);
        }else{
            Log.i(TAG,"Something went wrong. Couldn't find battery percentage switch preference");
        }

        updateAllOptions();


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"Calling updateAllOptions from onResume");
        updateAllOptions();
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (Utils.isMonkeyRunning()) {
            return false;
        }

        if(preference == mBatteryPercentagePreference){
            if(mBatteryPercentagePreference != null) {
                Log.i(TAG, "batteryPercentagePreference clicked");
                Settings.System.putInt(getContentResolver(),
                        Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENTAGE,
                        mBatteryPercentagePreference.isChecked() ? 1 : 0);
                Log.i(TAG, "Set batteryPercentagePreference to : " + mBatteryPercentagePreference.isChecked());
            }
        }else{
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        return false;
    }

    private SwitchPreference findAndInitSwitchPref(String key) {
        SwitchPreference pref = (SwitchPreference) findPreference(key);
        if (pref == null) {
            //throw new IllegalArgumentException("Cannot find preference with key = " + key);
            Log.i(TAG,"Cannot find preference with key = " +key);
        }
        return pref;
    }

    private void updateAllOptions(){
        Log.i(TAG, "updateAllOptions()");
        if(mBatteryPercentagePreference != null) {
            Log.i(TAG,"batteryPercentagePreference is not null. Updating switch preference");
            updateSwitchPreference(mBatteryPercentagePreference,
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENTAGE , 0) != 0);
        }
    }

    void updateSwitchPreference(SwitchPreference switchPreference, boolean value) {
        Log.i(TAG,"Updating switchPreference to: " +value);
        switchPreference.setChecked(value);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();

        if(key == KEY_BATTERY_PERCENTAGE){
            try{
                int value = Integer.parseInt((String) objValue);
                Settings.System.putInt(getContentResolver(),
                        Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENTAGE, value);
            }catch(NumberFormatException e){
                Log.e(TAG,"Could not persist battery percentage display setting");
            }
            return true;
        }
        return true;
    }
}