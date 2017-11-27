package com.example.gek.peoplefinder.fragments;

import android.content.Intent;
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
import android.widget.Toast;

import com.example.gek.peoplefinder.LocationService;
import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.helpers.Connection;
import com.example.gek.peoplefinder.helpers.Const;
import com.example.gek.peoplefinder.helpers.LogHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends Fragment {

    public static final String TAG = "F_SETTINGS";

    @BindView(R.id.rbGps) protected RadioButton rbGps;
    @BindView(R.id.rbNetwork) protected RadioButton rbNetwork;
    @BindView(R.id.tvStateRate) protected TextView tvStateRate;
    @BindView(R.id.sbRate) protected SeekBar sbRate;
    @BindView(R.id.switchOldPerson) protected SwitchCompat switchOldPerson;
    @BindView(R.id.switchServiceEnable) protected SwitchCompat switchServiceEnable;

    private LogHelper logHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, rootView);

        logHelper = new LogHelper(getActivity().getBaseContext());

        if (Connection.getInstance().getLocationProvider() == Const.PROVIDER_GPS){
            rbGps.setChecked(true);
        } else {
            rbNetwork.setChecked(true);
        }

        switchOldPerson.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Connection.getInstance().setShowOldPersons(isChecked);
            }
        });
        switchServiceEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (!Connection.getInstance().isServiceRunning()){
                        getActivity().startService(new Intent(getActivity(), LocationService.class));
                        Toast.makeText(getActivity(), "Service started", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    getActivity().stopService(new Intent(getActivity(), LocationService.class));
                    Connection.getInstance().setServiceRunning(false);
                    Toast.makeText(getActivity(), "Service stopped", Toast.LENGTH_SHORT).show();
                }
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
                Connection.getInstance().setFrequencyLocationUpdate(frequency * 1000);
                logHelper.writeLog("Set delay to " + frequency + " seconds");
            }
        });
        updateLabelFrequency(Connection.getInstance().getFrequencyLocationUpdate());
        sbRate.setProgress((Connection.getInstance().getFrequencyLocationUpdate()/1000)/Const.BASE_STEP_FREQUENCY - 1);
        switchOldPerson.setChecked(Connection.getInstance().isShowOldPersons());

        switchServiceEnable.setChecked(Connection.getInstance().isServiceRunning());

        return rootView;
    }


    @OnClick(R.id.rbGps) protected void onClickGps(){
        Connection.getInstance().setLocationProvider(Const.PROVIDER_GPS);
    }

    @OnClick(R.id.rbNetwork) protected void onClickNetwork(){
        Connection.getInstance().setLocationProvider(Const.PROVIDER_NETWORK);
    }

    private void updateLabelFrequency(int frequency){
        tvStateRate.setText(String.format(getResources().
                getString(R.string.settings_rate_location), Integer.toString(frequency)));
    }
}
