package com.adarshaaman.comestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;

public class HomeActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
   private FirebaseAuth auth ;
   public static  String userPhone;
   public static  FirebaseDatabase mDataBase;

   public static  DatabaseReference mShopsIdBase;
   public  static  DatabaseReference mCategoryBase;
   public static DatabaseReference mAdBase;
   public static DatabaseReference mCategListBase;
   public static FirebaseStorage mFireBaseStorage;
   public static StorageReference shopStorage;
   public static  StorageReference itemStorage;
   public  static ArrayList<String> followShops = new ArrayList<String>();
   public static ArrayList<String> favourites = new ArrayList<String>();


   public static Location currentLocation;
   public boolean mLocationPermissionGranted = false;
   public final static int PERMISSIONS_REQUEST_ENABLE_GPS = 8008;
    public final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =8009;

    private static final int ERROR_DIALOG_REQUEST = 30;
    public static FusedLocationProviderClient fused;
    private static LocationRequest locationRequest;
    private static LocationCallback callback;
    public static int userType=1;  // 1 for normal user , 2 for business , 3 for rider
    public static Shop userShop ;
    public static User user;
    public static boolean isRider = false;
    public static ArrayList<String> riderList =new ArrayList<String>();
    public static String userToken;
    public static  String rider;
    public static  String riderToken;



   //-------------------------------------------------------------------------------------------------------------------
    //Location methods

    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled() {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        try{  if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }} catch (Exception e){                            Toast.makeText(HomeActivity.this,"ERROR",Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isServicesOK() {

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests

            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it

            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        try{
            mLocationPermissionGranted = false;
            switch (requestCode) {
                case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = true;
                    }
                }
            }} catch (Exception e){Toast.makeText(HomeActivity.this,"ERROR2",Toast.LENGTH_SHORT);}
    }


    public void getLocation() {
        if (checkMapServices()) {

            if (mLocationPermissionGranted) {
                startLocationUpdates();
            }

            else{ getLocationPermission();}
        }


    }

    public void startLocationUpdates (){

        fused.requestLocationUpdates(locationRequest,callback,null);

    }
    public void stopLocationUpdates(){

        fused.removeLocationUpdates(callback);
    }




    //-------------------------------------------------------------------------------------------------------------------



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();


// check sign in
        //----------------------------------------------------------------------------------------------
       if (auth.getCurrentUser() != null) {

            // already signed in

            //initialise phone number
            //---------------------------------------------------------------------------------------------

            userPhone = auth.getCurrentUser().getPhoneNumber();



            //--------------------------------------------------------------------------------------------
            //initialising location sysytems

            fused = LocationServices.getFusedLocationProviderClient(this);
            locationRequest = LocationRequest.create();
            locationRequest.setInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            callback = new LocationCallback(){

                public void onLocationResult(LocationResult locationResult){
                    Location lastlocation =  locationResult.getLastLocation();
                    if(lastlocation != null){
                        currentLocation = lastlocation;
                        Toast.makeText(HomeActivity.this, "Current Location Ready", Toast.LENGTH_SHORT).show();
                        stopLocationUpdates();

                    }
                }
            }
            ;

            getLocation();

            //initialise database references
            //------------------------------------------------------------------------------------------------

            mDataBase = FirebaseDatabase.getInstance();

            mShopsIdBase = mDataBase.getReference("ShopsIds");
            mAdBase = mDataBase.getReference("Ads");
            mCategoryBase = mDataBase.getReference("Categories");

            mCategListBase = mDataBase.getReference("Categ");
            mFireBaseStorage = FirebaseStorage.getInstance();
            shopStorage = mFireBaseStorage.getReference("ShopPhotos");
            itemStorage = mFireBaseStorage.getReference("ItemPhotos");

            //-----------------------------------------------------------------------------------
              //user token genration

           FirebaseInstanceId.getInstance().getInstanceId()
                   .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                       @Override
                       public void onComplete(@NonNull Task<InstanceIdResult> task) {
                           if (!task.isSuccessful()) {

                               return;
                           }

                           // Get new Instance ID token
                           String token = task.getResult().getToken();
                           userToken = token;

                           // Log and toast
                           mDataBase.getReference("userToken"+ userPhone).setValue(token);
                       }
                   });

            //--------------------------------
            //initialising Riders
            mDataBase.getReference("Riders").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String  r = postSnapshot.getKey();
                    riderList.add(r);

                }
                  if (BusinessWriteActivity.isMember(riderList,userPhone)){ isRider = true;}
                   rider = BusinessWriteActivity.giveRandom(HomeActivity.riderList);
                  HomeActivity.mDataBase.getReference("userToken"+rider).addListenerForSingleValueEvent(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnap) {
                          riderToken = dataSnap.getValue(String.class);
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


            //--------------------------------
            //initialising usershop

            mDataBase.getReference("id"+ userPhone).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userShop = dataSnapshot.getValue(Shop.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //-------------------------------------------------------------------------------------------------------
            //initialising user

            mDataBase.getReference("user"+ userPhone).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user= dataSnapshot.getValue(User.class);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //initialise followed shop list
            //---------------------------------------------------------------------------------
            mDataBase.getReference("followShops" + userPhone).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    followShops.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String s = postSnapshot.getKey();
                        String t = postSnapshot.getValue(String.class);
                        if (t.equals("follow")) {
                            followShops.add(s);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            mDataBase.getReference("favourites" + userPhone).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    favourites.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String s = postSnapshot.getKey();


                            favourites.add(s);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            //set up layout and viewpager fragments
            //----------------------------------------------------------------------------------------------------


            setContentView(R.layout.activity_home);


   final Fragment ff= new HomeFragment();

getSupportFragmentManager().beginTransaction().replace(R.id.home_framelayout,ff).commit();


            BottomNavigationView bb= (BottomNavigationView) findViewById(R.id.bottom_navigaton);

            bb.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment= ff;
                    switch (menuItem.getItemId()){
                        case R.id.nav_home :
                            selectedFragment = ff;
                            break;
                        case R.id.nav_favourite :
                            selectedFragment = new ItemFragment(3,"");
                             break;

                        case R.id.nav_cart : if (isRider){selectedFragment = new OrderFragment(1);}
                                            else if (!(userShop== null)){ selectedFragment = new OrderFragment(2);}
                                               else {selectedFragment = new OrderFragment(3);}
                             break;
                         default: break;

                    }  HomeActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.home_framelayout,selectedFragment).commit();
                return true;}
            });


    } else {
         startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                 AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build())).build(),
                    RC_SIGN_IN);
            // not signed in


        }


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){finish();
        moveTaskToBack(true);
        }
        if (requestCode== PERMISSIONS_REQUEST_ENABLE_GPS){
            final LocationManager manager1 = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if (manager1.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if(mLocationPermissionGranted){ getLocation(); } else {getLocationPermission();}

            }else{Toast.makeText(HomeActivity.this,"Can't get location without GPS",Toast.LENGTH_SHORT).show();}
        }

    }
}

