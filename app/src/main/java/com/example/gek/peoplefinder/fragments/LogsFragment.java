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

import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.helpers.Connection;
import com.example.gek.peoplefinder.helpers.LogHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogsFragment extends Fragment {

    @BindView(R.id.tvLog) protected TextView tvLog;
    @BindView(R.id.fabRefresh) protected FloatingActionButton fabRefresh;
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
        ButterKnife.bind(this, rootView);

        tvLog.setText(logHelper.readLog());
        rotationAnim = AnimatedVectorDrawableCompat.create(getActivity(), R.drawable.arrows_vector_animate);
        fabRefresh.setImageDrawable(rotationAnim);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Connection.getInstance().isServiceRunning()){
            fabRefresh.setVisibility(View.VISIBLE);
        } else {
            fabRefresh.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.fabRefresh) protected void clickFab(){
        refreshLog();
    }

    // TODO: 11/25/2017 This need move to AsyncTask
    private void refreshLog(){
        fabRefresh.setClickable(false);
        rotationAnim.start();
        String log = logHelper.readLog();
        if ((log != null) && (log.length() > 0)) {
            tvLog.setText(log);
        }
        fabRefresh.setClickable(true);
    }

}
