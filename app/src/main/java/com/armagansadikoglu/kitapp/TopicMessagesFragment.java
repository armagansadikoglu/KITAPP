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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class TopicMessagesFragment extends Fragment {
    View view;

    RecyclerView topicMessagesRecyclerView;
    ForumRecyclerViewAdapter forumRecyclerViewAdapter;
    ArrayList<Topic> topicMessages = new ArrayList<>();
    Button topicMessagesSendButton;
    EditText topicMessagesAddMessageEditText;
    private DatabaseReference mDatabase;
    DatabaseReference topicsDatabaseReference;

    String topicname;
    String topicKey;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.topic_messages, container, false);
        // ForumFragment'dan bundle ile gönderilen isimi ve keyi alıyoruz

        Toast.makeText(getContext(), topicname, Toast.LENGTH_SHORT).show();


        topicMessagesRecyclerView = view.findViewById(R.id.topicMessagesRecyclerView);


        topicMessagesSendButton = view.findViewById(R.id.topicMessagesSendButton);
        topicMessagesAddMessageEditText = view.findViewById(R.id.topicMessagesAddMessageEditText);


        // Yeni mesaj ekleme
        topicMessagesSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Boşluk kontrolü
                if (topicMessagesAddMessageEditText.getText().toString().equals("")) {
                    Toast.makeText(getContext(), R.string.registerError, Toast.LENGTH_SHORT).show();
                } else {

                    // Üst üste yazmasın diye key oluşturma
                    String id = mDatabase.push().getKey();

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    // Tarihe bakılacak
                    Topic topic = new Topic(topicMessagesAddMessageEditText.getText().toString(), currentDateandTime, FirebaseAuth.getInstance().getCurrentUser().getUid());
                    topic.setKey(id);
                    mDatabase.child("forumTopics").child(topicKey).child("messages").child(id).setValue(topic);

                    //EditTexti temizleme
                    topicMessagesAddMessageEditText.setText("");

                }

            }
        });

        forumRecyclerViewAdapter = new ForumRecyclerViewAdapter(getContext(), topicMessages);
        topicMessagesRecyclerView.setAdapter(forumRecyclerViewAdapter);

        // Tek satırda 1 adet ürün sergilemek için
        topicMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));


        // TIKLANAN İTEME YAPILACAKLAR
        forumRecyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //Toast.makeText(getContext(), lstNotice.get(position).getBookName(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), topicMessages.get(position).getTopicName(), Toast.LENGTH_SHORT).show();


                // layoutu güncelleme
                //forumRecyclerViewAdapter.notifyItemChanged(position);
            }
        });


        return view;
    }

    //////////////////////////
    /// MESAJLARI GETİRME child("forumTopics").child(topicKey).child("messages") child("messages") ile halloldu
    ////////////

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topicname = getArguments().getString("topicName");
        topicKey = getArguments().getString("topicKey");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        // topicsDatabaseReference = mDatabase.child("forumTopics").child(topicKey).getRoot(); eklemeyi denemek için yazdım
        topicsDatabaseReference = mDatabase.child("forumTopics").child(topicKey).child("messages");// *messages node eklendi yoksa message child'ına ulaşamıyorums

        // anında eklemeyi sağlıyor value event listener. addListenerForSingleValueEven işe yaramadı
        topicsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                topicMessages.clear();
                //DataSnapshot forumTopics = dataSnapshot.child("forumTopics").child(topicKey); *gelen snapshotı incelemek için yazmıştım
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                //Iterable<DataSnapshot> children = dataSnapshot.child("forumTopics").child(topicKey).child("messages").getChildren();
                for (DataSnapshot child : children) {
                    Topic value = child.getValue(Topic.class);
                    topicMessages.add(value);
                }
                // DOĞRU SIRADA OLMASI İÇİN LİSTEYİ DÖNDÜRME
                Collections.reverse(topicMessages);
                // Verileri sürekli getirmesi için
                forumRecyclerViewAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
