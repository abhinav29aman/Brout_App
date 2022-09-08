package com.adarshaaman.comestore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class CartActivity extends AppCompatActivity {
public static int whichAddress=1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_activity);
        final ViewGroup orderlayout = ((ViewGroup)(findViewById(R.id.order_layout1)));
        final EditText addressText = (EditText) (findViewById(R.id.ettt1));
        final Button order = (Button) (findViewById(R.id.iorder11));
        final Button currentAd = (Button) (findViewById(R.id.location_current1));
        final Button lastUsedAd = (Button) (findViewById(R.id.location_last1));
        findViewById(R.id.custom_layout).setVisibility(View.GONE);

        addressText.setVisibility(View.GONE);
        order.setVisibility(View.GONE);
        currentAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = ImageActivity.getAddress(HomeActivity.currentLocation.getLatitude(),HomeActivity.currentLocation.getLongitude(),CartActivity.this);
                addressText.setVisibility(View.VISIBLE);
                order.setVisibility(View.VISIBLE);
                addressText.setText(address);
                whichAddress = 2;
            }
        });

        lastUsedAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = ImageActivity.getAddress(HomeActivity.user.getLatitude(),HomeActivity.user.getLongitude(),CartActivity.this);
                addressText.setVisibility(View.VISIBLE);
                order.setVisibility(View.VISIBLE);
                addressText.setText(address);
                whichAddress = 3;
            }
        });



        //-----------------------------------------------------------------------------------------------
        orderlayout.setVisibility(View.GONE);
         Button buyButton = (Button) findViewById(R.id.order_cart);

         order.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String rider = HomeActivity.rider;
                 User use= new User();
                 if(whichAddress==2){use= new User(HomeActivity.userPhone, HomeActivity.currentLocation.getLatitude(), HomeActivity.currentLocation.getLongitude());
                     HomeActivity.mDataBase.getReference("user" + HomeActivity.userPhone).setValue(use);}
                 else if(whichAddress==3){use = HomeActivity.user;}
                 int radom = (int) (Math.random() * 9000 + 1000);
                 for(int i=0; i< HomeFragment.userCart.size();i++){

                     Order ord = HomeFragment.userCart.get(i);

                    if(ord.getNumber()>0) {ord.setCode(radom);
                        ord.setRiderId(rider);
                        ord.setUser(use);

                        HomeActivity.mDataBase.getReference("Orders").child(ord.getOrderKey()).setValue(ord);
                     HomeActivity.mDataBase.getReference("shopOrder" + ord.getShop().getContact()).child(ord.getOrderKey()).setValue(ord);
                     HomeActivity.mDataBase.getReference("userOrder" + HomeActivity.userPhone).child(ord.getOrderKey()).setValue(ord);}

                 }    HomeActivity.mDataBase.getReference("userCart"+HomeActivity.userPhone).removeValue();
                       finish();

             }

         });

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                orderlayout.setVisibility(View.VISIBLE);
                if (HomeActivity.user == null) {
                    lastUsedAd.setVisibility(View.GONE);
                }

            }
        });





       RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerview1);
       rv.setLayoutManager(new LinearLayoutManager(this));
       final CartAdapter ad = new CartAdapter();
        rv.setAdapter(ad);
    }

    public class Cartholder extends RecyclerView.ViewHolder{
        public Cartholder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.cart_item,parent,false));


        }
    }

    public class CartAdapter extends RecyclerView.Adapter<Cartholder>{

        public CartAdapter(){}
        public Cartholder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(CartActivity.this);
            Cartholder i = new Cartholder(inflater,parent); return i;

        }
        @Override
        public void onBindViewHolder(Cartholder holder, final int position) {
          View v = holder.itemView;
           final Order ord = HomeFragment.userCart.get(position);
           final int singleprice = (int)(ord.getTotalPrice()/ord.getNumber());
            ((TextView )v.findViewById(R.id.order_title1)).setText(ord.getItemName());
            final TextView qty =  ((TextView )v.findViewById(R.id.order_quantity1));
            final TextView price =  ((TextView )v.findViewById(R.id.order_price1));
            qty.setText("Qty : " +ord.getNumber());
            price.setText("Rs "+ord.getTotalPrice());
           Button callShop = (Button)v.findViewById(R.id.call_shop1);
           ImageButton add = (ImageButton) v.findViewById(R.id.add1);
            ImageButton minus = (ImageButton) v.findViewById(R.id.minus1);
             callShop.setOnClickListener(new View.OnClickListener() {
                 @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_DIAL);
                  i.setData(Uri.parse("tel:"+ ord.getShop().getContact()));
                     startActivity(i);
                }
             });

             add.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     int m = ord.getNumber();
                     int newPrice = singleprice*(m+1);
                     ord.setNumber(m+1);
                     ord.setTotalPrice(newPrice);
                     qty.setText("Qty : "+ (m+1));
                     price.setText("Rs "+ newPrice);
                     HomeActivity.mDataBase.getReference("userCart"+HomeActivity.userPhone).child(ord.getOrderKey()).child("number").setValue(m+1);
                     HomeActivity.mDataBase.getReference("userCart"+HomeActivity.userPhone).child(ord.getOrderKey()).child("totalPrice").setValue(newPrice);
                 }
             });

             minus.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     int m = ord.getNumber();
                     if(m>0){
                     int newPrice = singleprice*(m-1);
                     ord.setNumber(m-1);
                     ord.setTotalPrice(newPrice);
                         qty.setText("Qty : "+ (m-1));
                         price.setText("Rs "+ newPrice);
                     HomeActivity.mDataBase.getReference("userCart"+HomeActivity.userPhone).child(ord.getOrderKey()).child("number").setValue(m-1);
                     HomeActivity.mDataBase.getReference("userCart"+HomeActivity.userPhone).child(ord.getOrderKey()).child("totalPrice").setValue(newPrice);

                 }}
             });


        }

        @Override
        public int getItemCount() {            return HomeFragment.userCart.size();        }
    }
}
