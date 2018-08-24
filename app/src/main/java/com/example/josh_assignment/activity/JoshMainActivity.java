package com.example.josh_assignment.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.josh_assignment.R;
import com.example.josh_assignment.fragment.PostDetailFragment;

public class JoshMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.flLayout, new PostDetailFragment(), "ListView")
                    .commit();
        }

    }
}
