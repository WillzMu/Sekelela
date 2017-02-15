package net.theoverride.zedmemeapp;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class PicassoClient {

    public static void downloadImage(Context context, String url, ImageView imageView)
    {
        if(url != null && url.length()>0)
        {
            //Ion.with(imageView).placeholder(R.drawable.image2)
              //      .error(R.drawable.image2).load(url);
            Picasso.with(context)
                    .load(url)
                    .resize(900,900)
                    .centerCrop()
                    .into(imageView);

        }else{
            Picasso.with(context)
                    .load(R.drawable.ic_menu_gallery);
        }
    }
}
