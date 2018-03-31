package com.example.gek.peoplefinder.helpers.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.helpers.Connection;
import com.example.gek.peoplefinder.helpers.Db;
import com.example.gek.peoplefinder.helpers.Utils;
import com.example.gek.peoplefinder.models.Mark;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Locale;

public class MarkInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Context mContext;

    public MarkInfoWindowAdapter(Context context) {
        mContext = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Mark mark = Db.findMark(marker.getTitle(), null);
        if (mark != null){
            if (Utils.isUsersMark(mark)){
                View view = LayoutInflater.from(mContext).inflate(R.layout.layout_marker_info_window_user, null);
                return view;
            } else {
                View view = LayoutInflater.from(mContext).inflate(R.layout.layout_marker_info_window, null);

                TextView tvName = (TextView) view.findViewById(R.id.tvName);
                TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
                TextView tvDistance = (TextView) view.findViewById(R.id.tvDistance);

                PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
                String ago = prettyTime.format(mark.getDate());
                tvDate.setText(ago);

                tvName.setText(mark.getName());

                LatLng currentUserLocation = Connection.getInstance().getLastLocation();
                if (currentUserLocation != null){
                    StringBuilder distance = new StringBuilder();
                    distance.append(Utils.getDistance(currentUserLocation, marker.getPosition()));
                    distance.append("\n");
                    distance.append(mContext.getString(R.string.mark_direction_from_you,
                            Utils.getDirection(currentUserLocation, marker.getPosition())));
                    tvDistance.setText(distance);
                } else {
                    tvDistance.setVisibility(View.GONE);
                }
                return view;
            }

        }
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
