package com.example.tuyucel.activship;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import de.hdodenhof.circleimageview.CircleImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity{
    /*HomeActivity de ListView de yer alan konulara tıkladığımızda ChatActivity açıldıyor,
    açılan bu sayfada şunları yaptık; HomeActivity den gönderdiğimiz değeri aldık ve ilgili referans değerine ulaştık.
    Json yapısına baktığımızda en üst kısıma “ChatSubjects” adını vermiştik önce ona ulaşıyoruz daha sonra
    içindeki konuya ( HomeActivity den gelen değer ) daha sonra da ilgili “mesaj” bloğuna erişiyoruz.*/

    private DatabaseReference dbRef;
    private FirebaseDatabase db;
    private FirebaseUser fUser;
    private ArrayList<Message> chatList = new ArrayList<>();
    private CustomAdapter customAdapter;
    private ListView listView;
    private FloatingActionButton floatingActionButton ;
    private String subject;
    private EditText inputChat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            subject = bundle.getString("subject");
            dbRef = db.getReference("ChatSubject/"+subject+"/mesaj");
            setTitle(subject);
        }
        /*addValueEventListener methodu ile veritabanında değerlerde herhangi bir değişiklik oldu mu
        diye bakıyoruz eğer olduysa ListView yapımızı güncelliyoruz. ( yeni bir mesaj eklenebilir,
        mesajın içeriğinde değişiklik olabilir)*/
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Message message = ds.getValue(Message.class);
                    chatList.add(message);
                    Log.d("VALUE",ds.getValue(Message.class).getMesajText());
                }
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                if(inputChat.getText().length()>=6){

                    long msTime = System.currentTimeMillis();
                    Date curDateTime = new Date(msTime);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd'/'MM'/'y hh:mm");
                    String datetime = formatter.format(curDateTime);
                    /*Yeni bir mesaj girildiğinde ise Message model yapısını oluşturuyoruz
                    (kullanıcıdan girdiği mesaj içeriğini, kullanıcının e-posta adresini ve mesajı
                     girdiği andaki güncel zaman dilimini kullanıyoruz ) daha sonra da DatabaseReference
                     objesini kullanarak oluşturduğumuz Message objesini veritabanına ekliyoruz.*/
                    Message message = new Message(inputChat.getText().toString(),fUser.getEmail(),datetime);
                    dbRef.push().setValue(message);
                    inputChat.setText("");

                }else{
                    Toast.makeText(getApplicationContext(),"Gönderilecek mesaj uzunluğu en az 6 karakter olmalıdır!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void init(){
        ActionBar actionBar = getSupportActionBar(); actionBar.hide();

        listView = findViewById(R.id.chatListView);
        inputChat = findViewById(R.id.inputChat);
        floatingActionButton  = findViewById(R.id.sendBtn);

        db = FirebaseDatabase.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        customAdapter = new CustomAdapter(getApplicationContext(),chatList,fUser);
        listView.setAdapter(customAdapter);
    }

}