package com.example.tuyucel.activship;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class CustomAdapter extends ArrayAdapter{
    /*if else yapısını kullanarak sistemdeki güncel kullanıcının e-posta adresi ile mesajlardaki e-posta
    aynı olduğunda right_item_layout u kullan, farklı olduğunda left_item_layout u kullan diyoruz.*/
    private FirebaseUser firebaseUser;
    public CustomAdapter(@NonNull Context context, ArrayList<Message> chatList, FirebaseUser firebaseUser) {
        super(context, 0 , chatList);
        this.firebaseUser = firebaseUser;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        Message message = (Message) getItem(position);
        if (firebaseUser.getEmail().equalsIgnoreCase(message.getGonderici())){

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.right_item_layout,
                    parent, false);

            TextView txtUser = convertView.findViewById(R.id.txtUserRight);
            TextView txtMessage = convertView.findViewById(R.id.txtMessageRight);
            TextView txtTime = convertView.findViewById(R.id.txtTimeRight);

            txtUser.setText(message.getGonderici());
            txtMessage.setText(message.getMesajText());
            txtTime.setText(message.getZaman());

        }else{

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.left_item_layout,
                    parent, false);

            TextView txtUser = convertView.findViewById(R.id.txtUserLeft);
            TextView txtMessage = convertView.findViewById(R.id.txtMessageLeft);
            TextView txtTime = convertView.findViewById(R.id.txtTimeLeft);

            txtUser.setText(message.getGonderici());
            txtMessage.setText(message.getMesajText());
            txtTime.setText(message.getZaman());

        }
        return  convertView;
    }
}
