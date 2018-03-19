package com.example.gek.peoplefinder.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.example.gek.peoplefinder.activities.FragmentChanger;
import com.example.gek.peoplefinder.enums.StateMenu;
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

    protected void defineRootDrawerMenu(StateMenu state){
        if (!getActivity().isFinishing()){
            mCallbackDrawerMenuStateChanger.setMenuState(state);
        }
    }
}
