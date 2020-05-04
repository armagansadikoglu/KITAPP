package com.armagansadikoglu.kitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.fragment.app.Fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private FirebaseAuth mAuth;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 2;

    @Override
    protected void onStart() {
        super.onStart();
        // Bildirime tıklayıp geldiyse
        String menuFragment = getIntent().getStringExtra("menuFragment");
        // KONUM İLE İLGİLİ İZİN KONTROLÜ
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        )!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);
        }else getCurrentLocation();
        if (menuFragment != null) {
            if (menuFragment.equals("messages")) {

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesFragment()).addToBackStack("HomeFragment").commit();

            }
        }
    }
    // İZİN VERİLDİYSE YAPILACAKLAR
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length >0){
            getCurrentLocation();
        }else{
            Toast.makeText(this, "izin verilmedi", Toast.LENGTH_SHORT).show();
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
                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                            try {
                                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                String state = addresses.get(0).getAdminArea(); // şehir
                                String country = addresses.get(0).getCountryName(); // ülke
                                Toast.makeText(MainActivity.this, "city : " + state+ " country : " + country, Toast.LENGTH_SHORT).show();

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
        mAuth = FirebaseAuth.getInstance();
        Toast.makeText(this, mAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();


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
