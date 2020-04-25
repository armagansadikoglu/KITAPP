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

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    //// on clikc ınterface'i

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    /////////////////


    Context mContext;
    List<Chat> mData;
    Uri profilePicture;

    public ChatAdapter(Context mContext, List<Chat> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.chat_frag_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view, mListener); // burada m listener gönderildi

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.chatMessageDateTextView.setText(mData.get(position).getDate());
        holder.chatMessageTextView.setText(mData.get(position).getMessage());
        //BURASI ARTIK HATA VERMİYOR 
        /*
         * Olması gereken :
         * chatUserNameTextView = itemView.findViewById(R.id.chatUserNameTextView);
         * Hata verdiren :
         *
         * chatUserNameTextView = itemView.findViewById(R.id.messageUserNameTextView);
         * Bir önceki fragmenttaki textview'a erişmeye çalışılmış. Hata çözüldü.
         *
         * */
        holder.chatUserNameTextView.setText(mData.get(position).getSenderName());


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference profileImagesRef = storageRef.child(mData.get(position).getSenderID());

        profileImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                profilePicture = uri;
                // GLIDE DA PİCASSO DA ÇALIŞIYOR. PERFORMANS TESTLERİ YAPILIP KARAR VERİLECEK
                Glide.with(mContext).load(profilePicture).apply(new RequestOptions().override(70, 70)).into(holder.chatUserProfile);
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
        return mData.size();
    }

    // interface buradan devam
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView chatUserNameTextView;
        private TextView chatMessageTextView;
        private TextView chatMessageDateTextView;
        private ImageView chatUserProfile;

        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            chatUserNameTextView = itemView.findViewById(R.id.chatUserNameTextView);
            chatMessageTextView = itemView.findViewById(R.id.chatMessageTextView);
            chatMessageDateTextView = itemView.findViewById(R.id.chatMessageDateTextView);
            chatUserProfile = itemView.findViewById(R.id.chatUserProfile);


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
