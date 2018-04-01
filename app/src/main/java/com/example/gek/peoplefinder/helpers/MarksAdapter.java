package com.example.gek.peoplefinder.helpers;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.models.Mark;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;
import java.util.Locale;

public class MarksAdapter extends RecyclerView.Adapter<MarksAdapter.MarkViewHolder>{

    private List<Mark> marks;
    private LayoutInflater inflater;

    public MarksAdapter(List<Mark> marks) {
        this.marks = marks;
    }

    public void swapData(List<Mark> marks){
        this.marks = marks;
        this.notifyDataSetChanged();
    }

    @Override
    public MarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        return MarkViewHolder.create(inflater, parent);
    }

    @Override
    public void onBindViewHolder(MarkViewHolder holder, int position) {
        holder.bind(marks.get(position));
    }

    @Override
    public int getItemCount() {
        return marks == null ? 0 : marks.size();
    }


    static class MarkViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivPicture;
        private TextView tvDate;
        private TextView tvName;
        private TextView tvLatLng;

        public static MarkViewHolder create(LayoutInflater inflater, ViewGroup parent){
            return new MarkViewHolder(inflater.inflate(R.layout.item_mark, parent, false));
        }

        private RequestOptions glideOptions = new RequestOptions()
                .circleCrop()
                .error(R.drawable.ic_person)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        private MarkViewHolder(View itemView) {
            super(itemView);
            ivPicture = itemView.findViewById(R.id.ivPicture);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvName = itemView.findViewById(R.id.tvName);
            tvLatLng = itemView.findViewById(R.id.tvLatLng);
        }

        public void bind(Mark mark){
            PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
            String ago = prettyTime.format(mark.getDate());
            tvDate.setText(ago);
            tvName.setText(mark.getName());
            tvLatLng.setText(mark.getPosition().toString());
            if (!TextUtils.isEmpty(mark.getImageUrl())){
                Glide.with(ivPicture.getContext())
                        .load(mark.getImageUrl())
                        .apply(glideOptions)
                        .into(ivPicture);
            } else {
                ivPicture.setImageResource(mark.isPerson() ? R.drawable.ic_person : R.drawable.ic_place);
            }
        }
    }
}