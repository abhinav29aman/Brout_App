package com.adarshaaman.comestore;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;


public class BusinessWriteActivity extends AppCompatActivity {
    public static boolean isInStock = false;
    EditText shopname;
    EditText shopCateg;
    EditText shopAddress1;
    EditText shopAdress2;
    EditText itemTitle;
    EditText itemDes1;
    EditText itemDes2;
    EditText itemDes3;
    EditText itemprice;
    Button postShop;
    Button postItem;
    Button postAd;
    public static String shopUrl;
    public static String itemUrl;
    private static final int RC_SHOP_PHOTO = 5; EditText shopAddress1;
    EditText shopAdress2;
    EditText itemTitle;
    EditText itemDes1;
    EditText itemDes2;
    EditText itemDes3;
    EditText itemprice;
    Button postShop;
    Button postItem;
    Button postAd;
    public static String shopUrl;
    public static String itemUrl;
    private static final int RC_SHOP_PHOTO = 5;
    private static final int RC_ITEM_PHOTO = 6;
    private static final int RC_SHOP_PHOTOS = 7;


    //String methods for whole application
    //---------------------------------------------------------------------------------------------------------------

    public static boolean isEmptyString(String s) {
        if (s == (null)) {
            return true;
        }
        int m = s.length();
        if (s.equals("")) {
            return true;
        }
        for (int i = 0; i < m; i++) {
            if (!(s.charAt(i) == ' ')) {
                return false;
            }
        }
        return true;
    }

    public static String getRestString(String s) {
        int n = s.length();
        String ret = "";

        for (int i = 1; i < n; i++) {
            ret = ret + s.charAt(i);
        }
        return ret;
    }

    public static boolean isMember(ArrayList<String> arr, String s) {

        int m = arr.size();
        if (m == 0) {
            return false;
        }
        for (int i = 0; i < m; i++) {

            if (arr.get(i).equals(s)) {
                return true;
            }
        }
        return false;
    }

    public static void deleteString(ArrayList<String> arr, String s) {
        int m = arr.size();
        if (m == 0) {
            return;
        }
        for (int i = 0; i < m; i++) {

            if (arr.get(i).equals(s)) {
                arr.remove(i);
                return;
            }
        }


    }

    public static String giveRandom(ArrayList<String> arr){
        int n = arr.size();
        if(n==0){return  "";}
        int m = (int)(n* Math.random());
        return arr.get(m);

    }

    //to remove spaces from the beginning and end in a string in categories// not needed trim() is already


    //------------------------------------------------------------------------------------------------------------
    // maps permissions and check methods




