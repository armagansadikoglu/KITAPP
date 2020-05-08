package com.armagansadikoglu.kitapp;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
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

    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView imageViewProfile;
    Uri imageURI;

    ProgressBar progressBarProfile;

    EditText editTextUserDisplayNameProfile;
    Button buttonUpdateUserNameProfile;
    View v;
    private RecyclerView profileRecylerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<Notice> profileNotices = new ArrayList<>();

    boolean available = true;


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
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
                    Toast.makeText(getContext(), R.string.success, Toast.LENGTH_SHORT).show();
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
        v = inflater.inflate(R.layout.fragment_profile, container, false);

        imageViewProfile = v.findViewById(R.id.imageViewProfile);
            // profil fotosunu çekme
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
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });


        progressBarProfile = v.findViewById(R.id.progressBarProfile);
        editTextUserDisplayNameProfile = v.findViewById(R.id.editTextUserDisplayNameProfile);
        buttonUpdateUserNameProfile = v.findViewById(R.id.buttonUpdateUserNameProfile);
        // Update butonuna tıklandı
        buttonUpdateUserNameProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 boolean newName = userNameAvailable();
                if (editTextUserDisplayNameProfile.getText().toString().trim() .length() == 0) {  // boş bırakmış mı kontrolü
                    Toast.makeText(getContext(), R.string.registerError, Toast.LENGTH_SHORT).show(); // registerda da kullandığım boş bırakmayın uyarısını ver
                }else{

                    if (editTextUserDisplayNameProfile.getText().toString().length() > 20){ // yeni kullanıcı adı uzun
                        Toast.makeText(getContext(), R.string.usernameLong, Toast.LENGTH_SHORT).show();
                    }else if (newName != true){ // aynı isim kullanılıyor
                        Toast.makeText(getContext(), "İSİM ALINMIŞ", Toast.LENGTH_SHORT).show();
                    }else{
                        //Tıklamayı önleme
                        progressBarProfile.setVisibility(View.VISIBLE);
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        final String newUserName = editTextUserDisplayNameProfile.getText().toString();

                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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
                                            // Databasedeki username'i de güncelledik
                                            DatabaseReference usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
                                            usersDatabaseReference.child(user.getUid()).child("userDisplayName").setValue(newUserName);

                                            Toast.makeText(getContext(), R.string.updated, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                }

            }
        });


        profileRecylerView = v.findViewById(R.id.recyclerViewProfile);
        //recyclerViewAdapter = new RecyclerViewAdapter(getContext(),lstNotice);
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), profileNotices);
        profileRecylerView.setAdapter(recyclerViewAdapter);

        // Tek satırda 2 adet ürün sergilemek için
        profileRecylerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));


        // TIKLANAN İTEME YAPILACAKLAR
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                // Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getContext(), profileRecylerView);
                // Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_profile_menu, popup.getMenu());
                // Registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        // popup'a tıklanınca yapılacak

                        // Bunlar gereksiz gibi

                        //Fragment fg = new ChatFragment();
                        // Fragmentlar arası bilgi alışverişi için bundle kullanımı (tıklanan kullanıcının bilgileri gidiyor)
                        //final Bundle bundle = new Bundle();

                        // İlanı Silme işlemi
                        if (item.getItemId() == R.id.popupProfileDelete) {

                            DatabaseReference mDatabase;
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("notices").child(profileNotices.get(position).getNoticeID());
                            mDatabase.removeValue();
                            // Yüklenen fotoğrafı silme
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference().child(profileNotices.get(position).getNoticeID());        // profileNotices.get(position).getNoticeID()+".jpeg"); yapınca olmyuyor
                            storageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // Toast.makeText(getContext(), "silindi", Toast.LENGTH_SHORT).show(); deneme amaçlı
                                }
                            });
                            Toast.makeText(getContext(), R.string.noticeDeleted, Toast.LENGTH_SHORT).show();
                        }else if (item.getItemId() == R.id.popupProfileSold){
                            // İlanı Silme işlemi
                            /*
                            DatabaseReference mDatabase;
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("notices").child(profileNotices.get(position).getNoticeID());
                            mDatabase.removeValue();
                            */
                            Fragment fg = new SoldFragment();
                            // Fragmentlar arası bilgi alışverişi için bundle kullanımı (tıklanan kullanıcının bilgileri gidiyor)
                            Bundle bundle = new Bundle();
                            bundle.putString("noticeID", profileNotices.get(position).getNoticeID());
                            bundle.putString("bookName",profileNotices.get(position).getBookName());
                            bundle.putLong("price",profileNotices.get(position).getPrice());
                            fg.setArguments(bundle);
                            // adding fragment to relative layout by using layout id
                            getFragmentManager().beginTransaction().add(R.id.fragment_container, fg).addToBackStack("ProfileFragment").commit();

                        }
                        return true;
                    }
                });

                popup.show();
                //Toast.makeText(getContext(), lstNotice.get(position).getBookName(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), profileNotices.get(position).getBookName(), Toast.LENGTH_SHORT).show();
                // layoutu güncelleme
                recyclerViewAdapter.notifyItemChanged(position);
            }
        });


        return v;
    }

    private boolean userNameAvailable() {

        final String newUserNme =  editTextUserDisplayNameProfile.getText().toString();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference users = reference.child("users");
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children){
                    User value = child.getValue(User.class);
                    if (value.getUserDisplayName().equals(newUserNme)){
                        available = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return  available;
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
                for (DataSnapshot child : children) {
                    Notice value = child.getValue(Notice.class);
                    if (value.getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) { // equals yerine == yazınca çalışmıyor
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
