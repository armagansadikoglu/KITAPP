package com.armagansadikoglu.kitapp;

import android.os.Bundle;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class ChatFragment extends Fragment {
    RecyclerView chatRecyclerView;
    ChatAdapter chatAdapter;
    EditText chatEditText;
    Button chatSendButton;
    String receiverName, receiverID;
    View view;


    String chatKey;

    ArrayList<Chat> chats = new ArrayList<>();

    private DatabaseReference mDatabase;
    DatabaseReference chatsDatabaseReference;
    // bildirimler için
    DatabaseReference serverkeyDatabaseReference;
    String server_key;
    String baseURL = "https://fcm.googleapis.com/fcm/";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        chatEditText = view.findViewById(R.id.chatEditText);
        chatSendButton = view.findViewById(R.id.chatSendButton);


        chatAdapter = new ChatAdapter(getContext(), chats);
        chatRecyclerView.setAdapter(chatAdapter);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));


        final String displayName;
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser().getDisplayName() == null) {
            displayName = firebaseAuth.getCurrentUser().getEmail();
        } else {
            displayName = firebaseAuth.getCurrentUser().getDisplayName();
        }
        // Mesaj gönderme
        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Boşluk kontrolü
                if (chatEditText.getText().toString().equals("")) {
                    Toast.makeText(getContext(), R.string.registerError, Toast.LENGTH_SHORT).show();
                } else {
                    // Üst üste yazmasın diye key oluşturma
                    chatKey = mDatabase.push().getKey();
                    // Tarihe bakılacak

                    //String displayName;


                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());
                    //mesajın kendisi
                    Chat chat = new Chat(chatKey, FirebaseAuth.getInstance().getCurrentUser().getUid(), displayName, receiverID, receiverName, chatEditText.getText().toString(), currentDateandTime);

                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("messages").child(receiverID).child(chatKey).setValue(chat);

                    // mesajı attığımız kişiye de mesajı eklememiz lazım

                    mDatabase.child("users").child(receiverID).child("messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(chatKey).setValue(chat);

                    Notification notification = new Notification(firebaseAuth.getCurrentUser().getUid(), firebaseAuth.getCurrentUser().getDisplayName(), chatEditText.getText().toString());
                    mDatabase.child("notifications").child(receiverID).child(mDatabase.push().getKey()).setValue(notification);

                    //Mesaj attıktan sonra temizlemek için
                    chatEditText.setText("");

                }
            }
        });

        return view;
    }

    // Mesajları Getirme
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // MessagesFragment'dan gelen bilgileri aldık
        receiverName = getArguments().getString("receiverName");
        receiverID = getArguments().getString("receiverID");

        mDatabase = FirebaseDatabase.getInstance().getReference();


        //Server keyini okuma
        serverkeyDatabaseReference = mDatabase.child("server");
        serverkeyDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               /* Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    server_key = child.getValue(String.class);
                }*/
                // Üstteki de çalışıyor zaten ama tekli için böyle de bir yöntem varmış
                DataSnapshot next = dataSnapshot.getChildren().iterator().next();
                String s = next.getValue().toString();
                Log.d("Server KEY", s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        chatsDatabaseReference = mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("messages").child(receiverID);

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
                // Mesajlaşmada reverse etmeye gerek yok. Eski mesaj yukarıda kalsın
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