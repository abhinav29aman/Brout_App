package com.adarshaaman.comestore;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ShopCategoryFragment extends Fragment {
    public static ArrayList<String> categories = new ArrayList<String>();

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HomeActivity.mCategListBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    String s = postSnapshot.getKey();
                    categories.add(s);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_recycler,container,false);


        RecyclerView rv = (RecyclerView)v.findViewById(R.id.recyclerview);
        rv.setLayoutManager(new GridLayoutManager(getActivity(),2));
       final CategAdapter ad = new CategAdapter();
        rv.setAdapter(ad);
        HomeActivity.mCategListBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 categories.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    String s = postSnapshot.getKey();
                    categories.add(s);

                }ad.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return v;}


    public class Categholder extends RecyclerView.ViewHolder{
        public Categholder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.shop_category,parent,false));
            Random rnd = new Random();
            int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            itemView.setBackgroundColor(currentColor);



        }
    }

    public class CategAdapter extends RecyclerView.Adapter<Categholder>{

        public CategAdapter(){}
        public Categholder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            Categholder i = new Categholder(inflater,parent); return i;

        }
        @Override
        public void onBindViewHolder(Categholder holder, final int position) {
            ((TextView)holder.itemView.findViewById(R.id.name)).setText(categories.get(position)) ;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(),ShopSearchActivity.class);
                    i.putExtra("Category",categories.get(position));
                    startActivity(i);
                }
            });}

        @Override
        public int getItemCount() {            return categories.size();        }
    }

}