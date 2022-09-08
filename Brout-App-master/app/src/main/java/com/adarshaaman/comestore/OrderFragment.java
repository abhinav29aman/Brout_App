package com.adarshaaman.comestore;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderFragment extends Fragment {
   public static Inventory inn;
   public int userType; // 1 for rider , 2 for shops , 3 for normal users

   public OrderFragment(){}

   public OrderFragment(int userType ){ this.userType = userType;}

    private ArrayList<Order> orderUserShop = new ArrayList<Order>();
    public Context getmyContext (){
        return  (getActivity().getApplicationContext());
    }


    public int getColor (String status){
        //returned isn't a status as we delete it from everywhere
        if (status.equals("shipped")){return Color.YELLOW;}
        if (status.equals("delivered")){return Color.GREEN;}
        if (status.equals("requestedForReturn")){return Color.CYAN ;}
        if (status.equals("cancelled")){return Color.RED;}
        return 0;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_recycler,container,false);


        RecyclerView rv = (RecyclerView)v.findViewById(R.id.recyclerview);
        LinearLayoutManager l = new LinearLayoutManager(getActivity());
        l.setStackFromEnd(false);
        rv.setLayoutManager(l);
        final OrderAdapter ad = new OrderAdapter();
        rv.setAdapter(ad);
        DatabaseReference r = HomeActivity.mDataBase.getReference("userOrder"+(HomeActivity.userPhone));
        DatabaseReference s = HomeActivity.mDataBase.getReference("shopOrder"+(HomeActivity.userPhone));
        DatabaseReference t = HomeActivity.mDataBase.getReference("Orders");
        if (userType == 3){
            r.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    orderUserShop.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        Order order = postSnapshot.getValue(Order.class);
                       orderUserShop.add(order) ;
                      } ad.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if(userType==2){
            s.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    orderUserShop.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        Order order = postSnapshot.getValue(Order.class);
                        orderUserShop.add(order) ;
                    } ad.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if(userType==1){
            t.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    orderUserShop.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        Order order = postSnapshot.getValue(Order.class);
                        if(order.getRiderId().equals(HomeActivity.userPhone)){  orderUserShop.add(order) ;}
                    } ad.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        return v;

    }
    public class Orderholder extends RecyclerView.ViewHolder{
        public Orderholder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.user_order,parent,false));


        }
    }

    public class OrderAdapter extends RecyclerView.Adapter<Orderholder>{

        public OrderAdapter(){}
        public Orderholder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            Orderholder i = new Orderholder(inflater,parent); return i;

        }
        @Override
        public void onBindViewHolder(Orderholder holder, final int position) {
           final Order s = orderUserShop.get(position);
            View v = holder.itemView;

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!(s.getType()==0)){
                        CustomOrderActivity.i = s;
                        Intent i = new Intent(getActivity(),CustomOrderActivity.class);
                        startActivity(i);

                    }
                  else  { startImageActivity(s.getShop().getContact(),s.getInventoryKey());}

                }
            });
            final Button callRider = (Button) v.findViewById(R.id.call_rider);
            final Button callShop = (Button) v.findViewById(R.id.call_shop);


            //-----------------------------------------------------------------------------------------
            //rider settings ------------------------------rider settings--------------------------------------------
             TextView shopName = (TextView) v.findViewById(R.id.shopname);
            final View v1 = v.findViewById(R.id.riderdirection);
            final View v2 = v.findViewById(R.id.rider_status);
            final Button shopD = v.findViewById(R.id.shop_direction);
            final Button userD = v.findViewById(R.id.user_direction);
            final Button shipped = v.findViewById(R.id.shipped);
            final Button delivered = v.findViewById(R.id.delivered);
            final Button returned = v.findViewById(R.id.returned);
            if(!(userType == 1)){
                shopName.setVisibility(View.GONE);
                v1.setVisibility(View.GONE);
                v2.setVisibility(View.GONE);
            }

             if (userType== 1){
                 shopName.setText(s.getShop().getName());


             shopD.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Uri navigation = Uri.parse("google.navigation:q="+s.getShop().getLatitude()+","+s.getShop().getLongitude()+"");
                     Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
                     navigationIntent.setPackage("com.google.android.apps.maps");
                     startActivity(navigationIntent);
                 }
             });

            userD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri navigation = Uri.parse("google.navigation:q="+s.getUser().getLatitude()+","+s.getUser().getLongitude()+"");
                    Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
                    navigationIntent.setPackage("com.google.android.apps.maps");
                    startActivity(navigationIntent);
                }
            });



                 if (s.getStatus().equals("ordered")){returned.setVisibility(View.GONE); delivered.setVisibility(View.GONE); shipped.setVisibility(View.VISIBLE);}
                 if (s.getStatus().equals("shipped")){returned.setVisibility(View.GONE); shipped.setVisibility(View.GONE);delivered.setVisibility(View.VISIBLE);}
                 if (s.getStatus().equals("delivered")){returned.setVisibility(View.GONE); delivered.setVisibility(View.GONE); shipped.setVisibility(View.GONE);}
                 if (s.getStatus().equals("requestedForReturn")){delivered.setVisibility(View.GONE); shipped.setVisibility(View.GONE);returned.setVisibility(View.VISIBLE);}
                 if (s.getStatus().equals("cancelled")){returned.setVisibility(View.GONE); delivered.setVisibility(View.GONE); shipped.setVisibility(View.GONE);}
                // if (s.getStatus().equals("returned")){}
                 //happens when we give it back to the shopkeeper, deleted from all database

                 shipped.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                         builder.setMessage("Order shipped?").setTitle("Shipped").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 s.setStatus("shipped");
                                 HomeActivity.mDataBase.getReference("userOrder" + s.getUser().getContact()).child(s.getOrderKey()).child("status").setValue("shipped");
                                 HomeActivity.mDataBase.getReference("Orders").child(s.getOrderKey()).child("status").setValue("shipped");
                                 HomeActivity.mDataBase.getReference("shopOrder" + s.getShop().getContact()).child(s.getOrderKey()).child("status").setValue("shipped");
                             }
                         });


                         builder.create().show();

                     }
                 });




            delivered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Order delivered?").setTitle("Delivered").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            s.setStatus("shipped");
                            HomeActivity.mDataBase.getReference("userOrder" + s.getUser().getContact()).child(s.getOrderKey()).child("status").setValue("delivered");
                            HomeActivity.mDataBase.getReference("Orders").child(s.getOrderKey()).child("status").setValue("delivered");
                            HomeActivity.mDataBase.getReference("shopOrder" + s.getShop().getContact()).child(s.getOrderKey()).child("status").setValue("delivered");
                        }
                    });


                    builder.create().show();

                }
            });

                 returned.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                         builder.setMessage("Order returned to shop?").setTitle("Returned").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 s.setStatus("shipped");
                                 HomeActivity.mDataBase.getReference("userOrder" + s.getUser().getContact()).child(s.getOrderKey()).child("status").setValue("returned");
                                 HomeActivity.mDataBase.getReference("Orders").child(s.getOrderKey()).removeValue();
                                 HomeActivity.mDataBase.getReference("shopOrder" + s.getShop().getContact()).child(s.getOrderKey()).removeValue();
                             }
                         });


                         builder.create().show();

                     }
                 });

          callRider.setText("CALL SHOP");
          callRider.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent i = new Intent(Intent.ACTION_DIAL);
                  i.setData(Uri.parse("tel:"+ s.getShop().getContact()));
                  startActivity(i);

              }
          });
            callShop.setText("CUSTOMER");
          callShop.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent i = new Intent(Intent.ACTION_DIAL);
                  i.setData(Uri.parse("tel:"+ s.getUser().getContact()));
                  startActivity(i);
              }
          });

        }




            ///////////////////////////////////////////////////////////////////////////////////////////////////////


            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(userType ==3) {
                        if ((s.getStatus().equals("ordered")) || (s.getStatus().equals("shipped"))) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Do you wish to cancel this order?").setTitle("Cancel Order").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    s.setStatus("cancelled");
                                    HomeActivity.mDataBase.getReference("userOrder" + HomeActivity.userPhone).child(s.getOrderKey()).child("status").setValue("cancelled");
                                    HomeActivity.mDataBase.getReference("Orders").child(s.getOrderKey()).child("status").setValue("cancelled");
                                    HomeActivity.mDataBase.getReference("shopOrder" + s.getShop().getContact()).child(s.getOrderKey()).removeValue();
                                }
                            });


                            builder.create().show();
                        }

                        if ((s.getStatus().equals("delivered"))) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Do you wish to request a return ?").setTitle("Request return").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    HomeActivity.mDataBase.getReference("Orders").child(s.getOrderKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Order order = dataSnapshot.getValue(Order.class);
                                            if (order == null) {
                                                Toast.makeText(getActivity(), "This can not be returned now", Toast.LENGTH_SHORT);
                                            } else {
                                                if (s.getReturnPolicy().equals("One Day Return")) {

                                                    s.setStatus("requestedForReturn");
                                                    HomeActivity.mDataBase.getReference("userOrder" + HomeActivity.userPhone).child(s.getOrderKey()).child("status").setValue("requestedForReturn");
                                                    HomeActivity.mDataBase.getReference("Orders").child(s.getOrderKey()).child("status").setValue("requestedForReturn");
                                                    HomeActivity.mDataBase.getReference("shopOrder" + s.getShop().getContact()).child(s.getOrderKey()).setValue(s);
                                                    Toast.makeText(getActivity(), "Successfully Requested", Toast.LENGTH_SHORT);
                                                } else {
                                                    Toast.makeText(getActivity(), "No return policy of shop", Toast.LENGTH_SHORT);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }


                            });
                            builder.create().show();
                        } }else if (userType == 2) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Do you wish to delete this item?").setTitle("Delete").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    HomeActivity.mDataBase.getReference("shopOrder" + s.getShop().getContact()).child(s.getOrderKey()).removeValue();
                                }
                            });
                            builder.create().show();

                        }
                        return false;}
                    });

            ((TextView) v.findViewById(R.id.order_code)).setText("Code : " + s.getCode());
            ((TextView) v.findViewById(R.id.order_status)).setText(s.getStatus());
            ((TextView) v.findViewById(R.id.order_title)).setText(s.getItemName());
            ((TextView) v.findViewById(R.id.order_title)).setText(s.getItemName());
            ((TextView) v.findViewById(R.id.order_quantity)).setText("Qty : "+s.getNumber());
            ((TextView) v.findViewById(R.id.order_price)).setText("Rs "+s.getTotalPrice());



      if (!(userType==1)){      if ((s.getStatus().equals("delivered")) || (s.getStatus().equals("cancelled"))) {callRider.setVisibility(View.GONE);}
           callRider.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent i = new Intent(Intent.ACTION_DIAL);
                   i.setData(Uri.parse("tel:"+ s.getRiderId()));
                   startActivity(i);
               }
           });}

         if(userType== 3) { callShop.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent i = new Intent(Intent.ACTION_DIAL);
                   i.setData(Uri.parse("tel:"+ s.getShop().getContact()));
                   startActivity(i);
               }
           });}
            if(userType== 2) {
                  callShop.setText("Customer");
                callShop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_DIAL);
                    i.setData(Uri.parse("tel:" + s.getUser().getContact()));
                    startActivity(i);
                }
            });}

            if (!(s.getStatus().equals("ordered"))){v.findViewById(R.id.statuscode).setBackgroundColor(getColor(s.getStatus()));}

        }

        @Override
        public int getItemCount() {            return orderUserShop.size();        }
    }

     public void startImageActivity(String shopId , String key) {

         HomeActivity.mDataBase.getReference("item" + shopId).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 inn = dataSnapshot.getValue(Inventory.class);
                 ImageActivity.invent = inn;
                 Intent intent = new Intent(getActivity(), ImageActivity.class);
                 startActivity(intent);
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
     }
}


