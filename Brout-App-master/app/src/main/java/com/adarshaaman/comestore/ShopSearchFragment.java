package com.adarshaaman.comestore;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;




public class ShopSearchFragment extends Fragment {
    private ArrayList<Shop> shops = new ArrayList<Shop>();
public  Context getmyContext (){
    return  (getActivity().getApplicationContext());
}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_recycler,container,false);


        RecyclerView rv = (RecyclerView)v.findViewById(R.id.recyclerview);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        final ShopAdapter ad = new ShopAdapter();
        rv.setAdapter(ad);
        DatabaseReference r = HomeActivity.mDataBase.getReference("Categories/"+(ShopSearchActivity.category));

        r.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shops.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Shop i= postSnapshot.getValue(Shop.class);
                    shops.add(i);
                } ad.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return v;

    }
    public class Shopholder extends RecyclerView.ViewHolder{
        public Shopholder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.shop_listitem,parent,false));


        }
    }

    public class ShopAdapter extends RecyclerView.Adapter<Shopholder>{

        public ShopAdapter(){}
        public Shopholder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            Shopholder i = new Shopholder(inflater,parent); return i;

        }
        @Override
        public void onBindViewHolder(Shopholder holder, final int position) {
            Shop s = shops.get(position);
            holder.itemView.findViewById(R.id.shop_image).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(),ShopActivity.class);
                   i.putExtra("SHOPPHONE1",shops.get(position).getContact());
                   ShopActivity.shop= shops.get(position);

                    startActivity(i);
                }
            });
           ImageView iv = (ImageView) holder.itemView.findViewById(R.id.shop_image);
           ((TextView) holder.itemView.findViewById(R.id.shop_name)).setText(s.getName());
           ((TextView) holder.itemView.findViewById(R.id.shop_address)).setText(s.getAddressLine1()+" ,"+s.getAddressLine2());
           try{Glide.with(getmyContext()).load(s.getPhotourl()).into(iv);}catch (Exception e){
               e.printStackTrace();
           }

        }

        @Override
        public int getItemCount() {            return shops.size();        }
    }

}
