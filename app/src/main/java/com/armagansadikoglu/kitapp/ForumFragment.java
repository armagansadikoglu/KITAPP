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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class ForumFragment extends Fragment {
    View view;

    RecyclerView forumRecyclerView;
    ForumRecyclerViewAdapter forumRecyclerViewAdapter;
    ArrayList<Topic> topics = new ArrayList<>();
    Button forumSearchButton,forumAddButton;
    EditText forumSearchEditText,forumAddEditText;
    private DatabaseReference mDatabase;
    DatabaseReference topicsDatabaseReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_forum,container,false);
        forumRecyclerView = view.findViewById(R.id.forumMessagesRecyclerView);



        forumSearchButton = view.findViewById(R.id.forumSearchTopicButton);
        forumAddButton = view.findViewById(R.id.forumAddTopicButton);
        forumSearchEditText = view.findViewById(R.id.forumSearchTopicEditText);
        forumAddEditText = view.findViewById(R.id.forumAddTopicEditText);




        // Topic ekleme
        forumAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Boşluk kontrolü
                if (forumAddEditText.getText().toString().equals("")){
                    Toast.makeText(getContext(), R.string.registerError, Toast.LENGTH_SHORT).show();
                }else{

                    // Üst üste yazmasın diye key oluşturma
                    String id = mDatabase.push().getKey();
                    // Tarihe bakılacak

                    Topic topic = new Topic(forumAddEditText.getText().toString(), "10.01.2022",FirebaseAuth.getInstance().getCurrentUser().getUid());

                    topic.setKey(id);
                    mDatabase.child("forumTopics").child(id).setValue(topic);

                    Toast.makeText(getContext(),R.string.topicadded, Toast.LENGTH_SHORT).show();
                    // Edittexti temizleme
                    forumAddEditText.setText("");
                }


            }
        });

        forumRecyclerViewAdapter = new ForumRecyclerViewAdapter(getContext(),topics);
        forumRecyclerView.setAdapter(forumRecyclerViewAdapter);

        // Tek satırda 1 adet ürün sergilemek için
        forumRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));


        // TIKLANAN TOPİC'E YAPILACAKLAR
        forumRecyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //Toast.makeText(getContext(), lstNotice.get(position).getBookName(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), topics.get(position).getTopicName(), Toast.LENGTH_SHORT).show();

                //TIKLANINCA MESAJLARIN OLDUĞU FRAGMENT GELSİN

                Fragment fg = new TopicMessagesFragment();
                // Fragmentlar arası bilgi alışverişi için bundle kullanımı
                Bundle bundle=new Bundle();
                bundle.putString("topicName", topics.get(position).getTopicName());
                bundle.putString("topicKey",topics.get(position).getKey());
                fg.setArguments(bundle);
                // adding fragment to relative layout by using layout id
                getFragmentManager().beginTransaction().add(R.id.fragment_container, fg).addToBackStack("ForumFragment").commit();

                // layoutu güncelleme
                //forumRecyclerViewAdapter.notifyItemChanged(position);
            }
        });


        return view;
    }

    //////////////////////////
    /// TOPİCLERİ GETİRME
    ////////////

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        topicsDatabaseReference = mDatabase.child("forumTopics");

        // anında eklemeyi sağlıyor value event listener. addListenerForSingleValueEven işe yaramadı
        topicsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                topics.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Topic value = child.getValue(Topic.class);
                    topics.add(value);
                }
                // DOĞRU SIRADA OLMASI İÇİN LİSTEYİ DÖNDÜRME
                Collections.reverse(topics);
                // Verileri sürekli getirmesi için
                forumRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
