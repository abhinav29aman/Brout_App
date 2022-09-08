package com.adarshaaman.comestore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    public static View cartView ;
    public static ArrayList<Order> userCart =new ArrayList<Order>();

    final Fragment itemfragment = new ItemFragment(1,"");
    final  Fragment shopCategory = new ShopCategoryFragment();
    final Fragment account = new AccountFragment();
    FragmentPagerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FragmentManager fm = getChildFragmentManager();
        adapter= new FragmentPagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                if (position == 1) {
                    return (shopCategory);
                } else if (position == 2) {
                    return (account);
                } else {
                    return (itemfragment);
                }


            }

            @Override
            public int getCount() {
                return 3;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "Deals";
                    case 1:
                        return "Stores";
                    case 2:
                        return "Account";

                }
                return "Comestore";
            }
        };


    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();

          if(!(cartView==null)){if ((userCart.size()==0)){cartView.setVisibility(View.GONE);}else{cartView.setVisibility(View.VISIBLE);}
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_fragment,container,false);
        cartView = v.findViewById(R.id.cart_view);
        ViewPager vp = (ViewPager) v.findViewById(R.id.homeViewPager);

        //--------------------------------------Cart------------------------------------------
        HomeActivity.mDataBase.getReference("userCart" + HomeActivity.userPhone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userCart.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Order o = postSnapshot.getValue(Order.class);
                    userCart.add(o);
                }

                if(userCart.size()==0){cartView.setVisibility(View.GONE);} else{cartView.setVisibility(View.VISIBLE);}
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}

        });


        vp.setAdapter(adapter);
        TabLayout tabs = (TabLayout) (v.findViewById(R.id.main_tab));
        tabs.setupWithViewPager(vp);

        Button clear_cart = (Button) v.findViewById(R.id.clear_cart);
        Button continue_cart = (Button)v.findViewById(R.id.continue_cart);
        clear_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure? All items chosen will be deleted .")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                HomeActivity.mDataBase.getReference("userCart"+ HomeActivity.userPhone).removeValue();
                                cartView.setVisibility(View.GONE);
                            }


                        });
                final AlertDialog alert = builder.create();
                alert.show();


            }
        });

        continue_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),CartActivity.class);
                startActivity(i);
            }
        });

 return  v;
    }
}
