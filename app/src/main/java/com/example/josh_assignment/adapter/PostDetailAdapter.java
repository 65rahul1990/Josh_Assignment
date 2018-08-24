package com.example.josh_assignment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.josh_assignment.R;
import com.example.josh_assignment.Utilities;
import com.example.josh_assignment.database.PostEntity;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;

import java.util.List;

public class PostDetailAdapter extends easyRegularAdapter<PostEntity, PostDetailViewBinder> {

    public PostDetailAdapter(List<PostEntity> brandEntities) {
        super(brandEntities);
    }

    /**
     * the layout id for the normal data
     * @return the ID
     */
    @Override
    protected int getNormalLayoutResId() {
        return PostDetailViewBinder.layout;
    }

    @Override
    protected PostDetailViewBinder newViewHolder(View view) {
        return new PostDetailViewBinder(view, true);
    }

    @Override
    public PostDetailViewBinder newFooterHolder(View view) {
        return new PostDetailViewBinder(view, false);
    }

    @Override
    public PostDetailViewBinder newHeaderHolder(View view) {
        return new PostDetailViewBinder(view, false);
    }

    public final void insertOne(PostEntity e) {
        insertLast(e);
    }

    public final void removeLastOne() {
        removeLast();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    protected void withBindHolder(PostDetailViewBinder holder, PostEntity postEntity, int position) {
        holder.tvDescription.setText(postEntity.getEvent_name());
        holder.tvLikesCount.setText(String.valueOf(postEntity.getLikes()));
        holder.tvShareCount.setText(String.valueOf(postEntity.getShares()));
        holder.tvWatcherCount.setText(String.valueOf(postEntity.getViews()));
        long days = (postEntity.getEvent_date() / (60*60*24*1000));
        holder.tvDate.setText(""+days +" days ago");

        Glide.with(holder.itemView)
                .asBitmap()
                .load(postEntity.getThumbnail_image())
                .apply(new RequestOptions()
                        .error(R.drawable.image_placeholder)
                        .placeholder(R.drawable.image_placeholder))
                .into(holder.thumbNailImage);

    }

}

