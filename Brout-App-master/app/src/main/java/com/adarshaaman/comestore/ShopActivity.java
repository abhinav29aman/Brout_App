package com.adarshaaman.comestore;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class ShopActivity extends AppCompatActivity {
    public static String shopContact;
    public static Shop shop;
    public static ArrayList<String> shopImages ;
    public static  ImageAdapter classImageAdapter ;


    //Note __  A very good way to add data fields to user  can be having seperate children of main database with user ids
// like i did for shops that users follow
    //gonna do the same for shop description as it is not needed on  other views of shops like shop list
    // so for now we need to change just the shop object by writing location
    // other info can be optional to input while creating a shop and will be loaded when required





    public void onCreate(Bundle savedInstanceState) {
        shopImages = new ArrayList<String>();
        shopImages.add(shop.getPhotourl());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        final TextView followers = (TextView)findViewById(R.id.followers_text);
        shopContact = shop.getContact();
        classImageAdapter = new ImageAdapter(this);


        //handles follow buttons and their action
        //---------------------------------------------------------------------------------------------------------
        final Button customOrder= (Button)findViewById(R.id.custom_order);
        final Button follow = (Button)(findViewById(R.id.follow_button));
        final Button following = (Button)(findViewById(R.id.following_button));
        final Button directions = (Button)findViewById(R.id.get_direction);

        customOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.mDataBase.getReference("userToken"+shopContact).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String t = dataSnapshot.getValue(String.class);
                        Intent i = new Intent(ShopActivity.this, CustomOrderActivity.class);
                        CustomOrderActivity.i = null;
                        CustomOrderActivity.shopToken= t;
                        CustomOrderActivity.sh = shop;
                        startActivity(i);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri navigation = Uri.parse("google.navigation:q="+shop.getLatitude()+","+shop.getLongitude()+"");
                Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
                navigationIntent.setPackage("com.google.android.apps.maps");
                startActivity(navigationIntent);
            }
        });

        if (BusinessWriteActivity.isMember(HomeActivity.followShops,shopContact))
        {follow.setVisibility(View.GONE);}
        else {following.setVisibility(View.GONE);}
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                following.setVisibility(View.VISIBLE);
                follow.setVisibility(View.GONE);
                int m = shop.getFollowers() +1;
                followers.setText(m+"");
                shop.setFollowers(m);
                HomeActivity.mCategoryBase.child(shop.getCategory()).child(shopContact).child("followers").setValue(m);
                HomeActivity.mDataBase.getReference().child("id"+shopContact).child("followers").setValue(m);
                HomeActivity.mDataBase.getReference().child("followShops"+ HomeActivity.userPhone).child(shopContact).setValue("follow");
// this followShops of user stores it's followed shop numbers

            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follow.setVisibility(View.VISIBLE);
                following.setVisibility(View.GONE);
                int m = shop.getFollowers() -1;
                followers.setText(m+"");
                shop.setFollowers(m);
                BusinessWriteActivity.deleteString(HomeActivity.followShops,shopContact);
                HomeActivity.mCategoryBase.child(shop.getCategory()).child(shopContact).child("followers").setValue(m);
                HomeActivity.mDataBase.getReference().child("id"+shopContact).child("followers").setValue(m);
                HomeActivity.mDataBase.getReference().child("followShops"+ HomeActivity.userPhone).child(shopContact).setValue("unfollow");


            }
        });

        //handles expansion and contraction of description
        //--------------------------------------------------------------------------------------------------
        final ImageView es = (ImageView)(findViewById(R.id.expand_shop));
       final ImageView cs = (ImageView)(findViewById(R.id.contract_shop)) ;
       final View shopExpand = (View)(findViewById(R.id.shop_expanded));
       shopExpand.setVisibility(View.GONE);
        cs.setVisibility(View.GONE);
        es.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cs.setVisibility(View.VISIBLE);
                es.setVisibility(View.GONE);
                shopExpand.setVisibility(View.VISIBLE);
            }
        });

        cs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cs.setVisibility(View.GONE);
                es.setVisibility(View.VISIBLE);
                shopExpand.setVisibility(View.GONE);
            }
        });

        //get shop contact and load calling intent
        //---------------------------------------------------------------------------------------------

        ImageView iv = (ImageView)(findViewById(R.id.call_shop));

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:"+shopContact));
                ShopActivity.this.startActivity(i);
            }
        });

        //loads shop details
        //----------------------------------------------------------------------------------





        followers.setText(shop.getFollowers()+"");

        //ImageView v = findViewById(R.id.shop_page_image);
       final ViewPager imagePager = (ViewPager)findViewById(R.id.shop_photoPager);
        imagePager.setAdapter(classImageAdapter);





        final CircleIndicator circle = (CircleIndicator)findViewById(R.id.circle_indicator);
        circle.setViewPager(imagePager);

        if (!(shop == null)) {

            ((TextView) (findViewById(R.id.shop_page_name))).setText(shop.getName());
            ((TextView) (findViewById(R.id.shop_page_address))).setText(shop.getAddressLine1()+" ," + shop.getAddressLine2());

        }


        //loads shop images urls in list
        //---------------------------------------------------------------------------------------------------

        HomeActivity.mDataBase.getReference("ShopImages"+ shopContact).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shopImages.clear();
                shopImages.add(shop.getPhotourl());
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    String s = postSnapshot.getValue(String.class);

                    shopImages.add(s);

                }
                classImageAdapter.notifyDataSetChanged();
                circle.setViewPager(imagePager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //brings in details and fills text views of the expanded linearlayout

        final TextView timeText = (TextView)findViewById(R.id.shop_time) ;
        final TextView detailText = (TextView)findViewById(R.id.shop_detail) ;

        HomeActivity.mDataBase.getReference("shopDetails"+shopContact).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ShopDetail sd = dataSnapshot.getValue(ShopDetail.class);
                timeText.setText(sd.getTimeDetail());
                detailText.setText(sd.getDetail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // brings in the fragment of items of the shop
//------------------------------------------------------------------------------------------------------------

        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.shopt_item_container);
        if (f == null) {
            f = new ItemFragment(2,shopContact);

            fm.beginTransaction().add(R.id.shopt_item_container, f).commit();

        }

    }


    class ImageAdapter extends PagerAdapter{
        Context c ;
        public ImageAdapter(Context c){this.c = c;}
        @Override
        public Object instantiateItem(ViewGroup container,int postion){
            View v = LayoutInflater.from(c).inflate(R.layout.shop_image,container,false);
            ImageView i = v.findViewById(R.id.shop_page_image);
            try {
                Glide.with(c).load(shopImages.get(postion)).into(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
            container.addView(v);
            return  v;

        }
        @Override
        public int getCount() {
            // return shopImages.size();
            return shopImages.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return  object == view;
        }

        @Override
        public void destroyItem(ViewGroup container , int position , Object object ){

            container.removeView((View)object);
        }


    }

}
