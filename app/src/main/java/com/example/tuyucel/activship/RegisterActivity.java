package com.example.tuyucel.activship;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    /*Kodlara baktığımzda createUserWithEmailAndPassword methodu kullanıcı adını ( kullanıcı email adresi ) ile
     parolasını alıyor yine addOnCompleteListener ile de işlemin başarılı olup olmadığını kontrol
     ediyoruz eğer register işleminde sorun yok ise kullanıcıyı MainActivity sayfasına yönlendiriyoruz.*/

    private EditText registerUsername,registerPassword;
    private Button buttonRegister;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private String userName,userPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerUsername = findViewById(R.id.registerUsername);
        registerPassword = findViewById(R.id.registerPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        mAuth = FirebaseAuth.getInstance();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = registerUsername.getText().toString();
                userPassword = registerPassword.getText().toString();
                if (userName.isEmpty() || userPassword.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Lütfen Gerekli Alanları Doldurunuz!",
                            Toast.LENGTH_SHORT).show();
                }else{
                    register();
                }
            }
        });
    }

    public void register(){
        mAuth.createUserWithEmailAndPassword(userName,userPassword)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    dbRef = FirebaseDatabase.getInstance().getReference("users/"+uId);
                    User user = new User(userName,uId);
                    dbRef.setValue(user);
                    Intent i = new Intent(RegisterActivity.this,StartActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
