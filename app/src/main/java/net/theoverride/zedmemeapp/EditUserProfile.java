package net.theoverride.zedmemeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EditUserProfile extends AppCompatActivity {
    public static final String DEFAULT = "";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int RESULT_LOAD_IMG = 2;
    static final String TAG ="gallery";
    ImageView imageView2;
    String selectedImagePath;

    ListView listview;
    String[] EditUserProfileArray = {
            "Change Profile Picture", "Posts", "Sign Out"
    };
    TextView username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        username = (TextView) findViewById(R.id.textView5);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        SharedPreferences sharedPreferences = getSharedPreferences("username", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("name", DEFAULT);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,EditUserProfileArray);
         listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(adapter);
       listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

           @Override
           public void onItemClick(AdapterView<?> parent, View view,
                                   int position, long id) {
               if (position == 0) {
                   PopupMenu popup = new PopupMenu(EditUserProfile.this, listview);
                   //Inflating the Popup using xml file
                   popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                   //registering popup with OnMenuItemClickListener
                   popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                       public boolean onMenuItemClick(MenuItem item) {
                           //Toast.makeText(EditUserProfile.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                           switch(item.getItemId()){
                               case R.id.item1:
                                   //Toast.makeText(getApplicationContext(),"take photo",Toast.LENGTH_SHORT).show();
                                   dispatchTakePictureIntent();
                                   break;
                               case R.id.item2:
                                   Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                           android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
// Start the Intent
                                   startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                                   break;
                           }
                           return true;
                       }
                   });

                   popup.show();//showing popup menu
               }
               ;
           }

       });

    }
    //open camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    //set the just taken photo to the imageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView2.setImageBitmap(imageBitmap);
        }
        else if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMG) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri

                    Log.i(TAG, "Image Path : " + selectedImageUri);
                    // Set the image in ImageView
                    selectedImagePath = getRealPathFromURI(selectedImageUri);
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    imageView2.setImageBitmap(bitmap);
                }
            }
        }
    }
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    }
