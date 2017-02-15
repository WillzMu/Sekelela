package net.theoverride.zedmemeapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import static net.theoverride.zedmemeapp.MainActivity.*;

public  class FirebaseClient  {
    Context context;
    String DB_URL;
    RecyclerView rv;
    Adapter adapter;
    ImageView button;
    DatabaseReference databaseReference;


    Firebase firebase;
    ArrayList<MyItem> myItems = new ArrayList<>();


    public FirebaseClient(Context context, RecyclerView rv, String DB_URL) {
        this.context = context;
        this.rv = rv;
        this.DB_URL = DB_URL;
        //initialize
        Firebase.setAndroidContext(context);
        firebase = new Firebase(DB_URL);

    }

    //retrieve data, able to carry out the same function as that in MainActivity
    public void refreshData(){
        firebase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getUpdates(dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void getUpdates(DataSnapshot dataSnapshot){
        myItems.clear();
        for(DataSnapshot ds: dataSnapshot.getChildren())
        {

            MyItem m = new MyItem();
            m.setComments(ds.getValue(MyItem.class).getComments());
            m.setCommentNumber(ds.getValue(MyItem.class).getCommentNumber());
            m.setUrl(ds.getValue(MyItem.class).getUrl());
            m.setMeme_comment(ds.getValue(MyItem.class).getMeme_comment());
            m.setUpvoteNumber(ds.child("upvoteNumber").getChildrenCount());
            m.setKey(ds.getValue(MyItem.class).getKey());
            myItems.add(m);

        }
        if(myItems.size()>0){
            adapter = new Adapter(context,myItems);
            rv.setAdapter(adapter);
        }else{
            Toast.makeText(context,"NO DATA", Toast.LENGTH_SHORT).show();
        }
    }



}

