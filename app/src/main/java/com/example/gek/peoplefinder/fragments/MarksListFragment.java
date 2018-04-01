package com.example.gek.peoplefinder.fragments;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.enums.StateMenu;
import com.example.gek.peoplefinder.helpers.MarksAdapter;
import com.example.gek.peoplefinder.models.Mark;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MarksListFragment extends BaseFragment{
    private Unbinder unbinder;
    private Realm mRealm;

    @BindView(R.id.recyclerView) protected RecyclerView recyclerView;

    private final RealmChangeListener<RealmResults<Mark>> changeListener =
            new RealmChangeListener<RealmResults<Mark>>() {
                @Override
                public void onChange(RealmResults<Mark> elements) {
                    updateUI(elements);
                }
            };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_marks_list, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        setToolbarTitle(getString(R.string.title_all_marks));
        mCallbackDrawerMenuStateChanger.setMenuState(StateMenu.MARK);

        mRealm = Realm.getDefaultInstance();
        RealmResults<Mark> marks = mRealm.where(Mark.class).findAll();
        updateUI(marks);
        marks.addChangeListener(changeListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        mRealm.close();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void updateUI(RealmResults<Mark> marks) {
        if (recyclerView != null){
            if (recyclerView.getAdapter() == null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(new MarksAdapter(marks));
            }
            ((MarksAdapter)recyclerView.getAdapter()).swapData(marks);
        }
    }
}
