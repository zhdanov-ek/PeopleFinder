package com.example.gek.peoplefinder.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.example.gek.peoplefinder.enums.StateMenu;
import com.example.gek.peoplefinder.interfaces.DrawerMenuStateChanger;


public abstract class BaseFragment extends Fragment{
    protected DrawerMenuStateChanger mCallbackDrawerMenuStateChanger;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallbackDrawerMenuStateChanger = (DrawerMenuStateChanger) getActivity();
        } catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString() + " must implement DrawerMenuStateChanger");
        }
    }

    protected void defineRootDrawerMenu(StateMenu state){
        if (!getActivity().isFinishing()){
            mCallbackDrawerMenuStateChanger.setMenuState(state);
        }
    }
}
