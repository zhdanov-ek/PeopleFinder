package com.example.gek.peoplefinder.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.example.gek.peoplefinder.activities.FragmentChanger;
import com.example.gek.peoplefinder.interfaces.DrawerMenuStateChanger;


public abstract class BaseFragment extends Fragment{
    protected DrawerMenuStateChanger mCallbackDrawerMenuStateChanger;
    protected FragmentChanger mFragmentChanger;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallbackDrawerMenuStateChanger = (DrawerMenuStateChanger) getActivity();
            mFragmentChanger = (FragmentChanger) getActivity();
        } catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString() + " must implement DrawerMenuStateChanger, FragmentChanger");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    protected void setToolbarTitle(String title){
        mFragmentChanger.setToolbarTitle(title);
    }
}
