package com.armagansadikoglu.kitapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    //// on clikc ınterface'i
    private int shoppingsCount = 0;
    private OnItemClickListener mListener;
    String word;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    /////////////////


    Context mContext;
    List<Notice> mData;
    Uri bookuri;

    public RecyclerViewAdapter(Context mContext, List<Notice> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view, mListener); // burada m listener gönderildi

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
         word = mContext.getResources().getString(R.string.bookName) + " : " + mData.get(position).getBookName() ;
        holder.rowTvBookName.setText(word);
        word =  mContext.getResources().getString(R.string.seller) + " : " + mData.get(position).getSeller() ;
        holder.rowTvSeller.setText(word);
        if (Locale.getDefault().getLanguage().equals("tr")) {
            word = mContext.getResources().getString(R.string.bookPrice) + " : "+ String.valueOf(mData.get(position).getPrice()) + " ₺";
            holder.rowTvPrice.setText(word);
        } else {
            word = mContext.getResources().getString(R.string.bookPrice) + " : "+ String.valueOf(mData.get(position).getPrice()) +" €/$";
            holder.rowTvPrice.setText(word);
        }
        word = mContext.getResources().getString(R.string.details) + " : " + mData.get(position).getBookDetails();
        holder.rowTvBookDetails.setText(word);
        word = mContext.getResources().getString(R.string.genre) + " : " + mData.get(position).getGenre();
        holder.rowTvGenre.setText(word);
        // Yapılan alışveriş sayısını bulma

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference shoppings = reference.child("shoppings").child(mData.get(position).getUserID());
        shoppings.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                shoppingsCount = 0;
                for (DataSnapshot child: children) {
                    shoppingsCount++;
                }
                word = shoppingsCount + " " + mContext.getResources().getString(R.string.shoopingsMade);
                holder.rowTvShoppings.setText(word);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // İlan fotosunu çekme
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference profileImagesRef = storageRef.child(mData.get(position).getNoticeID());

        profileImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                bookuri = uri;
                // GLIDE DA PİCASSO DA ÇALIŞIYOR. PERFORMANS TESTLERİ YAPILIP KARAR VERİLECEK
                Glide.with(mContext).load(bookuri).apply(new RequestOptions().override(500, 500)).into(holder.rowPP);
                //Picasso.get().load(bookuri).resize(500,500).into(holder.rowPP);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        // Kullanıcının fotoğrafını çekme
        StorageReference sellerImageRef = storageRef.child(mData.get(position).getUserID());
        sellerImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                bookuri = uri;
                // GLIDE DA PİCASSO DA ÇALIŞIYOR. PERFORMANS TESTLERİ YAPILIP KARAR VERİLECEK
                Glide.with(mContext).load(bookuri).apply(new RequestOptions().override(500, 500)).into(holder.rowSellerPP);
                //Picasso.get().load(bookuri).resize(500,500).into(holder.rowPP);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    // interface buradan devam
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView rowTvPrice;
        private TextView rowTvSeller;
        private TextView rowTvBookName;
        private ImageView rowPP;
        private TextView rowTvBookDetails;
        private TextView rowTvGenre;

        private TextView rowTvShoppings;
        private ImageView rowSellerPP;

        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            rowPP = itemView.findViewById(R.id.rowPP);
            rowTvPrice = itemView.findViewById(R.id.rowTvPrice);
            rowTvSeller = itemView.findViewById(R.id.rowTvSeller);
            rowTvBookName = itemView.findViewById(R.id.rowTvBookName);
            rowTvGenre = itemView.findViewById(R.id.rowTvGenre);
            rowTvBookDetails = itemView.findViewById(R.id.rowTvBookDetails);
            rowTvShoppings = itemView.findViewById(R.id.rowTvShoppings);
            rowSellerPP = itemView.findViewById(R.id.rowSellerPP);
            // onclick interface
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }


    }
}
