package com.armagansadikoglu.kitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private FirebaseAuth mAuth;


    @Override
    protected void onStart() {
        super.onStart();
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Menüde itemlere tıklanılınca yapılacaklar
        NavigationView navigationView = findViewById(R.id.navigationView);

        //--- Navigation header'a erişim
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername =  headerView.findViewById(R.id.navHeaderTextView);
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
                Glide.with(getApplicationContext()).load(img).apply(new RequestOptions().override(600,600)).into(navUserPic);
                //Picasso.get().load(bookuri).resize(500,500).into(holder.rowPP);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        // Kullanıcı adı varsa yüklüyoruz yoksa e-mail adresini kullanıyoruz
        if (mAuth.getCurrentUser().getDisplayName() == null){
            navUsername.setText(getString(R.string.hello,mAuth.getCurrentUser().getEmail()) );
        }
        else {
            navUsername.setText( getString(R.string.hello, mAuth.getCurrentUser().getDisplayName()) );
        }


        // Seçilen iteme tıklanıldığında açılacak fragmentler
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_menu_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).addToBackStack("HomeFragment").commit();
                        break;
                    case R.id.nav_menu_newNotice:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AddNewNoticeFragment()).addToBackStack("HomeFragment").commit();
                        break;
                    case R.id.nav_menu_messages:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MessagesFragment()).addToBackStack("HomeFragment").commit();
                        break;
                    case R.id.nav_menu_profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).addToBackStack("HomeFragment").commit();
                        break;
                    case R.id.nav_menu_cart:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ShoppingsFragment()).addToBackStack("HomeFragment").commit();
                        break;
                    case R.id.nav_menu_forum:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ForumFragment()).addToBackStack("HomeFragment").commit();
                        break;
                    case R.id.nav_menu_star:
                        break;
                    case R.id.nav_menu_logout:
                        FirebaseAuth.getInstance().signOut();
                        break;
                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        if (savedInstanceState == null){
            // Açılırken Home sayfasını aç
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
            // navigation menüde home seçili olsun
            navigationView.setCheckedItem(R.id.nav_menu_home);
        }

    }
    // Geri tuşuna basıldığında yapılacaklar
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            //super.onBackPressed();
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }


    }
}
