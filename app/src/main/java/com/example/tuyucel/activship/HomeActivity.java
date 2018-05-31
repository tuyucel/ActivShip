package com.example.tuyucel.activship;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HomeActivity  extends AppCompatActivity{
    /*Oluşturduğumuz json yapısındaki gibi, chat konularımızı firebase database den çekiyoruz ve
    ListView de gösteriyoruz. Bu konuları sohbet odaları olarak da düşünebiliriz. */
    private ListView listView;
    private Button geriBtn;
    private FirebaseAuth fAuth;
    private ArrayList<String> subjectLists = new ArrayList<>();
    private FirebaseDatabase db;
    private DatabaseReference dbRef;
    private ArrayAdapter<String> adapter;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        geriBtn = findViewById(R.id.geriBtn);
        geriBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, StartActivity.class);
                startActivity(i);
                finish();
            }
        });

        fAuth = FirebaseAuth.getInstance();

        listView = findViewById(R.id.listViewSubjects);

        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("ChatSubjects");

        adapter = new ArrayAdapter<>(HomeActivity.this,
                android.R.layout.simple_list_item_1, android.R.id.text1, subjectLists);
        listView.setAdapter(adapter);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subjectLists.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    subjectLists.add(ds.getKey());

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),""+databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                intent.putExtra("subject", subjectLists.get(position));
                startActivity(intent);
            }
        });
    }
}