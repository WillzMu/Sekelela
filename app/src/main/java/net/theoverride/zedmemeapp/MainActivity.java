package net.theoverride.zedmemeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

import static net.theoverride.zedmemeapp.R.id.imageView;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final static String DB_URL = "<link-to-database>";
    RecyclerView recyclerView;
    final static String TAG= "onResume";
    FirebaseClient firebaseClient;
    Firebase firebase;
    ArrayList<MyItem> myItems = new ArrayList<>();
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Firebase.setAndroidContext(this);
        firebase = new Firebase(DB_URL);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        firebaseClient = new FirebaseClient(this,recyclerView,DB_URL);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        showProgressDialog(getString(R.string.loading));
        new  loadBack().execute();

    }

    private void loadMemes(){
        firebase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getUpdates(dataSnapshot);
                dismissProgressDialog();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                getUpdates(dataSnapshot);
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
    private class loadBack extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {

           loadMemes();

            return null;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume called");
    loadMemes();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "For some funny Zambia Memes, download the Sekelela App!");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       // if (id == R.id.nav_user_profile) {
          //  Intent intent = new Intent(MainActivity.this, EditUserProfile.class);
           // startActivity(intent);
      //  }
     if (id == R.id.nav_post_meme) {
            Intent intent = new Intent(MainActivity.this, PostMeme.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            adapter = new Adapter(this,myItems);
            recyclerView.setAdapter(adapter);
        }else{
            Toast.makeText(this,"NO DATA", Toast.LENGTH_SHORT).show();
        }
    }




}
