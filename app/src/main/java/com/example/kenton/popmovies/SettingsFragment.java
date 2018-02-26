package com.example.kenton.popmovies;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {

    private final static String TAG = SettingsFragment.class.getSimpleName();

    SharedPreferences.OnSharedPreferenceChangeListener mPrefListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                Preference preference = findPreference(key);
                if (preference instanceof ListPreference) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntry());
                }
            }
        };
        initializePreferenceSummary(getString(R.string.pref_key_sort));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(mPrefListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(mPrefListener);
    }

    private void initializePreferenceSummary(String key) {
        mPrefListener.onSharedPreferenceChanged(
                getPreferenceScreen().getSharedPreferences(),
                key);
    }
}
