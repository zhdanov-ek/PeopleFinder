package com.example.gek.peoplefinder.activities;


import android.os.Bundle;

public interface FragmentChanger {
    void showMarkFragment(Bundle bundle);
    void showMapFragment();
    void hideKeyboard();
    void setToolbarTitle(String text);
}
