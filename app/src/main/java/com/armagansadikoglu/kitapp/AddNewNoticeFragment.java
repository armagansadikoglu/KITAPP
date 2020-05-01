package com.armagansadikoglu.kitapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class AddNewNoticeFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 2;
    private Button buttonAddNewNotice;
    private EditText editTextBookName, editTextBookPrice, editTextBookDetails;
    private ImageView imageViewNoticeAdd;
    private Uri bookImageURI;
    private DatabaseReference mDatabase;
    // Fotolar için de bu id kullanılacak
    private String id;
    private Boolean imageChosen = false;

    Spinner spinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_addnewnotice, container, false);

        buttonAddNewNotice = v.findViewById(R.id.buttonAddNotice);
        editTextBookName = v.findViewById(R.id.editTextNoticeBookName);
        editTextBookPrice = v.findViewById(R.id.editTextNoticeBookPrice);
        editTextBookDetails = v.findViewById(R.id.editTextNoticeBookDetails);
        imageViewNoticeAdd = v.findViewById(R.id.imageViewNoticeAdd);
        // Türler için
        spinner = v.findViewById(R.id.spinnerNewNotice);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.bookGenres));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);




        // KİTAP FOTOSUNU SEÇMEK İÇİN İNTENT BAŞLATMA
        imageViewNoticeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        buttonAddNewNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Alanlar boş mu kontrol ediliyor . imageViewNoticeAdd.getDrawable() == null foto için
                if (editTextBookName.getText().toString().trim().length() == 0 ||
                        editTextBookPrice.getText().toString().trim().length() == 0 ||
                        editTextBookDetails.getText().toString().trim().length() == 0 ||
                        imageChosen == false || spinner.getSelectedItem().toString().equals("Tür Seçin") || spinner.getSelectedItem().toString().equals("Choose Genre") ) {
                    Toast.makeText(getContext(), R.string.registerError, Toast.LENGTH_SHORT).show();
                } else {

                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    // Üst üste yazmasın diye key oluşturma
                    id = mDatabase.push().getKey();
                    Notice notice = new Notice(editTextBookName.getText().toString(), Long.parseLong(editTextBookPrice.getText().toString()), FirebaseAuth.getInstance().getCurrentUser().getEmail(), FirebaseAuth.getInstance().getCurrentUser().getUid(), editTextBookDetails.getText().toString(), id,spinner.getSelectedItem().toString());
                    mDatabase.child("notices").child(id).setValue(notice);

                    // KİTAP FOTOSUNU YÜKLEME
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference bookImageRef = storageRef.child(id);
                    //StorageReference profileImagesRef = storageRef.child(id).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    UploadTask uploadTask = bookImageRef.putFile(bookImageURI);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            //  progressBarProfile.setVisibility(View.INVISIBLE);
                            //Tıklamayı geri verme
                            //  getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(getContext(), R.string.success, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), R.string.fail, Toast.LENGTH_SHORT).show();
                        }
                    });


                    Toast.makeText(getContext(), R.string.noticeUploaded, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }


    // KİTAP FOTOSU SONUCUNA GÖRE FOTOYU IMAGEVİEW'A YÜKLEYEN onActivityResult fonksiyonu
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            bookImageURI = data.getData();
            Picasso.get().load(bookImageURI).into(imageViewNoticeAdd);
            imageChosen = true;

        }

    }

}
