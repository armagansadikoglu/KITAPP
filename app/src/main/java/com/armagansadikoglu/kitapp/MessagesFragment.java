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

import com.google.firebase.auth.FirebaseAuth;
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


    ArrayList<String> userIDs = new ArrayList<>();
    ArrayList<String> userNames = new ArrayList<>();

    private DatabaseReference mDatabase;
    DatabaseReference usersDatabaseReference;
    DatabaseReference namesDatabaseReference;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view =  inflater.inflate(R.layout.fragment_messages,container,false);
       messagesUsersRecylerView = view.findViewById(R.id.messagesUsersRecylerView);
        messagesAdapter = new MessagesAdapter(getContext(),userIDs,userNames);
        // Tek satırda 1 adet ürün sergilemek için
        messagesUsersRecylerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
       messagesUsersRecylerView.setAdapter(messagesAdapter);

        // TIKLANAN USER'A YAPILACAKLAR
        messagesAdapter.setOnItemClickListener(new MessagesAdapter.OnItemClickListener(){

            @Override
            public void onItemClick(int position) {


                //TIKLANINCA MESAJLARIN OLDUĞU CHAT FRAGMENT GELSİN


                Fragment fg = new ChatFragment();
                // Fragmentlar arası bilgi alışverişi için bundle kullanımı (tıklanan kullanıcının bilgileri gidiyor)
                Bundle bundle=new Bundle();
                bundle.putString("receiverName", userNames.get(position));
                bundle.putString("receiverID",userIDs.get(position));
                fg.setArguments(bundle);
                // adding fragment to relative layout by using layout id
                getFragmentManager().beginTransaction().add(R.id.fragment_container, fg).addToBackStack("MessagesFragment").commit();

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


        // Burada sadece kullanıcının mesajlaştığı  userların ID'sini alıyoruz
        usersDatabaseReference = mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("messages");

        // anında eklemeyi sağlıyor value event listener.
        usersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userIDs.clear();

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    String s = child.getKey().toString();
                    userIDs.add(s);

                }
                // Verileri sürekli getirmesi için
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Burada kullanıcının mesajlaştığı idlerin isimlerini alıyoruz
        namesDatabaseReference = mDatabase.child("users");
        namesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userNames.clear();

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (int i = 0 ; i<userIDs.size() ; i++){
                    String userName = dataSnapshot.child(userIDs.get(i)).child("userDisplayName").getValue(String.class);
                    userNames.add(userName);
                }

                messagesAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }
}
