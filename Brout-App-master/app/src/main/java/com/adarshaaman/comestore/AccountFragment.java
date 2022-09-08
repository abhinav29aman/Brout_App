package com.adarshaaman.comestore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AccountFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
         View v = inflater.inflate(R.layout.account_user,container,false);

        Button business = (Button) v.findViewById(R.id.businessButton);
        business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),BusinessWriteActivity.class);
                startActivity(i);
            }
        });
        final View vv = v.findViewById(R.id.rider_verification);
       final  EditText code = (EditText) v.findViewById(R.id.rider_code);
       final  Button request = (Button) v.findViewById(R.id.rider_request);
       request.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (code.getText().toString().trim().equals("510")){
                   HomeActivity.mDataBase.getReference("Riders").child(HomeActivity.userPhone).setValue("truefalse");
                   Toast.makeText(getActivity(),"Request Accepted",Toast.LENGTH_SHORT).show();
               }
               else{Toast.makeText(getActivity(),"Request denied",Toast.LENGTH_SHORT).show();}
               vv.setVisibility(View.GONE);
           }
       });


        vv.setVisibility(View.GONE);
        Button rider = (Button) v.findViewById(R.id.riderButton);
        rider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vv.setVisibility(View.VISIBLE);


            }
        });
        return v;
    }
}
