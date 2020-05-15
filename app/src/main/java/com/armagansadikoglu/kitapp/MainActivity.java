package com.armagansadikoglu.kitapp;

import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private FirebaseAuth mAuth;
    // static yaptım ki fragmentlarda erişebileyim
    public static double latitude;
    public static double longitude;
    public static String state;
    public static String country;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 2;
    private static final int PICK_IMAGE_REQUEST = 1;

    public static void openAppRating(Context context) {
        // you can also use BuildConfig.APPLICATION_ID
        String appId = context.getPackageName();
        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + appId));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager()
                .queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp: otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName
                    .equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                // make sure it does NOT open in the stack of your activity
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // task reparenting if needed
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                // if the Google Play was already open in a search result
                //  this make sure it still go to the app page you requested
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // this make sure only the Google Play app is allowed to
                // intercept the intent
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id="+appId));
            context.startActivity(webIntent);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        initFCM();

        // Bildirime tıklayıp geldiyse
        String menuFragment = getIntent().getStringExtra("menuFragment");


        if (menuFragment != null) {
            if (menuFragment.equals("messages")) {

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesFragment()).addToBackStack("HomeFragment").commit();

            }
        }
    }

    private void askPermissions() {
        // KONUM İLE İLGİLİ İZİN KONTROLÜ
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        )!= PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, getApplicationContext().getResources().getString(R.string.pleaseAllow), Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_LOCATION_PERMISSION);
        }else{
            getCurrentLocation();
        }


    }

    // Bildirim için gerek Firebase Cloud Messaging tokenı oluşturan fonksiyon
    public static void initFCM() {

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                Log.d("login token", "token :  " + token);
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("fcm_token").setValue(token);
                }
            }
        });


    }



    // İZİN VERİLDİYSE YAPILACAKLAR

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // && grantResults[0] == PackageManager.PERMISSION_GRANTED eklenmezse deny dediğimizde grantresults'da -1 değeri olur ve lenth değeri 0 dan büyük olacağıdndan içeri girer.
        // Bu yüzden else kısmı çalışmıyordu
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length >0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getCurrentLocation();
        }else{
            // İzin vermiyorsa izin iste sürekli

            finish(); System.exit(0);


        }



    }

    private void getCurrentLocation() {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                            .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size()>0){
                            int latestLocationIndex = locationResult.getLocations().size()-1;
                             latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                             longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                            try {
                                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                state = addresses.get(0).getAdminArea(); // şehir bu, city olsa null dönüyor
                                country = addresses.get(0).getCountryName(); // ülke
                               // Toast.makeText(MainActivity.this, "city : " + state+ " country : " + country, Toast.LENGTH_SHORT).show();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }



                        }
                    }

                }, Looper.getMainLooper());



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askPermissions();
        mAuth = FirebaseAuth.getInstance();
        //Toast.makeText(this, mAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();


        // Navigation View kodları
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Menüde itemlere tıklanılınca yapılacaklar
        NavigationView navigationView = findViewById(R.id.navigationView);

        //--- Navigation header'a erişim
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.navHeaderTextView);
        final ImageView navUserPic = headerView.findViewById(R.id.navImageView);
        // Kullanıcı profil fotosunu indirip navigationView'a koyma

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference profileImagesRef = storageRef.child(mAuth.getCurrentUser().getUid());
        profileImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Uri img = uri;
                // GLIDE DA PİCASSO DA ÇALIŞIYOR. PERFORMANS TESTLERİ YAPILIP KARAR VERİLECEK
                Glide.with(getApplicationContext()).load(img).apply(new RequestOptions().override(600, 600)).into(navUserPic);
                //Picasso.get().load(bookuri).resize(500,500).into(holder.rowPP);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        // Kullanıcı adı varsa yüklüyoruz yoksa e-mail adresini kullanıyoruz
        if (mAuth.getCurrentUser().getDisplayName() == null) {
            navUsername.setText(getString(R.string.hello, mAuth.getCurrentUser().getEmail()));
        } else {
            navUsername.setText(getString(R.string.hello, mAuth.getCurrentUser().getDisplayName()));
        }


        // Seçilen iteme tıklanıldığında açılacak fragmentler
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_menu_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).addToBackStack("HomeFragment").commit();
                        break;
                    case R.id.nav_menu_newNotice:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddNewNoticeFragment()).addToBackStack("HomeFragment").commit();
                        break;
                    case R.id.nav_menu_messages:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesFragment()).addToBackStack("HomeFragment").commit();
                        break;
                    case R.id.nav_menu_profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).addToBackStack("HomeFragment").commit();
                        break;
                    case R.id.nav_menu_cart:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ShoppingsFragment()).addToBackStack("HomeFragment").commit();
                        break;
                    case R.id.nav_menu_forum:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ForumFragment()).addToBackStack("HomeFragment").commit();
                        break;
                    case R.id.nav_menu_star:
                        openAppRating(getApplicationContext());
                        break;
                    case R.id.nav_menu_logout:
                        // Kullanıcıyı logged out yapıp login sayfasına attık
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        if (savedInstanceState == null) {
            // Açılırken Home sayfasını aç
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            // navigation menüde home seçili olsun
            navigationView.setCheckedItem(R.id.nav_menu_home);
        }

    }

    // Geri tuşuna basıldığında yapılacaklar
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }


    }


}
