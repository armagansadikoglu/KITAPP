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

public class ChatFragment extends Fragment {
    RecyclerView chatRecyclerView;
    ChatAdapter chatAdapter;
    EditText chatEditText;
    Button chatSendButton;
    String userName,userID;
    View view;

    String chatKey;

    ArrayList<Chat> chats = new ArrayList<>();

    private DatabaseReference mDatabase;
    DatabaseReference chatsDatabaseReference;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=  inflater.inflate(R.layout.fragment_chat,container,false);
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        chatEditText = view.findViewById(R.id.chatEditText);
        chatSendButton =  view.findViewById(R.id.chatSendButton);


        chatAdapter = new ChatAdapter(getContext(),chats);
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));

        final String displayName;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser().getDisplayName() == null){
            displayName = firebaseAuth.getCurrentUser().getEmail();
        }else{
            displayName = firebaseAuth.getCurrentUser().getDisplayName();
        }
        // Mesaj gönderme
        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Boşluk kontrolü
                if (chatEditText.getText().toString().equals("")){
                    Toast.makeText(getContext(), R.string.registerError, Toast.LENGTH_SHORT).show();
                }else{
                    // Üst üste yazmasın diye key oluşturma
                    chatKey = mDatabase.push().getKey();
                    // Tarihe bakılacak

                    //String displayName;


                    Chat chat = new Chat(chatKey,FirebaseAuth.getInstance().getCurrentUser().getUid(), displayName,userID,userName,chatEditText.getText().toString(),"10.10.2010");

                    mDatabase.child("chat").child(chatKey).setValue(chat);

                    Toast.makeText(getContext(),R.string.topicadded, Toast.LENGTH_SHORT).show();}
            }
        });

        return view;
    }
    // Mesajları Getirme
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // MessagesFragment'dan gelen bilgileri aldık
        userName= getArguments().getString("userName");
        userID = getArguments().getString("userID");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        chatsDatabaseReference = mDatabase.child("chat");

        // anında eklemeyi sağlıyor value event listener. addListenerForSingleValueEven işe yaramadı
        chatsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Chat value = child.getValue(Chat.class);
                    chats.add(value);
                }
                // DOĞRU SIRADA OLMASI İÇİN LİSTEYİ DÖNDÜRME
                Collections.reverse(chats);

                // Verileri sürekli getirmesi için
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}