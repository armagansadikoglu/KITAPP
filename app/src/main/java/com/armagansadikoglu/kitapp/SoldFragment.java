package com.armagansadikoglu.kitapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

// MESSAGES FRAGEMENT'IN NEREDEYSE AYNISI OLDUĞU İÇİN KOPYALAYIP YAPIŞTIRDIK VE GEREKLİ DÜZENLEMELERİ YAPTIK

public class SoldFragment extends Fragment {
    private View view;

    private RecyclerView soldFragmentRecyclerView;
    private MessagesAdapter messagesAdapter;



    private ArrayList<String> userIDs = new ArrayList<>();
    private ArrayList<String> userNames = new ArrayList<>();

    private DatabaseReference mDatabase;
    private DatabaseReference usersDatabaseReference;
    private DatabaseReference namesDatabaseReference;

    private String noticeID,bookName;
    private Long price;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sold, container, false);

        soldFragmentRecyclerView = view.findViewById(R.id.soldFragmentRecyclerView);
        messagesAdapter = new MessagesAdapter(getContext(), userIDs, userNames);
        // Tek satırda 1 adet ürün sergilemek için
        soldFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        soldFragmentRecyclerView.setAdapter(messagesAdapter);

        // ProfileFragment'dan gelen bilgileri aldık
        noticeID = getArguments().getString("noticeID");
        bookName = getArguments().getString("bookName");
        price = getArguments().getLong("price");


        // TIKLANAN USER'A YAPILACAKLAR
        messagesAdapter.setOnItemClickListener(new MessagesAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(final int position) {
                Toast.makeText(getContext(), userNames.get(position), Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.areYouSure)
                        .setMessage(getResources().getString(R.string.doYouConfirm)+ "\n\n" + getResources().getString(R.string.buyer)+ " : "  + userNames.get(position)
                                + "\n\n"+ getResources().getString(R.string.book)+ " : " + bookName )
                        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               // Toast.makeText(getContext(), "İşlem İptal Edildi", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(getContext(), "Kaydedilecek", Toast.LENGTH_SHORT).show();
                                // Satışı yapan kullanıcıya ekleme
                                DatabaseReference shoppingsDbReference = mDatabase.child("shoppings").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                ShoppingModel shopping = new ShoppingModel(bookName,noticeID,FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                        FirebaseAuth.getInstance().getCurrentUser().getUid(),userNames.get(position),userIDs.get(position),price);
                                shoppingsDbReference.child(noticeID).setValue(shopping);
                                // Alan kullanıcıya ekleme

                                DatabaseReference dbref = mDatabase.child("shoppings").child(userIDs.get(position));
                                // aynı bilgiler yazıldığı içim direk shopping burada da kullanılabilir
                                dbref.child(noticeID).setValue(shopping);
                            }
                        })
                        .setCancelable(true)
                        .create();
                builder.show();

            }
        });


        return view;
    }

    //////////////////////////
    /// KULLANICILARI GETİRME
    ////////////

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        // Burada tüm userların idsini çekiyoruz
        usersDatabaseReference = mDatabase.child("users");

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
        // Burada tüm userların adını alıyuotuız
        namesDatabaseReference = mDatabase.child("users");
        namesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userNames.clear();

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (int i = 0; i < userIDs.size(); i++) {
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


