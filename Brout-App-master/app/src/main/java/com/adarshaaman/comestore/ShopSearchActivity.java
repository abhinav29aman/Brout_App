package com.adarshaaman.comestore;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class ShopSearchActivity extends AppCompatActivity {
    public static String category;
    public void onCreate(Bundle savedInstanceState){


        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_framelayout);
         category = getIntent().getStringExtra("Category");
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.empty_frame);
        if (f==null){
            f = new ShopSearchFragment();
            fm.beginTransaction().add(R.id.empty_frame,f).commit();
        }
    }
}
