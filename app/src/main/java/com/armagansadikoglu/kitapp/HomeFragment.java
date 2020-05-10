package com.armagansadikoglu.kitapp;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    View v;
    private RecyclerView myrcyclerview;
    private RecyclerViewAdapter recyclerViewAdapter;
    private EditText homeBookSearchEditText;
    private Button homeBookSearchButton;
    private RadioButton cityFilterRadioButton,countryFilterRadioButton;
    private Spinner genreFilterSpinner;
    ArrayList<Notice> notices = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_home, container, false);

        cityFilterRadioButton = v.findViewById(R.id.cityFilterRadioButton);
        countryFilterRadioButton = v.findViewById(R.id.countryFilterRadioButton);

        // Spinner
        genreFilterSpinner = v.findViewById(R.id.genreFilterSpinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.bookGenres));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreFilterSpinner.setAdapter(spinnerAdapter);


        homeBookSearchButton = v.findViewById(R.id.homeBookSearchButton);
        homeBookSearchEditText = v.findViewById(R.id.homeBookSearchEditText);
        homeBookSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchNotices(v);
            }
        });
        myrcyclerview = v.findViewById(R.id.homeFragmentRecyclerView);

        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), notices);
        myrcyclerview.setAdapter(recyclerViewAdapter);

        // Tek satırda 2 adet ürün sergilemek için
        myrcyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        // TIKLANAN İLANA YAPILACAKLAR

        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {

                // Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getContext(), myrcyclerview);
                // Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                // Registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        // popup'a tıklanınca yapılacak
                        Fragment fg = new ChatFragment();
                        // Fragmentlar arası bilgi alışverişi için bundle kullanımı (tıklanan kullanıcının bilgileri gidiyor)
                        final Bundle bundle = new Bundle();

                        // Menüye yeni item eklenirse diye ifle kontrol ettirdim
                        if (item.getItemId() == R.id.popupmenusendmessage) {

                            bundle.putString("receiverName", notices.get(position).getSeller());
                            bundle.putString("receiverID", notices.get(position).getUserID());
                            fg.setArguments(bundle);
                            // adding fragment to relative layout by using layout id
                            getFragmentManager().beginTransaction().add(R.id.fragment_container, fg).addToBackStack("HomeFragment").commit();
                            //Toast.makeText(getContext(), notices.get(position).getSeller(), Toast.LENGTH_SHORT).show();

                        }
                        return true;
                    }
                });

               // Toast.makeText(getContext(), notices.get(position).getBookName(), Toast.LENGTH_SHORT).show();
                popup.show();// Showing popup menu


                // layoutu güncelleme
                recyclerViewAdapter.notifyItemChanged(position);
            }
        });


        return v;


    }


    // İlanları getirme
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference().child("notices");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notices.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Notice value = child.getValue(Notice.class);
                    notices.add(value);
                }
                // Verileri sürekli getirmesi için
                recyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    // İLAN ARAMA
    private void searchNotices(View v) {
        final String word = homeBookSearchEditText.getText().toString();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference().child("notices");
        //Toast.makeText(getContext(), MainActivity.country, Toast.LENGTH_SHORT).show();
        final String genre = genreFilterSpinner.getSelectedItem().toString();
        if (genre.equals("Choose Genre") || genre.equals("Tür Seçin")){ // TÜR SEÇİLMEMİŞ
            if (countryFilterRadioButton.isChecked()){
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        notices.clear();
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            Notice value = child.getValue(Notice.class);
                            if (value.getBookName().toLowerCase().contains(word.toLowerCase()) && value.getCountry().equals(MainActivity.country))
                                notices.add(value);
                        }
                        // Verileri sürekli getirmesi için
                        recyclerViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }else if (cityFilterRadioButton.isChecked()){
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        notices.clear();
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            Notice value = child.getValue(Notice.class);
                            if (value.getBookName().toLowerCase().contains(word.toLowerCase()) && value.getCity().equals(MainActivity.state))
                                notices.add(value);
                        }
                        // Verileri sürekli getirmesi için
                        recyclerViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }else {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        notices.clear();
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            Notice value = child.getValue(Notice.class);
                            if (value.getBookName().toLowerCase().contains(word.toLowerCase()))
                                notices.add(value);
                        }
                        // Verileri sürekli getirmesi için
                        recyclerViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }else{ // TÜR SEÇİLMİŞ
            if (countryFilterRadioButton.isChecked()){
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        notices.clear();
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            Notice value = child.getValue(Notice.class);
                            if (value.getBookName().toLowerCase().contains(word.toLowerCase()) && value.getCountry().equals(MainActivity.country) && value.getGenre().equals(genre))
                                notices.add(value);
                        }
                        // Verileri sürekli getirmesi için
                        recyclerViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }else if (cityFilterRadioButton.isChecked()){
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        notices.clear();
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            Notice value = child.getValue(Notice.class);
                            if (value.getBookName().toLowerCase().contains(word.toLowerCase()) && value.getCity().equals(MainActivity.state)&& value.getGenre().equals(genre))
                                notices.add(value);
                        }
                        // Verileri sürekli getirmesi için
                        recyclerViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }else {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        notices.clear();
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            Notice value = child.getValue(Notice.class);
                            if (value.getBookName().toLowerCase().contains(word.toLowerCase())&& value.getGenre().equals(genre))
                                notices.add(value);
                        }
                        // Verileri sürekli getirmesi için
                        recyclerViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }



    }


}
