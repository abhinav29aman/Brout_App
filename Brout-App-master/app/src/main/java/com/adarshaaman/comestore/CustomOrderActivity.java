package com.adarshaaman.comestore;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CustomOrderActivity extends AppCompatActivity {
    public static Order i;
    public int state; // o in user making list , 1 when Inventory is sent to shop for shop , 2 is such state for user , 3 when Inventory comes back for user , 4 for shop  ,5 when order is placed
    // rider and shop have same view for now
    public ArrayList<String> items = new ArrayList<String>();
    public static Shop sh;
    public static String shopToken;
    public int whichAddress;

    public static int getState() {

        if (i == null) {
            return 0;
        } else if (i.getType() == 1) {
            if (i.getUser().getContact().equals(HomeActivity.userPhone)) {
                return 2;
            } else {
                return 1;
            }

        }
        if (i.getType() == 2) {
            if (i.getUser().getContact().equals(HomeActivity.userPhone)) {
                return 3;
            } else {
                return 4;
            }
        }
        if (i.getType() == 3) {
            return 5;
        }
        return 0;
    }

    public static int priceForOne(int position) {
        String s = (i.getInventoryKey().split("#"))[position];
        String[] arr = s.split("@");
        int price = Integer.parseInt(arr[2].trim());
        int number = Integer.parseInt(arr[1].trim());
        return ((int) (price / number));

    }

    public static int totalBill(String str){
        ArrayList<String> item = convertToArray(str);
        int ret =0;
        for(int i=0; i<item.size();i++){
            ret = ret + getPrice(item.get(i));

        }
        return ret;
    }

    //# separates items and @ separates item name from price and number --  itemNameOne@2@120#itemNameTwo@1@342#.........

    public static ArrayList<String> convertToArray(String s) {
        String[] lis = (s.split("#"));
        ArrayList<String> ret = new ArrayList<String>();
        int n = lis.length;
        for (int i = 0; i < n; i++) {
            ret.add(lis[i]);
        }

        return ret;

    }


    //used to determine if shop has set all the prices
    public boolean allPriceSet() {
        for (int i = 0; i < items.size(); i++) {

            String[] arr = items.get(i).split("@");
            if (!(arr.length == 3)) {
                return false;
            }
        }
        return true;
    }

    public String setPrice(String s, String p) {
        String[] arr = s.split("@");
        int n = 0;
        String ret = "";
        try {
            n = Integer.parseInt(p.trim());
        } catch (Exception e) {
            Toast.makeText(CustomOrderActivity.this, "Invalid price ! Only number is needed", Toast.LENGTH_SHORT).show();
            return "";
        }
        if (arr.length == 2) {
            ret = s + "@" + n;
        }

        if (arr.length == 3) {
            ret = arr[0] + "@" + arr[1] + "@" + n;
        }
        return ret;
    }


    public static String convertToString(ArrayList<String> arr) {
        String ret = "";
        int m = arr.size();
        for (int i = 0; i < (m - 1); i++) {

            String[] aw = arr.get(i).split("@");
            if (!(getNumber(arr.get(i)) == 0)) {
                if (aw.length == 2) {
                    ret = ret + arr.get(i) + "#";
                }
                if (aw.length == 3) {
                    if (!(getPrice(arr.get(i)) == 0)) {
                        ret = ret + arr.get(i) + "#";
                    }
                }
            }
        }
        String[] am = arr.get(m - 1).split("@");
        if (!(getNumber(arr.get(m - 1)) == 0)) {
            if (am.length == 2) {
                ret = ret + arr.get(m - 1) ;
            }
            if (am.length == 3) {
                if (!(getPrice(arr.get(m - 1)) == 0)) {
                    ret = ret + arr.get(m - 1) ;
                }
            }
        }

 return ret;
    }




    public static int getNumber (String s){
        String[] arr = s.split("@");
        try{
        return (Integer.parseInt(arr[1]));}
        catch (Exception e){return  0;}
    }

    public static int getPrice(String s){
        String[] arr = s.split("@");
        try{
            return (Integer.parseInt(arr[2]));}
        catch (Exception e){return  0;}}

    public static String getName(String s){
        String[] arr = s.split("@");
        return  arr[0] ;
    }

    public static String increaseNumber(String s,int position){
        String[] arr = s.split("@");
        String ret = "";
        if(arr.length==2){
            int m = Integer.parseInt (arr[1]);
            m= m+1;
            ret = arr[0] + "@" + m ;
        }
        if (arr.length ==3){
            int m = Integer.parseInt (arr[1]);
            int p = Integer.parseInt (arr[2]); //keep prices in integer

            int priceOne = priceForOne(position);
            m = m+1;
            p = priceOne * m;
            ret = arr[0] + "@" + m + "@" + p;

        }

        return ret;

    }

    public static String decreaseNumber(String s,int position){
        String[] arr = s.split("@");
        String ret = "";
        int m = Integer.parseInt (arr[1]);
       if(!(m==0)) {if(arr.length==2){

            m= m-1;
            ret = arr[0] + "@" + m ;
        }
        if (arr.length ==3){
            int p = Integer.parseInt (arr[2]); //keep prices in integer

            int priceOne = priceForOne(position);
            m = m-1;
            p = priceOne * m;
            ret = arr[0] + "@" + m + "@" + p;

        }}
       else{ret = s;}

        return ret;
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_activity);
        final ViewGroup orderlayout = ((ViewGroup)(findViewById(R.id.order_layout1)));
        final EditText addressText = (EditText) (findViewById(R.id.ettt1));
        final Button order = (Button) (findViewById(R.id.iorder11));
        final Button currentAd = (Button) (findViewById(R.id.location_current1));
        final Button lastUsedAd = (Button) (findViewById(R.id.location_last1));
        final Button orderList = (Button) findViewById(R.id.order_cart);
        final EditText writeItem = (EditText) findViewById(R.id.write_item);
        final Button addItem = (Button) findViewById(R.id.add_item);
        final Button send = (Button) findViewById(R.id.send_list);
       final  View fill  = findViewById(R.id.fill_item);
       fill.setVisibility(View.GONE);


        state = getState(); //obtain state for loading view type

        addressText.setVisibility(View.GONE);
        order.setVisibility(View.GONE);
        orderlayout.setVisibility(View.GONE);
        //------------------------------------------------------------------------------------------
        currentAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = ImageActivity.getAddress(HomeActivity.currentLocation.getLatitude(),HomeActivity.currentLocation.getLongitude(),CustomOrderActivity.this);
                addressText.setVisibility(View.VISIBLE);
                order.setVisibility(View.VISIBLE);
                addressText.setText(address);
                whichAddress = 2;
            }
        });

        lastUsedAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = ImageActivity.getAddress(HomeActivity.user.getLatitude(),HomeActivity.user.getLongitude(),CustomOrderActivity.this);
                addressText.setVisibility(View.VISIBLE);
                order.setVisibility(View.VISIBLE);
                addressText.setText(address);
                whichAddress = 3;
            }
        });


        if (!(state ==0)){ items = convertToArray(i.getInventoryKey());} //inventorykey saves the codified string
        RecyclerView rv = (RecyclerView)findViewById(R.id.recyclerview1);
        rv.setLayoutManager(new LinearLayoutManager(this));
        final ListAdapter ad = new ListAdapter();
        rv.setAdapter(ad);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = writeItem.getText().toString();
              if (!(BusinessWriteActivity.isEmptyString(s))){
                items.add(s+"@1");
                send.setVisibility(View.VISIBLE);
                //notifying dataset changed
                  ad.notifyDataSetChanged();
                  writeItem.setText("");
              }

            }
        });







        if(state==0){

            orderList.setVisibility(View.GONE);
            if(items.size()==0){send.setVisibility(View.GONE);
            fill.setVisibility(View.VISIBLE);}

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (state == 0) {
                        final int radom = (int) (Math.random() * 9000 + 1000);

                        final User u1 = new User(HomeActivity.userPhone, 0.00, 0.00);
                        DatabaseReference ref = HomeActivity.mDataBase.getReference("Orders").push();
                        String key = ref.getKey();
                        Order o = new Order( convertToString(items), "Custom List", sh, 0, 0, "sent for pricing", HomeActivity.rider, radom, u1, key, "noPolicy", HomeActivity.userToken, shopToken, HomeActivity.riderToken, 1);
                        ref.setValue(o);
                        HomeActivity.mDataBase.getReference("shopOrder"+sh.getContact()).child(key).setValue(o);
                        HomeActivity.mDataBase.getReference("userOrder" + HomeActivity.userPhone).child(key).setValue(o);
                        Toast.makeText(CustomOrderActivity.this, "List sent", Toast.LENGTH_SHORT).show();

                        finish();
                    }
                }

            });

        }

        if(state ==1){
            orderList.setVisibility(View.GONE);

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(allPriceSet()){
                        String key = i.getOrderKey();
                        i.setInventoryKey(convertToString(items));
                        i.setType(2);
                        i.setStatus("priced by shop");
                         HomeActivity.mDataBase.getReference("Orders").child(key).setValue(i);
                        HomeActivity.mDataBase.getReference("shopOrder" + i.getShop().getContact()).child(key).setValue(i);
                        HomeActivity.mDataBase.getReference("userOrder" + i.getUser().getContact()).child(key).setValue(i);
                        Toast.makeText(CustomOrderActivity.this, "List sent", Toast.LENGTH_SHORT).show();
                          finish();

                    }
                    else {Toast.makeText(CustomOrderActivity.this,"All prices not filled. Can't send.",Toast.LENGTH_SHORT).show();}
                }
            });

        }
        if (state ==2){
            send.setVisibility(View.GONE);
            orderList.setVisibility(View.GONE);


        }
        if (state==3){
            send.setVisibility(View.GONE);
            orderList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   orderlayout.setVisibility(View.VISIBLE);
                }
            });

            order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User use= new User();
                    if(whichAddress==2){use= new User(HomeActivity.userPhone, HomeActivity.currentLocation.getLatitude(), HomeActivity.currentLocation.getLongitude());
                        HomeActivity.mDataBase.getReference("user" + HomeActivity.userPhone).setValue(use);}
                    else if(whichAddress==3){use = HomeActivity.user;}
                    i.setUser(use);
                    String str= convertToString(items);
                    i.setInventoryKey(str);
                    i.setTotalPrice(totalBill(str));
                    i.setStatus("ordered");
                    i.setType(3);
                    String key = i.getOrderKey();

                    HomeActivity.mDataBase.getReference("Orders").child(key).setValue(i);
                    HomeActivity.mDataBase.getReference("shopOrder" + i.getShop().getContact()).child(key).setValue(i);
                    HomeActivity.mDataBase.getReference("userOrder" + i.getUser().getContact()).child(key).setValue(i);

                    Toast.makeText(CustomOrderActivity.this, "Ordered", Toast.LENGTH_SHORT).show();
                    finish();

                }
            });
        }


        if (state==4){send.setVisibility(View.GONE);
            orderList.setVisibility(View.GONE); }
        if(state==5){
            send.setVisibility(View.GONE);
            orderList.setVisibility(View.GONE);
        }

    }

    public class Listholder extends RecyclerView.ViewHolder{
        public Listholder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.custom_order,parent,false));


        }
    }

    public class ListAdapter extends RecyclerView.Adapter<Listholder>{

        public ListAdapter(){}
        public Listholder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(CustomOrderActivity.this);
            Listholder i = new Listholder(inflater,parent); return i;

        }
        @Override
        public void onBindViewHolder(Listholder holder, final int position) {
        final View v = holder.itemView;
         String content = items.get(position);
            final ImageButton add =(ImageButton) v.findViewById(R.id.add2);
            final ImageButton minus =(ImageButton) v.findViewById(R.id.minus2);
            final TextView title = (TextView) v.findViewById(R.id.order_title2);
            final TextView number = (TextView) v.findViewById(R.id.order_quantity2);
            final TextView price = (TextView) v.findViewById(R.id.order_price2);
            final EditText priceEdit = (EditText) v.findViewById(R.id.price_shop);
            final Button setPrice = (Button) v.findViewById(R.id.set_price);

             setPrice.setVisibility(View.GONE);
            priceEdit.setVisibility(View.GONE);

            int m = getNumber(content);
            String name = getName(content);
            title.setText(name);
            number.setText("Qty : " + m);

            if(state == 0|| state==3){
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = items.get(position);
                        s = increaseNumber(s,position);
                        int m = getNumber(s);
                        items.set(position,s);
                        number.setText("Qty : " + m);
                        if(state == 3){ int p = getPrice(s);
                         price.setText("Rs "+ p);
                         }

                    }
                });


                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = items.get(position);
                        s = decreaseNumber(s,position);
                        int m = getNumber(s);
                        items.set(position,s);
                        number.setText("Qty : " + m);
                        if(state == 3){ int p = getPrice(s);
                            price.setText("Rs "+ p);
                        }




                    }
                });

            }
            if(state==0){
                price.setVisibility(View.GONE);

            }

            if(state==1){
                price.setVisibility(View.GONE);
                setPrice.setVisibility(View.VISIBLE);
                priceEdit.setVisibility(View.VISIBLE);
                add.setVisibility(View.GONE);
                minus.setVisibility(View.GONE);

                setPrice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View vv) {
                        String pp = priceEdit.getText().toString();
                        String m = setPrice(items.get(position),pp );
                        if(!(m.equals(""))){ items.set(position,m);
                        v.setBackgroundColor(0xF25278);}
                    }
                });

            }

            if(state ==2){ add.setVisibility(View.GONE); minus.setVisibility(View.GONE);
            price.setVisibility(View.GONE);
            }

            if(state==3){  String str = items.get(position);
            price.setText("Rs " +getPrice(str));}

            if(state ==4){ price.setVisibility(View.GONE);
            add.setVisibility(View.GONE);
                minus.setVisibility(View.GONE);}

            if(state==5){ add.setVisibility(View.GONE);
            minus.setVisibility(View.GONE);
                String str = items.get(position);
                price.setText("Rs " +getPrice(str));}
            }



        @Override
        public int getItemCount() {            return items.size();        }
    }




}
