package com.armagansadikoglu.kitapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShoppingsFragment extends Fragment {
    ArrayList<ShoppingModel> shoppings = new ArrayList<>();
    View view;
    RecyclerView shoppingsRecyclerView;
    ShoppingRecyclerViewAdapter shoppingRecyclerViewAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shoppings, container, false);
        shoppingsRecyclerView = view.findViewById(R.id.shoppingsRecyclerView);


        shoppingRecyclerViewAdapter = new ShoppingRecyclerViewAdapter(getContext(),shoppings);
        // Tek satırda 1 adet ürün sergilemek için
        shoppingsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        shoppingsRecyclerView.setAdapter(shoppingRecyclerViewAdapter);
        return  view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("shoppings");
        DatabaseReference shoppingsReference = reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        shoppingsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shoppings.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child: children){
                    ShoppingModel value = child.getValue(ShoppingModel.class);
                    shoppings.add(value);
                }
                shoppingRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
