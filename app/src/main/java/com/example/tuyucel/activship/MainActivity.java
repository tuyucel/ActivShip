package com.example.tuyucel.activship;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    /*Uygulamamızda ilk çalışacak olan MainActivity sınıfımız. Eğer giriş yapmış ve
    firebase sistemine authenticate olmuş bir kullanıcı var ise direk HomeActivity açtırıyoruz.
    Login butonuna bastığımızda ise signInWithEmailAndPassword methodu kullanıcıdan email adresi
    ile parola alıyor ve login isteğinde bulunuyor.
    Daha sonra addOnCompleteListener ile de işlemin başarılı olup olmadığını kontrol ediyoruz.*/
    private EditText editTextUserName;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView txtRegister;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private String userName;
    private String userPassword;
    private SignInButton googleBtn;
    private final static int RC_SIGN_IN = 67;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG ="MAIN_ACTIVITY";
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        mAuth = FirebaseAuth.getInstance();
        //firebaseUser = mAuth.getCurrentUser();

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }

        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.e("Google Login", "Kullanıcı Artık Yetkili. Kullanıcı ID : " + user.getUid());
                    startActivity(new Intent(MainActivity.this, StartActivity.class));

                }else{
                    Log.e("Google Login", "Kullanıcı Artık Yetkili Değil.");

                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this,"Sanırım bişeyler ters gitti.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        if(firebaseUser != null){
            Intent i = new Intent(MainActivity.this, StartActivity.class);
            startActivity(i);
            finish();
        }

     buttonLogin.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             userName = editTextUserName.getText().toString();
             userPassword = editTextPassword.getText().toString();
             if (userName.isEmpty() || userPassword.isEmpty()) {
                 Toast.makeText(getApplicationContext(), "Lütfen Gerekli Alanları Doldurunuz!",
                         Toast.LENGTH_SHORT).show();
             }else{
                 login();
             }
         }
     });
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regis = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(regis);
                finish();
            }
        });
    }
    // Google giriş fonksiyonları
    private void signIn() {
        Intent signInIntent =  Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                Log.e("Google Login", "Oturum açılıyor..");
                GoogleSignInAccount account = result.getSignInAccount();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                        mAuth.signInWithCredential(credential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            Log.e("Google Login","Oturum Google hesabı ile açıldı");
                                        }else{
                                            Log.e("Google Login","Oturum açılmadı.", task.getException());
                                            Toast.makeText(MainActivity.this,"hata.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
            }else{
                Log.e("Google Login", "Google hesabıyla oturum açma isteği yapılamadı.");
            }
            /*Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);*/

        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            updateUI(account);
        } catch (ApiException e) {

            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }

    }
    public void login(){
        mAuth.signInWithEmailAndPassword(userName,userPassword).addOnCompleteListener(MainActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent i = new Intent(MainActivity.this,StartActivity.class);
                            startActivity(i);
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void init(){
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextPassword = findViewById(R.id.editTextUserPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        txtRegister = findViewById(R.id.txtRegister);
        googleBtn = findViewById(R.id.googleBtn);

    }



    private void updateUI(GoogleSignInAccount account) {
    }
    }

