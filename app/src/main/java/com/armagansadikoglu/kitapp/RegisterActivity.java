package com.armagansadikoglu.kitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    ProgressBar progressBar;
    CheckBox registerCheckBox;
    Button registerButton;
    EditText registerEmailEditText;
    EditText registerPasswordEditText;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.registerProgressBar);
        registerButton = findViewById(R.id.registerButton);
        registerCheckBox = findViewById(R.id.registerCheckBox);
        registerEmailEditText = findViewById(R.id.registerEmailEditText);
        registerPasswordEditText = findViewById(R.id.registerPasswordEditText);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        usersDatabaseReference = mDatabase.child("users");

    }

    public void registerButtonOnClick(View view) {

        // Klavyeyi kapatma -- yoksa yazmaya devam eder
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);




        // registerEmailEditText.getText().toString() == "" ile yapınca bir kere doldurup silince hata vermesin gerekirken hata vermiyor
        if (registerCheckBox.isChecked() == false || registerEmailEditText.getText().toString().trim().length() == 0 || registerPasswordEditText.getText().toString().trim().length() == 0) {
            Toast.makeText(this, R.string.registerError, Toast.LENGTH_SHORT).show();
        } else {

            progressBar.setVisibility(ProgressBar.VISIBLE);
            // Tıklamayı önleme
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // register işlemi yapılacak
            //Toast.makeText(this, "Thanks", Toast.LENGTH_SHORT).show();
            mAuth.createUserWithEmailAndPassword(registerEmailEditText.getText().toString(), registerPasswordEditText.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, R.string.registerSuccess, Toast.LENGTH_SHORT).show();
                        //Tıklamayı geri verme
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        // kullanıcıyı database'e de kaydetme(messages kısmı için gerekti)
                        String displayName ;
                        displayName = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                        User newUser = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), displayName);
                        usersDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(newUser);

                        // Display name set ediyoruz
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(displayName)
                                //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("updateProfile","Complete");
                                        }
                                    }
                                });



                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        StorageReference profileImagesRef = storageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/drawable/defaultpic");
                        UploadTask uploadTask = profileImagesRef.putFile(uri);
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, R.string.registerFail, Toast.LENGTH_SHORT).show();
                                Log.d("REGISTER ACT FAIL :", e.getLocalizedMessage() );
                            }
                        });

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this, R.string.registerFail, Toast.LENGTH_SHORT).show();
                        //Tıklamayı geri verme
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            });
        }
    }
}
