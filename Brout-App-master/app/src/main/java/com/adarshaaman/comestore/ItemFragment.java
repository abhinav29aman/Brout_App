package com.adarshaaman.comestore;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
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
import java.util.HashMap;
import java.util.Map;


public class ItemFragment extends Fragment {
    public ArrayList<Inventory> inventories = new ArrayList<Inventory>();
    ;
    public int state;// 1 for mamin activity ,  2 for shopactivity , 3 for favourites
    public String phone; //shop phone no to get data
    public static Shop orderShop;
    public ItemAdapter ad;

    public ItemFragment(int state, String phone) {
        this.state = state;
        this.phone = phone;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_recycler, container, false);


        RecyclerView rv = (RecyclerView) v.findViewById(R.id.recyclerview);

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        ad = new ItemAdapter();
        if (state == 1) {
            HomeActivity.mAdBase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    inventories.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Inventory i = postSnapshot.getValue(Inventory.class);
                        inventories.add(i);
                    }
                    ad.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        if (state == 2) {


            DatabaseReference ref = HomeActivity.mDataBase.getReference("item" + phone);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    inventories.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Inventory i = postSnapshot.getValue(Inventory.class);
                        inventories.add(i);

                    }
                    ad.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        if (state == 3) {


            DatabaseReference ref = HomeActivity.mDataBase.getReference("favourites" + HomeActivity.userPhone);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    inventories.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Inventory i = postSnapshot.getValue(Inventory.class);
                        inventories.add(i);

                    }
                    ad.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        rv.setAdapter(ad);
        return v;

    }

    public class Itemholder extends RecyclerView.ViewHolder {
        public Itemholder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.ad_listitem, parent, false));


        }
    }

    public class ItemAdapter extends RecyclerView.Adapter<Itemholder> {

        public ItemAdapter() {
        }

        public Itemholder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            Itemholder i = new Itemholder(inflater, parent);
            return i;

        }

        @Override
        public void onBindViewHolder(final Itemholder holder, final int position) {
            final Inventory i = inventories.get(position);
            final Button editPriceButton = (Button) holder.itemView.findViewById(R.id.edit_priceButton);
            final EditText editPriceText = (EditText) holder.itemView.findViewById(R.id.price_edit);
            final EditText editTitle = (EditText) holder.itemView.findViewById(R.id.edit_title);
            final EditText editDescription = (EditText) holder.itemView.findViewById(R.id.edit_description);
            final View relativeRadio = holder.itemView.findViewById(R.id.relativeradio);
            final RadioGroup rg1 = (RadioGroup) holder.itemView.findViewById(R.id.rg1);
            final RadioGroup rg2 = (RadioGroup) holder.itemView.findViewById(R.id.rg2);
            final TextView liketext = ((TextView) holder.itemView.findViewById(R.id.likecount));
            liketext.setText("" + i.getLikes());
            editTitle.setVisibility(View.GONE);
            editDescription.setVisibility(View.GONE);
            relativeRadio.setVisibility(View.GONE);
            editPriceButton.setVisibility(View.GONE);
            editPriceText.setVisibility(View.GONE);
            final TextView ttitle = ((TextView) (holder.itemView.findViewById(R.id.ad_title)));
            ttitle.setText(i.getTitle());
            final TextView tprice = ((TextView) (holder.itemView.findViewById(R.id.ad_price)));
            tprice.setText("Rs " + i.getPrice());
            final TextView tdescription = ((TextView) (holder.itemView.findViewById(R.id.ad_description)));
            tdescription.setText(i.getDescription());
            ImageView iv = (ImageView) holder.itemView.findViewById(R.id.ad_image);
            final TextView tc = ((TextView) (holder.itemView.findViewById(R.id.ad_delivery)));
            tc.setText(i.getReturnPolicy());
            final TextView tt = ((TextView) (holder.itemView.findViewById(R.id.ad_stock)));

            if (i.isInStock() != false) {
                tt.setText("In stock");
                tt.setTextColor(Color.BLACK);
            } else {
                tt.setText("Out of Stock");
                tt.setTextColor(Color.RED);
            }
            if (i.isService()) {
                tc.setVisibility(View.GONE);
                tt.setText("Service");
                tt.setTextColor(Color.BLACK);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageActivity.invent = i;

                    Intent i = new Intent(getActivity(), ImageActivity.class);
                    startActivity(i);
                }
            });
            Glide.with(getActivity()).load(i.getImageurl()).into(iv);


            if ((state == 2) && (HomeActivity.userPhone.equals(phone))) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {


                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("What do you wish to do?").setTitle("Edit").setCancelable(true).setPositiveButton("Delete item", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (i.isAd()) {
                                    HomeActivity.mDataBase.getReference("item" + phone + "/" + i.getKey()).removeValue();
                                    HomeActivity.mAdBase.child(i.getKey()).removeValue();
                                } else {
                                    HomeActivity.mDataBase.getReference("item" + phone + "/" + i.getKey()).removeValue();
                                }
                            }


                        }).setNegativeButton("Edit item", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                editDescription.setVisibility(View.VISIBLE);
                                editDescription.setText(i.getDescription());
                                editTitle.setVisibility(View.VISIBLE);
                                editTitle.setText(i.getTitle());
                                relativeRadio.setVisibility(View.VISIBLE);
                                editPriceButton.setVisibility(View.VISIBLE);
                                editPriceText.setVisibility(View.VISIBLE);
                                editPriceText.setText(i.getPrice() + "");
                                tprice.setVisibility(View.GONE);
                                ttitle.setVisibility(View.GONE);
                                tdescription.setVisibility(View.GONE);

                                editPriceButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        int s = Integer.parseInt(editPriceText.getText().toString());
                                        String tt = editTitle.getText().toString();
                                        String dd = editDescription.getText().toString();
                                        String ss = i.getReturnPolicy();
                                        if (rg1.getCheckedRadioButtonId() != (-1)) {

                                            ss = ((RadioButton) (holder.itemView.findViewById(rg1.getCheckedRadioButtonId()))).getText().toString();
                                        }
                                        boolean st = i.isInStock();
                                        if (rg2.getCheckedRadioButtonId() != (-1)) {
                                            if (((RadioButton) (holder.itemView.findViewById(rg2.getCheckedRadioButtonId()))).getText().toString().equals("In stock")) {
                                                st = true;
                                            } else {
                                                st = false;
                                            }
                                        }
                                        Inventory j = new Inventory(tt, dd, s, i.getLikes(), i.getImageurl(), i.getShopId(), i.getKey(), i.isAd(), i.getOrders(), st, ss, i.isService());
                                        if (i.isAd()) {
                                            HomeActivity.mDataBase.getReference("item" + phone + "/" + i.getKey()).setValue(j);
                                            HomeActivity.mAdBase.child(i.getKey()).setValue(j);
                                        } else {
                                            HomeActivity.mDataBase.getReference("item" + phone + "/" + i.getKey()).setValue(j);
                                        }
                                        tprice.setVisibility(View.VISIBLE);
                                        ttitle.setVisibility(View.VISIBLE);
                                        tdescription.setVisibility(View.VISIBLE);
                                        editDescription.setVisibility(View.GONE);
                                        editTitle.setVisibility(View.GONE);
                                        editPriceText.setVisibility(View.GONE);
                                        relativeRadio.setVisibility(View.GONE);
                                        editPriceButton.setVisibility(View.GONE);
                                    }
                                });
                            }
                        });
                        builder.create().show();

                        return false;
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return inventories.size();
        }
    }

}

