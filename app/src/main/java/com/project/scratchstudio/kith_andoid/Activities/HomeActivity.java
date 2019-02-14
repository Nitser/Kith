package com.project.scratchstudio.kith_andoid.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.TreeService;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        LinearLayout parent = findViewById(R.id.paper);
        TreeService treeService = new TreeService();

        treeService.setAttributes(parent);

    }



}
