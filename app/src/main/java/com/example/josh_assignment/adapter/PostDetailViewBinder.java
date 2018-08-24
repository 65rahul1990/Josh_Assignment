package com.example.josh_assignment.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.josh_assignment.R;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostDetailViewBinder extends UltimateRecyclerviewViewHolder {
    public static final int layout = R.layout.feed_list_item;
    @BindView(R.id.tvDescription) TextView tvDescription;
    @BindView(R.id.tvDate) TextView tvDate;
    @BindView(R.id.tvWatcherCount)  TextView tvWatcherCount;
    @BindView(R.id.tvCommitCount) TextView tvLikesCount;
    @BindView(R.id.tvStarCount) TextView tvShareCount;
    @BindView(R.id.thumbnail) ImageView thumbNailImage;


    /*** give more control over NORMAL or HEADER view binding
     *
     * @param itemView view binding
     * @param isItem   bool
     */
    public PostDetailViewBinder(View itemView, boolean isItem) {
        super(itemView);
        if (isItem){
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onItemSelected() {
//        itemView.setBackgroundColor(Color.LTGRAY);
    }

    @Override
    public void onItemClear() {
//        itemView.setBackgroundColor(0);
    }
}


