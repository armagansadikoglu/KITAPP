package com.armagansadikoglu.kitapp;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment {

    View v;
    private RecyclerView myrcyclerview;
    //private List<Notice> lstNotice;
    private RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<Notice> notices = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_home,container,false);
        myrcyclerview = v.findViewById(R.id.homeFragmentRecyclerView);
        //recyclerViewAdapter = new RecyclerViewAdapter(getContext(),lstNotice);
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(),notices);
        myrcyclerview.setAdapter(recyclerViewAdapter);

        // Tek satırda 2 adet ürün sergilemek için
        myrcyclerview.setLayoutManager(new GridLayoutManager(getActivity(),2));



        // TIKLANAN İTEME YAPILACAKLAR
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //Toast.makeText(getContext(), lstNotice.get(position).getBookName(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), notices.get(position).getBookName(), Toast.LENGTH_SHORT).show();
                // layoutu güncelleme
                recyclerViewAdapter.notifyItemChanged(position);
            }
        });



        return v;


    }

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
                for (DataSnapshot child:children) {
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
}
