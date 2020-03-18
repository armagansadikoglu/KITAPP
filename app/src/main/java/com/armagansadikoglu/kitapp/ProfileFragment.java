package com.armagansadikoglu.kitapp;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static  final int PICK_IMAGE_REQUEST = 1;
    ImageView imageViewProfile;
    Uri imageURI;

    ProgressBar progressBarProfile;

    EditText editTextUserDisplayNameProfile;
    Button buttonUpdateUserNameProfile;
    View v;
    private RecyclerView profileRecylerView;
    //private List<Notice> lstNotice;
    private RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<Notice> profileNotices = new ArrayList<>();


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData()!=null){
            imageURI = data.getData();
            Picasso.get().load(imageURI).into(imageViewProfile);

            //Tıklamayı önleme
            progressBarProfile.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);



            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference profileImagesRef = storageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            UploadTask uploadTask = profileImagesRef.putFile(imageURI);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    progressBarProfile.setVisibility(View.INVISIBLE);
                    //Tıklamayı geri verme
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getContext(),R.string.success, Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), R.string.fail, Toast.LENGTH_SHORT).show();
                }
            });
        }



    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_profile,container,false);

        imageViewProfile = v.findViewById(R.id.imageViewProfile);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference profileImagesRef = storageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        profileImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Glide.with(getContext()).load(imageURL).into(imageViewProfile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });



        // Profil resmine tıkladı
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_IMAGE_REQUEST);
            }
        });


        progressBarProfile = v.findViewById(R.id.progressBarProfile);
        editTextUserDisplayNameProfile = v.findViewById(R.id.editTextUserDisplayNameProfile);
        buttonUpdateUserNameProfile = v.findViewById(R.id.buttonUpdateUserNameProfile);
        // Update butonuna tıklandı
        buttonUpdateUserNameProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Tıklamayı önleme
                progressBarProfile.setVisibility(View.VISIBLE);
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                String newUserName = editTextUserDisplayNameProfile.getText().toString();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(newUserName)
                        //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressBarProfile.setVisibility(View.INVISIBLE);
                                    //Tıklamayı geri verme
                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Toast.makeText(getContext(), R.string.updated, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });


        profileRecylerView = v.findViewById(R.id.recyclerViewProfile);
        //recyclerViewAdapter = new RecyclerViewAdapter(getContext(),lstNotice);
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(),profileNotices);
        profileRecylerView.setAdapter(recyclerViewAdapter);

        // Tek satırda 2 adet ürün sergilemek için
        profileRecylerView.setLayoutManager(new GridLayoutManager(getActivity(),2));


        // TIKLANAN İTEME YAPILACAKLAR
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //Toast.makeText(getContext(), lstNotice.get(position).getBookName(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), profileNotices.get(position).getBookName(), Toast.LENGTH_SHORT).show();
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
                profileNotices.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child:children) {
                    Notice value = child.getValue(Notice.class);
                    if (value.getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){ // equals yerine == yazınca çalışmıyor
                        profileNotices.add(value);
                    }

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
