package net.theoverride.zedmemeapp;

import android.*;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

    public class PostMeme extends BaseActivity {
    public static final String TAG = "NewPostActivity";
    public static final String TAG_TASK_FRAGMENT = "newPostUploadTaskFragment";
    private static final int THUMBNAIL_MAX_DIMENSION = 640;
    private static final int FULL_SIZE_MAX_DIMENSION = 1280;
    private Button mSubmitButton;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    DatabaseReference firebase;
    private String memeComment = "haha";
    private int comments = 0;
    private  int upVote = 0;
    private static final String[] cameraPerms = new String[]{
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };
    ImageView imageView3;
    String selectedImagePath;
    TextView textView2;
    EditText editText;
    static final int RESULT_LOAD_IMG = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_meme);
       textView2 = (TextView) findViewById(R.id.textView2);
        editText = (EditText) findViewById(R.id.editText);
        editText.addTextChangedListener(mTextEditorWatcher);
        Firebase.setAndroidContext(this);
        firebase = FirebaseDatabase.getInstance().getReference();
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        //when the user clicks the image it opens the gallery for them to select an image. Images with a
        //high bitmap dimension(camera photos)  wont work
        //TODO: work on images from camera
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
// Start the Intent
                                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

            }

        });
        // find the retained fragment on activity restarts


    }


    //upload post to firebase
    private void newPost(String memeComment,String url,int comments,int upVote){
        String key = firebase.child("meme").push().getKey();
        firebase.child("/meme/"+key).child("meme_comment").setValue(memeComment);
        firebase.child("/meme/"+key).child("commentNumber").setValue(comments);
        firebase.child("/meme/"+key).child("upvoteNumber").setValue(upVote);
        firebase.child("/meme/"+key).child("url").setValue(url);
        firebase.child("/meme/"+key).child("key").setValue(key);
      //  String user = FirebaseUtil.getCurrentUserId();
      //  DatabaseReference childRef = firebase.child("/meme/-KcSbXfMfNQD-g8NqBjX/upvoteNumber").push();
      // childRef.setValue(memeComment);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.send, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            //noinspection SimplifiableIfStatement
            case android.R.id.home:
                //Write your logic here
                this.finish();
                break;
            case R.id.navigate:
                onClick1();
                return true;
            // startActivity(new Intent(this,Home.class));
        }
        return super.onOptionsItemSelected(item);
    }
    //called when the post button is presed
    public void onClick1(){
        final String post = textView2.getText().toString().trim();
        int postNumber = Integer.parseInt(post);
        if(postNumber<=0){
            Toast.makeText(getApplicationContext(),"sorry",Toast.LENGTH_SHORT).show();
        };
        if (imageView3 == null) {
            Toast.makeText(PostMeme.this, "Select an image first.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        final String postText = editText.getText().toString();
        if (TextUtils.isEmpty(postText)) {
            editText.setError(getString(R.string.error_required_field));
            return;
        }
        //user the BaseActivity to great a 'loading' pop-up
        showProgressDialog(getString(R.string.post_upload_progress_message));
        //Uploading to the firebase storage
        FirebaseStorage storageRef = FirebaseStorage.getInstance();
        StorageReference photoRef = storageRef.getReferenceFromUrl("<link-to-storage>");
        StorageReference mountainImagesRef = photoRef.child("images/"+postText+".jpg");
       //setting the selected Image to the imageView
        imageView3.setDrawingCacheEnabled(true);
        imageView3.buildDrawingCache();
        Bitmap bitmap = imageView3.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                dismissProgressDialog();
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                String url = String.valueOf(downloadUrl);
                newPost(postText,url,comments,upVote);
                //throw in a sound as well like facebook haha
                Toast.makeText(getApplicationContext(),"POST SUCCESSFUL!",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PostMeme.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }
   //shows how many characters are being typed in the edittext
    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            if (s.length()<120){
            textView2.setText(String.valueOf(140-s.length()));
        }else{
                textView2.setText(String.valueOf(140-s.length()));
                textView2.setTextColor(Color.parseColor("#ff0000"));
            }
        }

        public void afterTextChanged(Editable s) {
        }
    };
        //called when Gallery app opens
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView3.setImageBitmap(imageBitmap);
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

                    imageView3.setImageBitmap(bitmap);
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
