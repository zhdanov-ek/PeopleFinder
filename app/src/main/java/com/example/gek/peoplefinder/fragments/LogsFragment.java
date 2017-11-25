package com.example.gek.peoplefinder.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.helpers.Connection;
import com.example.gek.peoplefinder.helpers.LogHelper;

public class LogsFragment extends Fragment {

    private TextView tvLog;
    private FloatingActionButton fabRefresh;
    private LogHelper logHelper;
    private AnimatedVectorDrawableCompat rotationAnim;

    public LogsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logHelper = new LogHelper(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_logs, container, false);

        tvLog = (TextView) rootView.findViewById(R.id.tvLog);
        tvLog.setText(logHelper.readLog());

        rotationAnim = AnimatedVectorDrawableCompat.create(getActivity(), R.drawable.arrows_vector_animate);

        fabRefresh = (FloatingActionButton) rootView.findViewById(R.id.fabRefresh);
        fabRefresh.setImageDrawable(rotationAnim);
        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFab();
            }
        });

        return rootView;
    }

    // TODO: 11/25/2017 This need move to AsyncTask
    private void clickFab(){
        if (Connection.getInstance().isServiceRunning()){
            fabRefresh.setClickable(false);
            rotationAnim.start();
            String log = logHelper.readLog();
            if ((log != null) && (log.length() > 0)) {
                tvLog.setText(log);
            }
            fabRefresh.setClickable(true);
        } else {
            Toast.makeText(getActivity(), "You must run service firstly", Toast.LENGTH_SHORT).show();
        }

    }
}
