package com.example.gek.peoplefinder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.example.gek.peoplefinder.enums.StateMenu;
import com.example.gek.peoplefinder.helpers.Connection;
import com.example.gek.peoplefinder.helpers.Const;
import com.example.gek.peoplefinder.helpers.LogHelper;
import com.example.gek.peoplefinder.helpers.SettingsHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SettingsFragment extends BaseFragment {

    public static final String TAG = "F_SETTINGS";

    @BindView(R.id.rbGps) protected RadioButton rbGps;
    @BindView(R.id.rbNetwork) protected RadioButton rbNetwork;
    @BindView(R.id.tvStateRate) protected TextView tvStateRate;
    @BindView(R.id.sbRate) protected SeekBar sbRate;
    @BindView(R.id.switchOldPerson) protected SwitchCompat switchOldPerson;
    @BindView(R.id.switchServiceEnable) protected SwitchCompat switchServiceEnable;
    @BindView(R.id.switchCompass) protected SwitchCompat switchCompass;
    @BindView(R.id.switchMyLocationButton) protected SwitchCompat switchMyLocationButton;
    @BindView(R.id.switchRotateGestures) protected SwitchCompat switchRotateGestures;
    @BindView(R.id.switchTiltGestures) protected SwitchCompat switchTiltGestures;
    @BindView(R.id.switchZoomButtons) protected SwitchCompat switchZoomButton;

    private LogHelper logHelper;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        unbinder = ButterKnife.bind(this, rootView);

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

        // Show last settings
        updateLabelFrequency(Connection.getInstance().getFrequencyLocationUpdate());
        sbRate.setProgress((Connection.getInstance().getFrequencyLocationUpdate()/1000)/Const.BASE_STEP_FREQUENCY - 1);
        switchOldPerson.setChecked(Connection.getInstance().isShowOldPersons());
        switchServiceEnable.setChecked(Connection.getInstance().isServiceRunning());
        switchCompass.setChecked(SettingsHelper.isCompassEnabled());
        switchMyLocationButton.setChecked(SettingsHelper.isMyLocationButtonEnabled());
        switchRotateGestures.setChecked(SettingsHelper.isRotateGesturesEnabled());
        switchTiltGestures.setChecked(SettingsHelper.isTiltGesturesEnabled());
        switchZoomButton.setChecked(SettingsHelper.isZoomButtonsEnabled());

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

        setToolbarTitle(getString(R.string.title_settings));
        mCallbackDrawerMenuStateChanger.setMenuState(StateMenu.SETTINGS);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.rbGps) protected void onClickGps(){
        Connection.getInstance().setLocationProvider(Const.PROVIDER_GPS);
    }

    @OnClick(R.id.rbNetwork) protected void onClickNetwork(){
        Connection.getInstance().setLocationProvider(Const.PROVIDER_NETWORK);
    }

    @OnClick(R.id.switchZoomButtons) protected void onClickZoomButtons(){
        SettingsHelper.setZoomButtonsEnabled(switchZoomButton.isChecked());
    }

    @OnClick(R.id.switchTiltGestures) protected void onClickTiltGestures(){
        SettingsHelper.setTiltGesturesEnabled(switchTiltGestures.isChecked());
    }

    @OnClick(R.id.switchRotateGestures) protected void onClickRotateGestures(){
        SettingsHelper.setRotateGesturesEnabled(switchRotateGestures.isChecked());
    }

    @OnClick(R.id.switchCompass) protected void onClickCompass(){
        SettingsHelper.setCompassEnabled(switchCompass.isChecked());
    }

    @OnClick(R.id.switchMyLocationButton) protected void onClickMyLocationButton(){
        SettingsHelper.setMyLocationButtonEnabled(switchMyLocationButton.isChecked());
    }

    private void updateLabelFrequency(int frequency){
        tvStateRate.setText(String.format(getResources().
                getString(R.string.settings_rate_location), Integer.toString(frequency)));
    }
}
