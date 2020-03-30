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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder>{
    //// on clikc ınterface'i

    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    /////////////////


    Context mContext;

    List<String> Names;
    List<String> IDS;
    Uri profilePicture;

    public MessagesAdapter(Context mContext, List<String> IDS,  List<String> Names ) {
        this.mContext = mContext;
        this.IDS = IDS;
        this.Names = Names;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.messages_frag_users_row,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view,mListener); // burada m listener gönderildi

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.messagesUsernameTextView.setText(Names.get(position));

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference profileImagesRef = storageRef.child(IDS.get(position));

        profileImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                profilePicture = uri;
                // GLIDE DA PİCASSO DA ÇALIŞIYOR. PERFORMANS TESTLERİ YAPILIP KARAR VERİLECEK
                Glide.with(mContext).load(profilePicture).apply(new RequestOptions().override(50,50)).into(holder.messagesUserProfile);
                //Picasso.get().load(bookuri).resize(500,500).into(holder.rowPP);
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
        return Names.size();
    }
    // interface buradan devam
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView messagesUsernameTextView;
        private ImageView messagesUserProfile;

        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            messagesUserProfile = itemView.findViewById(R.id.messagesUserProfile);
            messagesUsernameTextView = itemView.findViewById(R.id.messagesUsernameTextView);

            // onclick interface
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position!= RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }


    }
}
