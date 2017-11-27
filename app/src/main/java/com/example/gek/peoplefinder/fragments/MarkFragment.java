package com.example.gek.peoplefinder.fragments;


import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.helpers.Connection;
import com.example.gek.peoplefinder.helpers.Utils;
import com.example.gek.peoplefinder.models.Mark;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


public class MarkFragment extends Fragment {
    private static final String TAG = "F_MARK";

    private String incorrectValue = "Incorrect value";
    private Unbinder unbinder;

    @BindView(R.id.etMarkName)  protected EditText etMarkName;
    @BindView(R.id.etLat)  protected EditText etLat;
    @BindView(R.id.etLng)  protected EditText etLng;
    @BindView(R.id.tilName) protected TextInputLayout tilName;
    @BindView(R.id.tilLat) protected TextInputLayout tilLat;
    @BindView(R.id.tilLng) protected TextInputLayout tilLng;

    public MarkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mark, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
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
        if ( validateLatLng() && validateName() ){
            saveMark();
        }
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void saveMark(){
        Realm realm = Realm.getDefaultInstance();
        int newId = 0;
        RealmResults<Mark> allMarks = realm.where(Mark.class).findAllSorted("id", Sort.DESCENDING);
        if (allMarks.size() > 0) {
            newId = allMarks.first().getId() + 1;
        }

        final Mark mark = new Mark();
        mark.setId(newId);
        mark.setName(etMarkName.getText().toString());
        mark.setLatitude(Double.parseDouble(etLat.getText().toString()));
        mark.setLongitude(Double.parseDouble(etLng.getText().toString()));
        mark.setDate(new Date());

        realm.beginTransaction();
        realm.insertOrUpdate(mark);
        realm.commitTransaction();
        realm.close();

        printDb();
    }

    private boolean validateLatLng(){
        resetErrorHints();
        boolean isValid = true;
        String lat = etLat.getText().toString();
        if ((lat.length() == 0) || ( !Utils.validateLat(lat) )){
            tilLat.setError(incorrectValue);
            isValid = false;
        }

        String lng = etLng.getText().toString();
        if ((lng.length() == 0) || ( !Utils.validateLng(lng)) ){
            tilLng.setError(incorrectValue);
            isValid = false;
        }

        return isValid;
    }

    private boolean validateName(){
        if (etMarkName.getText().toString().replaceAll(" ", "").length() == 0){
            tilName.setError(incorrectValue);
            return false;
        } else {
            tilName.setError(null);
            return true;
        }
    }

    private void resetErrorHints(){
        tilName.setError(null);
        tilLat.setError(null);
        tilLng.setError(null);
    }

    private void printDb(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Mark> marks = realm.where(Mark.class).findAll();
        for (Mark mark : marks) {
            Log.d(TAG, "printDb: " + mark.getName());
        }
        realm.close();
    }
}
