package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    // activity_main içindeki xml - > EditText tanımlaması
    EditText edUserEmail, edUserPassword;
    // firebase mAuth tanımlama
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // EditText id atamaları
        edUserEmail = findViewById(R.id.edUserEmail);
        edUserPassword = findViewById(R.id.edUserPassword);
        // mAuth nesne oluşturma
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user !=  null){
            Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
            startActivity(intent);
        }

    }
    // kullanıcı kayıt olma btn
    public void signUp(View view) {
        // Kullanıcının email ve şifre ile kayıt olması
        // parametre - > 1.Email 2.şifre
        mAuth.createUserWithEmailAndPassword(edUserEmail.getText().toString(), edUserPassword.getText().toString())
                // Yaptığımız işlem başarılı ise :
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // iş tamamlandıysa
                        if (task.isComplete()) {
                           // o anki kullanıcıyı al
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Test için kullanıcı e mail yazdırma
                            String userEmail = user.getEmail();
                            System.out.println("user e-mail : " + userEmail);
                            Toast.makeText(MainActivity.this, "User Created", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                            startActivity(intent);

                        }
                        // kayıt başarısız ise
                        else {
                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e != null){
                    Toast.makeText(MainActivity.this, "Kayıt esnasında bir hata oluştu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void signIn(View view) {

        mAuth.signInWithEmailAndPassword(edUserEmail.getText().toString(),edUserPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    if (e != null){
                        Toast.makeText(MainActivity.this, "Giriş yaparken bir hata oluştu", Toast.LENGTH_SHORT).show();
                    }
            }
        });

    }


}
