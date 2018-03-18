package com.example.gek.peoplefinder.helpers.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.models.Mark;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws profile photos inside markers (using IconGenerator).
 * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
 */
public class MarkRenderer extends DefaultClusterRenderer<Mark> {
    private IconGenerator mIconGenerator;
    private IconGenerator mClusterIconGenerator;
    private final ImageView mClusterImageView;
    private final int mHeight;
    private final int mWidth;
    private Context mContext;

    private final TextView mShortMarkNameTextView;
    private final ImageView mIconMarkImageView;

    public static final String TAG = "RENDERER";

    public MarkRenderer(Context context, GoogleMap map, ClusterManager clusterManager) {
        super(context, map, clusterManager);

        mContext = context;
        mIconGenerator = new IconGenerator(context);
        mClusterIconGenerator = new IconGenerator(context);

        mHeight = (int) context.getResources().getDimension(R.dimen.custom_mark_height);
        mWidth = (int) context.getResources().getDimension(R.dimen.custom_mark_width);

        View multiProfile = LayoutInflater.from(context).inflate(R.layout.multi_profile, null);
        multiProfile.setLayoutParams(new ViewGroup.LayoutParams(mWidth, mHeight));
        mClusterIconGenerator.setContentView(multiProfile);
        mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

        View markView = LayoutInflater.from(context).inflate(R.layout.one_profile, null);
        mShortMarkNameTextView = (TextView) markView.findViewById(R.id.shortMarkNameTextView);
        mIconMarkImageView = (ImageView) markView.findViewById(R.id.iconMarkImageView);
        markView.setLayoutParams(new ViewGroup.LayoutParams(mWidth, mHeight));

        mIconGenerator.setContentView(markView);
    }

    @Override
    protected void onBeforeClusterItemRendered(Mark mark, MarkerOptions markerOptions) {
        // Draw a single person.
        // Set the info window to show their name.
        mIconMarkImageView.setImageResource(mark.isPerson() ? R.drawable.ic_man : R.drawable.ic_place);
        mShortMarkNameTextView.setText(mark.getName());
        Bitmap icon = mIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(mark.getName());
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<Mark> cluster, MarkerOptions markerOptions) {
//        super.onBeforeClusterRendered(cluster, markerOptions);
        // Draw multiple people.
        // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
        List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
        int width = mWidth;
        int height = mHeight;

        for (Mark mark : cluster.getItems()) {
            // Draw 4 at most.
            if (profilePhotos.size() == 4) break;
            Drawable drawable = mContext.getResources().getDrawable(
                    mark.isPerson() ? R.drawable.ic_man : R.drawable.ic_place);
            drawable.setBounds(0, 0, width, height);
            profilePhotos.add(drawable);
        }
        MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
        multiDrawable.setBounds(0, 0, width, height);

        mClusterImageView.setImageDrawable(multiDrawable);
        Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 1;
    }
}