    //------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_business);
        shopname = (EditText)findViewById(R.id.store_name);
        shopCateg = (EditText)findViewById(R.id.store_category);
        shopAddress1 =(EditText)findViewById(R.id.store_addressLine1);
        shopAdress2= (EditText)findViewById(R.id.store_addressLine2);
        itemTitle = (EditText)findViewById(R.id.item_title);
        itemDes1 =(EditText)findViewById(R.id.item_description1);
        itemDes2 = (EditText)findViewById(R.id.item_description2);
        itemDes3 =       (EditText)findViewById(R.id.item_description3);
        final CheckBox cb  = (CheckBox) findViewById(R.id.instock);
        final RadioGroup rg = (RadioGroup) findViewById(R.id.radiogroup);
        final CheckBox cb1 = (CheckBox) findViewById(R.id.isService);
        itemprice = (EditText)findViewById(R.id.item_price);
        postShop= (Button) findViewById(R.id.post_store);
        postItem = (Button) findViewById(R.id.post_item);
        postAd = (Button) findViewById(R.id.post_ad);
        final Button location = (Button) findViewById(R.id.shop_location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(BusinessWriteActivity.this);
                builder.setMessage("Do you really want to set the current location as shop location?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                final Location lastlocation = HomeActivity.currentLocation;

                                if (lastlocation != null) {
                                    double lat = lastlocation.getLatitude();
                                    double lon = lastlocation.getLongitude();
                                    DatabaseReference ref1 = HomeActivity.mDataBase.getReference("id" + HomeActivity.userPhone);
                                    ref1.child("latitude").setValue(lat);
                                    ref1.child("longitude").setValue(lon);
                                    DatabaseReference ref2 = HomeActivity.mCategoryBase.child(HomeActivity.userShop.getCategory()).child(HomeActivity.userPhone);
                                    ref2.child("latitude").setValue(lat);
                                    ref2.child("longitude").setValue(lon);
                                    Toast.makeText(BusinessWriteActivity.this, "Location Added", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(BusinessWriteActivity.this, "No location available", Toast.LENGTH_SHORT).show();
                                }
                            }


                        });
                final AlertDialog alert = builder.create();
                alert.show();


            }});

        ImageButton i1 = (ImageButton)findViewById(R.id.shop_photo);
        ImageButton i2 = (ImageButton)findViewById(R.id.inventory_photo);
        ImageView i3 = (ImageView) findViewById(R.id.add_shop_images) ;

        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(intent,"Complete Action Using"), RC_SHOP_PHOTO);

            }
        });

        i2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(intent,"Complete Action Using"), RC_ITEM_PHOTO);
            }
        });

        i3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BusinessWriteActivity.this);
                builder.setMessage("Are you sure you want to add an image to your shop?").setTitle("Add image").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/jpeg");
                        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                        startActivityForResult(Intent.createChooser(intent,"Complete Action Using"), RC_SHOP_PHOTOS);


                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });



                builder.create().show();
            }
        });



 postShop.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View v) {
         try {
             String sn = shopname.getText().toString();
             String sc = shopCateg.getText().toString().trim();
             String sad1 = shopAddress1.getText().toString();
             String sad2 = shopAdress2.getText().toString();
             if (sn.charAt(0) == '1') {
                 Shop s = new Shop(getRestString(sn), sc, 0, sad1, sad2, HomeActivity.userPhone, shopUrl,0.00,0.00);
                 if (!(isEmptyString(s.getPhotourl()))) {
                     HomeActivity.mDataBase.getReference().child("id" + HomeActivity.userPhone).setValue(s);
                     if (!(isEmptyString(sc))) {
                         HomeActivity.mCategoryBase.child(sc).child(HomeActivity.userPhone).setValue(s);

                         HomeActivity.mCategListBase.child(sc).setValue("truefalse");
                         Toast.makeText(getApplicationContext(), "Shop Posted ", Toast.LENGTH_SHORT).show();
                     }
                 }
             }
         } catch (Exception e){}}
 });
  postItem.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          try{
          if (itemTitle.getText().toString().charAt(0)=='1'){
           String returnPolicy = ((RadioButton)(v.getRootView().findViewById(rg.getCheckedRadioButtonId()))).getText().toString();
           isInStock = cb.isChecked();
           boolean isService = false;
           isService = cb1.isChecked();
          Inventory i = getInventory(false,returnPolicy,isInStock,isService);

          if(!(isEmptyString(i.getImageurl()))){
      DatabaseReference ref =    HomeActivity.mDataBase.getReference().child("item"+(HomeActivity.userPhone)).push();
          i.setKey(ref.getKey());
          ref.setValue(i);
              Toast.makeText(getApplicationContext(),"Item Posted ",Toast.LENGTH_SHORT).show();
          }
      }}catch(Exception e){}}
  });

  postAd.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          try{
          if (itemTitle.getText().toString().charAt(0)=='1'){
              String returnPolicy = ((RadioButton)(v.getRootView().findViewById(rg.getCheckedRadioButtonId()))).getText().toString();
              isInStock = cb.isChecked();
              boolean isService = false;
              isService = cb1.isChecked();
              Inventory i = getInventory(true,returnPolicy,isInStock,isService);
          if(!(isEmptyString(i.getImageurl())))
          {DatabaseReference ref =HomeActivity.mDataBase.getReference().child("item"+(HomeActivity.userPhone)).push();
                 String r= ref.getKey();
                 i.setKey(r);
                 HomeActivity.mAdBase.child(r).setValue(i);
          ref.setValue(i);
              Toast.makeText(getApplicationContext(),"Ad Posted ",Toast.LENGTH_SHORT).show();}
      }} catch (Exception e){}}
  });

  final EditText timings = (EditText)findViewById(R.id.et);
  final EditText details = (EditText) findViewById(R.id.et2);


  Button detailButton = (Button)findViewById(R.id.detail_button);
  detailButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          String times = timings.getText().toString();
          String detail = details.getText().toString();
          if (times.charAt(0)=='1'){
              ShopDetail sd = new ShopDetail(getRestString(times),detail);
              HomeActivity.mDataBase.getReference("shopDetails"+HomeActivity.userPhone).setValue(sd);


          }


      }
  });

    }

    public Inventory getInventory(boolean isAd,String returnPolicy ,boolean isInStock,boolean isService){
        String it = getRestString(itemTitle.getText().toString());
        String id1 = itemDes1.getText().toString();
        String id2 = itemDes2.getText().toString();
        String id3 = itemDes3.getText().toString();
        int price =  Integer.parseInt(itemprice.getText().toString());
        Inventory i = new Inventory(it,(id1+" "+ " "+id2+" "+id3),price,0,itemUrl,HomeActivity.userPhone,"",isAd,0,isInStock,returnPolicy,isService);
        return i;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
   if (requestCode == RC_ITEM_PHOTO  && resultCode == RESULT_OK){
       Uri imageUri = data.getData();
       final StorageReference photoReference = HomeActivity.itemStorage.child(imageUri.getLastPathSegment());
      UploadTask uploadTask=  photoReference.putFile(imageUri);
       Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
           @Override
           public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
               if (!task.isSuccessful()) {
                   throw task.getException();
               }

               // Continue with the task to get the download URL
               return photoReference.getDownloadUrl();
           }
       }).addOnCompleteListener(new OnCompleteListener<Uri>() {
           @Override
           public void onComplete(@NonNull Task<Uri> task) {
               if (task.isSuccessful()) {
                   Uri downloadUri = task.getResult();
                   itemUrl = downloadUri.toString();
                   Toast.makeText(getApplicationContext(),"Image Loaded",Toast.LENGTH_SHORT).show();
               }
           }
       });
   }
   else if (requestCode == RC_SHOP_PHOTO && resultCode==RESULT_OK){

       Uri imageUri = data.getData();
       final StorageReference photoReference = HomeActivity.itemStorage.child(imageUri.getLastPathSegment());
       UploadTask uploadTask=  photoReference.putFile(imageUri);
       Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
           @Override
           public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
               if (!task.isSuccessful()) {
                   throw task.getException();
               }

               // Continue with the task to get the download URL
               return photoReference.getDownloadUrl();
           }
       }).addOnCompleteListener(new OnCompleteListener<Uri>() {
           @Override
           public void onComplete(@NonNull Task<Uri> task) {
               if (task.isSuccessful()) {
                   Uri downloadUri = task.getResult();
                   shopUrl = downloadUri.toString();
                   Toast.makeText(getApplicationContext(),"Image Loaded",Toast.LENGTH_SHORT).show();
               }
           }
       });


   }
   else if(requestCode == RC_SHOP_PHOTOS && resultCode==RESULT_OK){

       Uri imageUri = data.getData();
       final StorageReference photoReference = HomeActivity.itemStorage.child(imageUri.getLastPathSegment());
       UploadTask uploadTask=  photoReference.putFile(imageUri);
       Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
           @Override
           public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
               if (!task.isSuccessful()) {
                   throw task.getException();
               }

               // Continue with the task to get the download URL
               return photoReference.getDownloadUrl();
           }
       }).addOnCompleteListener(new OnCompleteListener<Uri>() {
           @Override
           public void onComplete(@NonNull Task<Uri> task) {
               if (task.isSuccessful()) {
                   Uri downloadUri = task.getResult();
                   shopUrl = downloadUri.toString();
                   HomeActivity.mDataBase.getReference("ShopImages"+ HomeActivity.userPhone).push().setValue(shopUrl);
                   Toast.makeText(getApplicationContext(),"Image Added" , Toast.LENGTH_SHORT).show();

               }
           }
       });


   }


         } catch (Exception e){}
    }
}
