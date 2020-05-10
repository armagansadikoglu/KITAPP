package com.armagansadikoglu.kitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginActivity extends AppCompatActivity {
    TextView register, forgotpassword;
    EditText loginEmail, loginPassword;
    ProgressBar loginProgressBar;
    private FirebaseAuth mAuth;

    FirebaseAuth.AuthStateListener authStateListener;

    // auth state listenerı mauth'a tanımladık.
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        loginProgressBar = findViewById(R.id.loginProgressBar);
        register = findViewById(R.id.loginRegisterTextView);
        forgotpassword = findViewById(R.id.loginForgotPasswordTextView);

        // Kullanıcıyı logged in ise MainActivity'e at
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        //int width = size.x;
        int height = size.y;


        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            // Gets linearlayout
            LinearLayout layout = findViewById(R.id.loginLinearLayout);
// Gets the layout params that will allow you to resize the layout
            ViewGroup.LayoutParams params = layout.getLayoutParams();
// Changes the height and width to the specified *pixels*
            params.height = height * 40 / 100;
            layout.setLayoutParams(params);
        } else {
            // In portrait
            // Gets linearlayout
            LinearLayout layout = findViewById(R.id.loginLinearLayout);
// Gets the layout params that will allow you to resize the layout
            ViewGroup.LayoutParams params = layout.getLayoutParams();
// Changes the height and width to the specified *pixels*
            params.height = height * 60 / 100;

            layout.setLayoutParams(params);
        }


    }

    public void loginButtonOnClick(View view) {
        // Giriş işlemi


        loginEmail = findViewById(R.id.loginEditTextEmail);
        loginPassword = findViewById(R.id.loginEditTextPassword);

        if (loginEmail.getText().toString().trim().length() == 0 || loginPassword.getText().toString().trim().length() == 0) {
            Toast.makeText(this, R.string.registerError, Toast.LENGTH_SHORT).show();
        } else {
            loginProgressBar.setVisibility(ProgressBar.VISIBLE);
            // Klavyeyi kapatma -- yoksa yazmaya devam eder
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

            // Tıklamayı önleme
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


            mAuth.signInWithEmailAndPassword(loginEmail.getText().toString(), loginPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loginProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            if (task.isSuccessful()) {
                                // FirebaseUser user = mAuth.getCurrentUser();
                                //Tıklamayı geri verme
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                //Toast.makeText(LoginActivity.this, user.getUid(), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    //Tıklamayı geri verme
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(LoginActivity.this, R.string.loginFail, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
