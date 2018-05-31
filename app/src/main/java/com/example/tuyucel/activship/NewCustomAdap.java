package com.example.tuyucel.activship;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;


    public class NewCustomAdap extends ArrayAdapter<User> {

        private Context context;
        private List<User> userList;
        public NewCustomAdap (Context context, List<User> users){
            super(context, 0, users);
            this.context = context;
            this.userList = users;
        }

        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View v = convertView;
            final ViewHolder holder;

            if (v == null) {
                LayoutInflater vi =
                        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.custom_item, null);
                holder = new ViewHolder();
                holder.userName = v.findViewById(R.id.userNameTxt);
                holder.userImage = v.findViewById(R.id.userImage);
                v.setTag(holder);
            }
            else {
                holder = (ViewHolder) v.getTag();
            }

            User user = userList.get(position);
            holder.userName.setText(user.getUserName());
            FirebaseStorage fStorage = FirebaseStorage.getInstance();
            StorageReference storageRef = fStorage.getReference().child("users").child(user.getuId());
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    //Picasso.with(context).load(uri).fit().centerCrop().into(holder.userImage);
                    Glide.with(context)
                            .load(uri)
                            .asBitmap()
                            .centerCrop()
                            .into(new SimpleTarget<Bitmap>(200,200) {
                                @Override
                                public void onResourceReady(Bitmap resource,GlideAnimation glideAnimation) {
                                    holder.userImage.setImageBitmap(resource);
                                }
                            });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                }
            });

            return v;
        }
        static class ViewHolder {
            TextView userName;
            ImageView userImage;
        }

    }