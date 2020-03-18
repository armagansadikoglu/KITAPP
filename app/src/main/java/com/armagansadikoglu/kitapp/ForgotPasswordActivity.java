package com.armagansadikoglu.kitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.net.PortUnreachableException;

public class ForgotPasswordActivity extends AppCompatActivity {
    Button resetMyPasswordButton;
    EditText forgotPasswordEmailEditText;
    ProgressBar forgotProgressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mAuth = FirebaseAuth.getInstance();
        forgotPasswordEmailEditText = findViewById(R.id.forgotPasswordEmailEditText);
        resetMyPasswordButton = findViewById(R.id.resetMyPasswordButton);
        forgotProgressBar = findViewById(R.id.forgotPasswordProgressBar);
    }

    public void resetMyPasswordButtonOnClick(View view){
        // Klavyeyi kapatma -- yoksa yazmaya devam eder
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        if (forgotPasswordEmailEditText.getText().toString().trim().length() == 0){
            Toast.makeText(this, R.string.registerError, Toast.LENGTH_SHORT).show();
        }else {
            forgotProgressBar.setVisibility(ProgressBar.VISIBLE);
            // Tıklamayı önleme
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mAuth = FirebaseAuth.getInstance();
            String emailAddress = forgotPasswordEmailEditText.getText().toString();

            mAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    forgotProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    //Tıklamayı geri verme
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, R.string.forgotPasswordMailSent, Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ForgotPasswordActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
