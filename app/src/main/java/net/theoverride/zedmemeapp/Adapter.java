package net.theoverride.zedmemeapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public  class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements View.OnClickListener{
    Context context;
    ArrayList<MyItem> myItems;
    private boolean isButtonClicked = false;
    FirebaseClient firebaseClient;
    RecyclerView recyclerView;
    DatabaseReference firebase;
    final static String DB_URL = "https://zedmememainactivity.firebaseio.com/";
    public Adapter(Context context, ArrayList<MyItem> myItems) {
        this.context = context;
        this.myItems = myItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView meme_comment,commentNumber,upvoteNumber,key;
        ImageView imageView;
        View mView;
        ImageView mLikeIcon;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            meme_comment = (TextView) itemView.findViewById(R.id.meme_comment);
            commentNumber = (TextView) itemView.findViewById(R.id.commentNumber);
            upvoteNumber = (TextView) itemView.findViewById(R.id.upvoteNumber);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            key = (TextView) itemView.findViewById(R.id.textView3);
            Typeface myCustomFont = Typeface.createFromAsset(context.getAssets(),"fonts/SF_Cartoonist_Hand.ttf");
            meme_comment.setTypeface(myCustomFont);
            mLikeIcon = (ImageView) itemView.findViewById(R.id.post_like_icon);
            firebaseClient = new FirebaseClient(context,recyclerView,DB_URL);
            firebase = FirebaseDatabase.getInstance().getReference();

            mLikeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isButtonClicked =  !isButtonClicked;
                    if(isButtonClicked){
                        //get the key for the specific meme
                        String voe =  key.getText().toString();
                      final  String user =  FirebaseUtil.getCurrentUserId();
                        final DatabaseReference childRef = firebase.child("/meme/"+voe+"/upvoteNumber").child(user);
                        //if the user likes a meme, their UserID is added to the upvoteNumber child and
                        //the number of likes is counted by the number of USerIDs there
                        //the if statements  remove or and a user and set the heart full or empty
                        //TODO: write better statement for mLikeIcon
                        childRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                            @Override
                            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    childRef.removeValue();
                                    mLikeIcon.setImageResource(R.drawable.heart_empty);
                                }else{
                                    childRef.setValue(user);
                                    mLikeIcon.setImageResource(R.drawable.heart_full);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    else if(!isButtonClicked){
                        String voe =  key.getText().toString();
                        final  String user =  FirebaseUtil.getCurrentUserId();
                        final DatabaseReference childRef = firebase.child("/meme/"+voe+"/upvoteNumber").child(user);

                        childRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                            @Override
                            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    childRef.removeValue();
                                    mLikeIcon.setImageResource(R.drawable.heart_empty);
                                }else{
                                    childRef.setValue(user);
                                    mLikeIcon.setImageResource(R.drawable.heart_full);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                }

            });



        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.meme_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.meme_comment.setText(myItems.get(position).getMeme_comment());
        holder.key.setText(myItems.get(position).getKey());
        holder.commentNumber.setText(String.valueOf(myItems.get(position).getCommentNumber()));
        holder.upvoteNumber.setText(String.valueOf(myItems.get(position).getUpvoteNumber()));
        PicassoClient.downloadImage(context, myItems.get(position).getUrl(), holder.imageView);

    }

    @Override
    public int getItemCount() {
        return myItems.size();
    }

    @Override
    public void onClick(View v) {

    }

}

