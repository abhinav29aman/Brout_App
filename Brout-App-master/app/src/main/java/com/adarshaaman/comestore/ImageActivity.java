package com.adarshaaman.comestore;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Api;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class ImageActivity extends AppCompatActivity {
    public  static  Inventory invent;
public static int whichAddress = 1; // 1 by default , 2 for current , 3 for last

    //geocoder string method
    public static String getAddress(double lat , double lon , Context c){
        String ret = "" ;
        Geocoder geocoder = new Geocoder(c , Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(lat,lon,1);
            ret = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            ret = "Can't show address but it has been obtained by GPS : Latitide" + lat +" Longitude " + lon;
        }


        return ret;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    setContentView(R.layout.image_display);
    ImageView iv = (ImageView)findViewById(R.id.image_view);

    //----------------------------------------------------------------------------------
        //setting up order layout



       final ViewGroup orderlayout = ((ViewGroup)(findViewById(R.id.order_layout)));
       final EditText addressText = (EditText) (findViewById(R.id.ettt));
        final Button order = (Button) (findViewById(R.id.iorder1));
        final Button currentAd = (Button) (findViewById(R.id.location_current));
        final Button lastUsedAd = (Button) (findViewById(R.id.location_last));

           addressText.setVisibility(View.GONE);
           order.setVisibility(View.GONE);
           currentAd.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   String address = getAddress(HomeActivity.currentLocation.getLatitude(),HomeActivity.currentLocation.getLongitude(),ImageActivity.this);
                   addressText.setVisibility(View.VISIBLE);
                   order.setVisibility(View.VISIBLE);
                   addressText.setText(address);
                   whichAddress = 2;
               }
           });

        lastUsedAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = getAddress(HomeActivity.user.getLatitude(),HomeActivity.user.getLongitude(),ImageActivity.this);
                addressText.setVisibility(View.VISIBLE);
                order.setVisibility(View.VISIBLE);
                addressText.setText(address);
                whichAddress = 3;
            }
        });



       //-----------------------------------------------------------------------------------------------
       orderlayout.setVisibility(View.GONE);
    try{
    Glide.with(this).load(invent.getImageurl()).into(iv);}catch (Exception e){}
        ((TextView)findViewById(R.id.ititle)).setText(invent.getTitle());
        ((TextView)findViewById(R.id.idescription)).setText(invent.getDescription());
        ((TextView)findViewById(R.id.iprice)).setText("Rs "+invent.getPrice());

        Button shopButton = (Button)findViewById(R.id.ishop);
        final Button buyButton = (Button)findViewById(R.id.iorder);
        final ImageView love2 = (ImageView)findViewById(R.id.love2);
        final ImageView love3 = (ImageView) findViewById(R.id.love3);

        if (BusinessWriteActivity.isMember(HomeActivity.favourites,invent.getKey())){

            love2.setVisibility(View.GONE);
            love3.setVisibility(View.VISIBLE);
        }
        else {love2.setVisibility(View.VISIBLE);
            love3.setVisibility(View.GONE);}
        love3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                love2.setVisibility(View.VISIBLE);
                love3.setVisibility(View.GONE);
                HomeActivity.mDataBase.getReference("favourites"+ HomeActivity.userPhone).child(invent.getKey()).removeValue();
                Toast.makeText(ImageActivity.this,"Removed from favourites",Toast.LENGTH_SHORT).show();
                int n = invent.getLikes();
                invent.setLikes(n-1);
                HomeActivity.mDataBase.getReference("item"+ invent.getShopId()).child(invent.getKey()).child("likes").setValue(n-1);
                BusinessWriteActivity.deleteString(HomeActivity.favourites,invent.getKey());
                if (invent.isAd()){HomeActivity.mAdBase.child(invent.getKey()).child("likes").setValue(n-1);}
            }
        });
        love2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                love2.setVisibility(View.GONE);
                love3.setVisibility(View.VISIBLE);
                HomeActivity.mDataBase.getReference("favourites"+ HomeActivity.userPhone).child(invent.getKey()).setValue(invent);
                Toast.makeText(ImageActivity.this,"Added to favourites",Toast.LENGTH_SHORT).show();
                int n = invent.getLikes();
                invent.setLikes(n+1);
                HomeActivity.mDataBase.getReference("item"+ invent.getShopId()).child(invent.getKey()).child("likes").setValue(n+1);
                if (invent.isAd()){HomeActivity.mAdBase.child(invent.getKey()).child("likes").setValue(n+1);}
            }
        });
        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.mDataBase.getReference("id"+invent.getShopId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       final Shop s = dataSnapshot.getValue(Shop.class);
                       ShopActivity.shop = s;
                       Intent i = new Intent(ImageActivity.this, ShopActivity.class);
                       startActivity(i);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

//adding to cart-----------------------------------------------------------------------------------
        Button addCart = (Button) findViewById(R.id.add_cart);
        if(invent.isInStock()==false){addCart.setVisibility(View.GONE);}
        addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.mDataBase.getReference("id"+ invent.getShopId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       final Shop ss = dataSnapshot.getValue(Shop.class);

                        HomeActivity.mDataBase.getReference("userToken"+ ss.getContact()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnap) {
                                String shopToken = dataSnap.getValue(String.class);
                                DatabaseReference ref = HomeActivity.mDataBase.getReference("userCart"+HomeActivity.userPhone).push();
                                String k= ref.getKey();
                                User use = new User();
                                Order ord = new Order(invent.getKey(), invent.getTitle(), ss, invent.getPrice(), 1, "ordered", HomeActivity.rider, 0,use , k,invent.getReturnPolicy(),HomeActivity.userToken,shopToken ,HomeActivity.riderToken,0);
                                ref.setValue(ord);
                                Toast.makeText(ImageActivity.this,"Added to cart",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

// choosing buy now ------------------------------------------------------------------------------------------
      buyButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              orderlayout.setVisibility(View.VISIBLE);
              if (HomeActivity.user == null) {
                  lastUsedAd.setVisibility(View.GONE);
              }

          }
      });

      order.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              final Inventory h = invent;
              final String p = h.getShopId();



              HomeActivity.mDataBase.getReference("id" + p).addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull final DataSnapshot dataSnapsho) {
                      final Shop orderShop = dataSnapsho.getValue(Shop.class);

                      final DatabaseReference ref = HomeActivity.mDataBase.getReference("Orders").push();
                      final String key = ref.getKey();
                      final int radom = (int) (Math.random() * 9000 + 1000);


                      HomeActivity.mDataBase.getReference("userToken" + orderShop.getContact()).addListenerForSingleValueEvent(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnap) {
                              final String shopToken = dataSnap.getValue(String.class);
                              User use = new User();
                              if (whichAddress == 2) {
                                  use = new User(HomeActivity.userPhone, HomeActivity.currentLocation.getLatitude(), HomeActivity.currentLocation.getLongitude());
                                  HomeActivity.mDataBase.getReference("user" + HomeActivity.userPhone).setValue(use);
                              } else if (whichAddress == 3) {
                                  use = HomeActivity.user;
                              }
                              Order ord = new Order(h.getKey(), h.getTitle(), orderShop, h.getPrice(), 1, "ordered", HomeActivity.rider, radom, use, key, h.getReturnPolicy(), HomeActivity.userToken, shopToken, HomeActivity.riderToken, 0);
                              ref.setValue(ord);
                              HomeActivity.mDataBase.getReference("shopOrder" + orderShop.getContact()).child(key).setValue(ord);
                              HomeActivity.mDataBase.getReference("userOrder" + HomeActivity.userPhone).child(key).setValue(ord);
                              Toast.makeText(ImageActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();
                              //notification sending
                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                          }
                      });


                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {

                  }
              });
              orderlayout.setVisibility(View.GONE);
          }

      } );





        TextView tt=((TextView) (findViewById(R.id.ad_stock)));
        TextView delivery = ((TextView) (findViewById(R.id.ad_delivery)));
                delivery.setText(invent.getReturnPolicy());

        if(invent.isInStock() != false)
        {
            tt.setText("In stock");
            tt.setTextColor(Color.BLACK);
            buyButton.setVisibility(View.VISIBLE);}
        else {TextView t=((TextView) (findViewById(R.id.ad_stock)));
            t.setText("Out of Stock");
            t.setTextColor(Color.RED);
        buyButton.setVisibility(View.GONE);}

        if (invent.isService()){
            buyButton.setVisibility(View.GONE);
            delivery.setVisibility(View.GONE);
           tt.setText("Service");
            tt.setTextColor(Color.BLACK);
        }

    }

}
