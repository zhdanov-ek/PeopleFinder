package com.example.gek.peoplefinder.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.helpers.Connection;
import com.example.gek.peoplefinder.helpers.Const;
import com.example.gek.peoplefinder.helpers.LogHelper;

import java.util.Date;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "F_SETTINGS";
    private SeekBar sbRate;
    private TextView tvStateRate;
    private SwitchCompat switchOldWariors;
    private LogHelper logHelper;
    private RadioButton rbNetwork, rbGps;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        logHelper = new LogHelper(getActivity().getBaseContext());

        rbGps = (RadioButton) rootView.findViewById(R.id.rbGps);
        rbGps.setOnClickListener(this);
        rbNetwork = (RadioButton) rootView.findViewById(R.id.rbNetwork);
        rbNetwork.setOnClickListener(this);
        if (Connection.getInstance().getLocationProvider() == Const.PROVIDER_GPS){
            rbGps.setChecked(true);
        } else {
            rbNetwork.setChecked(true);
        }

        tvStateRate = (TextView) rootView.findViewById(R.id.tvStateRate);
        sbRate = (SeekBar) rootView.findViewById(R.id.sbRate);
        switchOldWariors = (SwitchCompat) rootView.findViewById(R.id.switchOldWariors);
        switchOldWariors.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Connection.getInstance().setShowOldPersons(isChecked);
            }
        });
        sbRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int frequency = seekBar.getProgress() * Const.BASE_STEP_FREQUENCY + Const.BASE_STEP_FREQUENCY;
                updateLabelFrequency(frequency);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int frequency = seekBar.getProgress() * Const.BASE_STEP_FREQUENCY + Const.BASE_STEP_FREQUENCY;
                Connection.getInstance().setFrequencyLocationUpdate(frequency);
                logHelper.writeLog("Set delay to " + frequency, new Date());
            }
        });
        updateLabelFrequency(Connection.getInstance().getFrequencyLocationUpdate());
        sbRate.setProgress(Connection.getInstance().getFrequencyLocationUpdate()/Const.BASE_STEP_FREQUENCY - 1);
        switchOldWariors.setChecked(Connection.getInstance().isShowOldPersons());

        return rootView;
    }

    private void updateLabelFrequency(int frequency){
        tvStateRate.setText(String.format(getResources().
                getString(R.string.settings_rate_location), Integer.toString(frequency)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rbGps:
                Connection.getInstance().setLocationProvider(Const.PROVIDER_GPS);
                break;
            case R.id.rbNetwork:
                Connection.getInstance().setLocationProvider(Const.PROVIDER_NETWORK);
                break;
        }
    }
}
