package com.armagansadikoglu.kitapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class ShoppingRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingRecyclerViewAdapter.MyViewHolder> {


    Context mContext;
    List<ShoppingModel> mData;
    Uri bookuri;

    public ShoppingRecyclerViewAdapter(Context mContext, List<ShoppingModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ShoppingRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.shopping_row, parent, false);
        ShoppingRecyclerViewAdapter.MyViewHolder viewHolder = new ShoppingRecyclerViewAdapter.MyViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final ShoppingRecyclerViewAdapter.MyViewHolder holder, int position) {
        if (Locale.getDefault().getLanguage().equals("tr")){
            String bookName = "Kitap Adı : " +  mData.get(position).getBookName() ;
            holder.shopRowBookName.setText(bookName);
            String buyer = "Alıcı : " + mData.get(position).getBuyerName();
            holder.shopRowBuyer.setText(buyer);
            String price = "Ücret : " + mData.get(position).getPrice().toString() + "₺";
            holder.shopRowPrice.setText(price);
            String seller = "Satıcı : " + mData.get(position).getSellerName();
            holder.shopRowSeller.setText(seller);
        }else{
            String bookName = "Book Name : " +  mData.get(position).getBookName() ;
            holder.shopRowBookName.setText(bookName);
            String buyer = "Buyer : " + mData.get(position).getBuyerName();
            holder.shopRowBuyer.setText(buyer);
            String price = "Price : " + mData.get(position).getPrice().toString() + " €/$";
            holder.shopRowPrice.setText(price);
            String seller = "Seller : " + mData.get(position).getSellerName();
            holder.shopRowSeller.setText(seller);
        }




        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference bookImageRef = storageRef.child(mData.get(position).getShoppingID());

        bookImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                bookuri = uri;
                // GLIDE DAha düzgün duruyor
                Glide.with(mContext).load(bookuri).apply(new RequestOptions().override(150, 150)).into(holder.shopRowImageView);
                //Picasso.get().load(bookuri).resize(150,150).into(holder.shopRowImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView shopRowBookName;
        private TextView shopRowSeller;
        private TextView shopRowBuyer;
        private TextView shopRowPrice;
        private ImageView shopRowImageView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            shopRowBookName = itemView.findViewById(R.id.shopRowBookName);
            shopRowSeller = itemView.findViewById(R.id.shopRowSeller);
            shopRowBuyer = itemView.findViewById(R.id.shopRowBuyer);
            shopRowPrice = itemView.findViewById(R.id.shopRowPrice);
            shopRowImageView = itemView.findViewById(R.id.shopRowImageView);

        }
    }
}
