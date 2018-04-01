package com.example.gek.peoplefinder.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.enums.StateMenu;
import com.example.gek.peoplefinder.helpers.Connection;
import com.example.gek.peoplefinder.helpers.Const;
import com.example.gek.peoplefinder.helpers.Db;
import com.example.gek.peoplefinder.helpers.Utils;
import com.example.gek.peoplefinder.models.Mark;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class MarkFragment extends BaseFragment {
    private Unbinder unbinder;

    @BindView(R.id.rbManualLocation) protected RadioButton rbManualLocation;
    @BindView(R.id.etMarkName)  protected EditText etMarkName;
    @BindView(R.id.etLat)  protected EditText etLat;
    @BindView(R.id.etLng)  protected EditText etLng;
    @BindView(R.id.tilName) protected TextInputLayout tilName;
    @BindView(R.id.tilLat) protected TextInputLayout tilLat;
    @BindView(R.id.tilLng) protected TextInputLayout tilLng;
    @BindView(R.id.btnAddMark) protected Button btnAddMark;
    @BindView(R.id.btnRemoveMark) protected Button btnRemoveMark;
    @BindString(R.string.error_wrong_data) protected String wrongData;
    @BindString(R.string.error_name_already_exists) protected String nameAlreadyExists;

    private Mark mOpenedMark;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mark, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        extractArguments();
    }

    @Override
    public void onStart() {
        super.onStart();
        setToolbarTitle(getString(R.string.title_edit_mark));
        mCallbackDrawerMenuStateChanger.setMenuState(StateMenu.MARK);
    }

    @OnClick({R.id.rbCurrentLocation, R.id.rbManualLocation}) protected void clickChangeTypeMark(View view){
        resetErrorHints();
        if (view.getId() == R.id.rbCurrentLocation){
            etMarkName.requestFocus();
            etLat.setEnabled(false);
            etLng.setEnabled(false);
            if (Connection.getInstance().isServiceRunning() && Connection.getInstance().getLastLocation() != null){
                LatLng lastLocation = Connection.getInstance().getLastLocation();
                etLat.setText(String.valueOf(lastLocation.latitude));
                etLng.setText(String.valueOf(lastLocation.longitude));
            } else {
                Toast.makeText(getActivity(), "Service doesn't running", Toast.LENGTH_SHORT).show();
                etLat.setText("");
                etLng.setText("");
            }
        } else {
            etLat.setText("");
            etLng.setText("");
            etLat.setEnabled(true);
            etLat.requestFocus();
            etLng.setEnabled(true);
        }
    }

    @OnClick(R.id.btnAddMark) protected void clickAddMark(){
        if ( validateLatLng()
                && validateName()
                && !isNameExist(etMarkName.getText().toString())){
            saveMark();
        }
    }

    @OnClick(R.id.btnRemoveMark) protected void clickRemoveMark(){
        if (mOpenedMark != null){
            Db.removeMark(mOpenedMark.getId());
            Toast.makeText(getContext(), R.string.mark_removed, Toast.LENGTH_SHORT).show();
            mFragmentChanger.showMapFragment();
        }
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void saveMark(){
        if (mOpenedMark != null){
            Db.removeMark(mOpenedMark.getId());
        }

        Db.addMark(etMarkName.getText().toString(), null, Double.parseDouble(etLat.getText().toString()),
                Double.parseDouble(etLng.getText().toString()), false);

        etMarkName.requestFocus();
        rbManualLocation.setChecked(true);
        mFragmentChanger.hideKeyboard();
        Toast.makeText(getContext(), R.string.mark_saved, Toast.LENGTH_SHORT).show();
        mFragmentChanger.showMapFragment();
    }

    private boolean validateLatLng(){
        resetErrorHints();
        boolean isValid = true;
        String lat = etLat.getText().toString();
        if ((lat.length() == 0) || ( !Utils.validateLat(lat) )){
            tilLat.setError(wrongData);
            etLat.requestFocus();
            isValid = false;
        }

        String lng = etLng.getText().toString();
        if ((lng.length() == 0) || ( !Utils.validateLng(lng)) ){
            tilLng.setError(wrongData);
            etLng.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private boolean validateName(){
        if (etMarkName.getText().toString().replaceAll(" ", "").length() == 0){
            tilName.setError(wrongData);
            etMarkName.requestFocus();
            return false;
        } else {
            tilName.setError(null);
            return true;
        }
    }

    private boolean isNameExist(String name){
        if (Db.findMark(name) == null){
            return false;
        } else {
            tilName.setError(nameAlreadyExists);
            etMarkName.requestFocus();
            return true;
        }
    }

    private void resetErrorHints(){
        tilName.setError(null);
        tilLat.setError(null);
        tilLng.setError(null);
    }

    @SuppressLint("SetTextI18n")
    private void extractArguments() {
        Bundle args = getArguments();
        if (args != null) {
            double latitude = args.getDouble(Const.ARG_LATITUDE, -1);
            double longitude = args.getDouble(Const.ARG_LONGITUDE, -1);
            if (latitude != -1 && longitude != -1) {
                etLat.setText(Double.toString(latitude));
                etLng.setText(Double.toString(longitude));
            }
            mOpenedMark = args.getParcelable(Const.ARG_MARK);
            if (mOpenedMark != null){
                etMarkName.setText(mOpenedMark.getName());
                etLat.setText(Double.toString(mOpenedMark.getLatitude()));
                etLng.setText(Double.toString(mOpenedMark.getLongitude()));
                btnAddMark.setText(R.string.save);
                btnRemoveMark.setVisibility(View.VISIBLE);
            }
        }
    }
}
