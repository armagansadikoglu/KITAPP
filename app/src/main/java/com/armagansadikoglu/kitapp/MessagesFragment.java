package com.armagansadikoglu.kitapp;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class MessagesFragment extends Fragment {
    View view;

    RecyclerView messagesUsersRecylerView;
    MessagesAdapter messagesAdapter;
    ArrayList<User> users = new ArrayList<>();

    private DatabaseReference mDatabase;
    DatabaseReference usersDatabaseReference;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view =  inflater.inflate(R.layout.fragment_messages,container,false);
       messagesUsersRecylerView = view.findViewById(R.id.messagesUsersRecylerView);
        messagesAdapter = new MessagesAdapter(getContext(),users);
        // Tek satırda 1 adet ürün sergilemek için
        messagesUsersRecylerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
       messagesUsersRecylerView.setAdapter(messagesAdapter);

        // TIKLANAN USER'A YAPILACAKLAR
        messagesAdapter.setOnItemClickListener(new MessagesAdapter.OnItemClickListener(){

            @Override
            public void onItemClick(int position) {
                Toast.makeText(getContext(), users.get(position).getUserDisplayName(), Toast.LENGTH_SHORT).show();

                //TIKLANINCA MESAJLARIN OLDUĞU CHAT FRAGMENT GELSİN


                Fragment fg = new ChatFragment();
                // Fragmentlar arası bilgi alışverişi için bundle kullanımı (tıklanan kullanıcının bilgileri gidiyor)
                Bundle bundle=new Bundle();
                bundle.putString("userName", users.get(position).getUserDisplayName());
                bundle.putString("userID",users.get(position).getUserID());
                fg.setArguments(bundle);
                // adding fragment to relative layout by using layout id
                getFragmentManager().beginTransaction().add(R.id.fragment_container, fg).commit();

                // layoutu güncelleme
                //forumRecyclerViewAdapter.notifyItemChanged(position);
            }
        });





       return  view;
    }

    //////////////////////////
    /// KULLANICILARI GETİRME
    ////////////

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        usersDatabaseReference = mDatabase.child("users");

        // anında eklemeyi sağlıyor value event listener. addListenerForSingleValueEven işe yaramadı
        usersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    User value = child.getValue(User.class);
                    users.add(value);
                }
                // DOĞRU SIRADA OLMASI İÇİN LİSTEYİ DÖNDÜRME
                Collections.reverse(users);
                // Verileri sürekli getirmesi için
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
