package com.example.josh_assignment.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.example.josh_assignment.MyApplicationClass;
import com.example.josh_assignment.R;
import com.example.josh_assignment.adapter.PostDetailAdapter;
import com.example.josh_assignment.database.DBManager;
import com.example.josh_assignment.database.PostEntity;
import com.example.josh_assignment.events.FilterUpdateEvent;
import com.example.josh_assignment.events.RefreshEvent;
import com.example.josh_assignment.network.ApiManager;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailFragment extends Fragment {
    @BindView(R.id.ultimate_recycler_view)
    UltimateRecyclerView ultimateRecyclerView;
    @BindView(R.id.tv_error_message) TextView tvErrorMessage;
    @BindView(R.id.feed_loader)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;


    private List<PostEntity> postEntities = new ArrayList<>();
    private PostDetailAdapter simpleRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;
    private String [] urls;
    private static  int page = 0;
    private Realm realm;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_feed_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        realm = Realm.getDefaultInstance();
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            toolbar.setTitle(R.string.app_name);
        }
        page = MyApplicationClass.getSharedPreferences().getInt("page", 0);

        urls = getActivity().getResources().getStringArray(R.array.url);
        postEntities.clear();
        simpleRecyclerViewAdapter = new PostDetailAdapter(getDataFromDataBase());
        linearLayoutManager = new LinearLayoutManager(getActivity());
        ultimateRecyclerView.setLayoutManager(linearLayoutManager);
        ultimateRecyclerView.setHasFixedSize(false);
        ultimateRecyclerView.setItemViewCacheSize(simpleRecyclerViewAdapter.getAdditionalItems());
        ultimateRecyclerView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.light_bg_gray));
        ultimateRecyclerView.setAdapter(simpleRecyclerViewAdapter);
        ultimateRecyclerView.setLoadMoreView(LayoutInflater.from(getActivity()).inflate(R.layout.custom_bottom_progressbar, null));
        ultimateRecyclerView.setDefaultOnRefreshListener(() -> new Handler().postDelayed(() -> {
            ultimateRecyclerView.setRefreshing(false);
            page = 0;
            simpleRecyclerViewAdapter.removeAll();
            getNotifications(urls[page]);

            linearLayoutManager.scrollToPosition(0);
        }, 1000));

        ultimateRecyclerView.setOnLoadMoreListener((itemsCount, maxLastVisiblePosition) -> {
            if(page < 2) {
                getNotifications(urls[++page]);
            }
        });
        ultimateRecyclerView.reenableLoadmore();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private List<PostEntity> getDataFromDataBase(){
        RealmResults<PostEntity> postEntityRealmResults = realm.where(PostEntity.class).findAll();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (postEntityRealmResults.isEmpty()){
                progressBar.show();
                tvErrorMessage.setVisibility(View.VISIBLE);
                getNotifications(urls[page]);
            }else {
                tvErrorMessage.setVisibility(View.GONE);
                progressBar.hide();
                postEntities.addAll(postEntityRealmResults);
            }
        } else {
            progressBar.hide();
            postEntities.addAll(postEntityRealmResults);
        }
        return postEntities;
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void showInternetErrorDialog(){
        if(getActivity() == null){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.internet_error));
        builder.setCancelable(true);
        builder.setPositiveButton("OK", (dialog, which) -> {
            RealmResults<PostEntity> postEntityRealmResults = realm.where(PostEntity.class).findAll();
            simpleRecyclerViewAdapter.insert(postEntityRealmResults);
                    dialog.dismiss();
                    });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private void getNotifications(String url){
        if(!isNetworkAvailable(getContext())){
            progressBar.hide();
            showInternetErrorDialog();
            return;
        }

        Call<ResponseBody> request = ApiManager.getInstance().searchApi(url);
        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                progressBar.hide();
                if (response.isSuccessful()){
                    List<PostEntity> postEntityList = DBManager.getInstance().setFeedDataInDB(response.body());
                    updateAdapter(postEntityList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                progressBar.hide();
            }
        });
    }

    @Subscribe
    public void refreshList(RefreshEvent refreshEvent){
        page = 0;
        simpleRecyclerViewAdapter.removeAll();
        getNotifications(urls[page]);
        linearLayoutManager.scrollToPosition(0);
    }

    @Subscribe
    public void updateAfterFilter(FilterUpdateEvent filterUpdateEvent){
        simpleRecyclerViewAdapter.removeAll();
        simpleRecyclerViewAdapter.insert(filterUpdateEvent.getPostEntities());
    }

    private void updateAdapter(List<PostEntity> postEntities){
        simpleRecyclerViewAdapter.insert(postEntities);
        if (simpleRecyclerViewAdapter.getItemCount() > 0){
            tvErrorMessage.setVisibility(View.GONE);
        } else {
            tvErrorMessage.setVisibility(View.VISIBLE);
            tvErrorMessage.setText(R.string.error_msg);
        }
    }

    @OnClick(R.id.fab)
    void filterFragment(){
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        FilterFragment newFragment = FilterFragment.newInstance();
        newFragment.setParentFab(fab);
        newFragment.show(ft, "dialog");
    }


    public  static class FilterFragment extends AAH_FabulousFragment implements  CompoundButton.OnCheckedChangeListener{
        ArrayMap<String, List<String>> applied_filters = new ArrayMap<>();
        @BindView(R.id.view) RadioButton rbtnView;
        @BindView(R.id.like) RadioButton rbtnLike;
        @BindView(R.id.share) RadioButton rbtnShare;
        @BindView(R.id.desc) RadioButton rbtnDesc;
        @BindView(R.id.asc) RadioButton rbtnAsc;
        @BindView(R.id.date) RadioButton rbtnDate;
        @BindView(R.id.rl_content) RelativeLayout rl_content;
        @BindView(R.id.ll_buttons) LinearLayout ll_buttons;

        private String sortBy;
        private String orderBy;
        RealmResults<PostEntity> postEntities;
        Realm realm;

        public static FilterFragment newInstance() {
            return new FilterFragment();
        }

        @Override
        public void setupDialog(Dialog dialog, int style) {
            View contentView = View.inflate(getContext(), R.layout.filter_fragment, null);
            ButterKnife.bind(this, contentView);
            realm = Realm.getDefaultInstance();

            rbtnView.setOnCheckedChangeListener(this);
            rbtnLike.setOnCheckedChangeListener(this);
            rbtnShare.setOnCheckedChangeListener(this);
            rbtnDesc.setOnCheckedChangeListener(this);
            rbtnAsc.setOnCheckedChangeListener(this);
            rbtnDate.setOnCheckedChangeListener(this);
            postEntities = realm.where(PostEntity.class).findAll();

            //params to set
            setAnimationDuration(400); //optional; default 500ms
            setPeekHeight(300); // optional; default 400dp
            setViewgroupStatic(ll_buttons); // optional; layout to stick at bottom on slide
            setViewMain(rl_content); //necessary; main bottomsheet view
            setMainContentView(contentView); // necessary; call at end before super
            super.setupDialog(dialog, style); //call super at last

        }

        @OnClick(R.id.imgbtn_apply)
        void saveFilter(){
            if (sortBy == null){
                closeFilter(applied_filters);
                return;
            }
            RealmQuery<PostEntity> realmQuery = postEntities.where();
            if (orderBy != null && orderBy.equalsIgnoreCase("desc")){
                realmQuery = realmQuery.sort(sortBy, Sort.DESCENDING);
            } else {
                realmQuery = realmQuery.sort(sortBy, Sort.ASCENDING);
            }
            postEntities = realmQuery.findAll();
            EventBus.getDefault().post(new FilterUpdateEvent(postEntities));
            closeFilter(applied_filters);

        }

        @OnClick(R.id.imgbtn_refresh)
        void refreshList(){
            EventBus.getDefault().post(new RefreshEvent());
            closeFilter(applied_filters);
        }

        @OnClick(R.id.img_close_filter)
        void closePopUp(){
            closeFilter(applied_filters);

        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            String sortBy = null, orderBy = null;
            switch (compoundButton.getId()){
                case R.id.view:
                    if (isChecked){
                        sortBy = "views";
                    } else {
                        sortBy = null;
                    }
                    break;
                case R.id.like:
                    if (isChecked){
                        sortBy = "likes";
                    } else {
                        sortBy = null;
                    }
                    break;
                case R.id.share:
                    if (isChecked){
                        sortBy = "shares";
                    } else {
                        sortBy = null;
                    }
                    break;
                case R.id.date:
                    if (isChecked){
                        sortBy = "event_date";
                    } else {
                        sortBy = null;
                    }
                    break;
                case R.id.desc:
                    if (isChecked){
                        orderBy = "desc";
                    } else {
                        orderBy = null;
                    }
                    break;
                case R.id.asc:
                    if (isChecked){
                        orderBy = "asc";
                    } else {
                        orderBy = null;
                    }
                    break;
            }
            if (sortBy != null){
                this.sortBy = sortBy;
            }
            if (orderBy != null){
                this.orderBy = orderBy;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplicationClass.handlePageSize(page);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
