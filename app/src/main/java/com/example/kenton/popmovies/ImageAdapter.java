package com.example.kenton.popmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Kenton on 06/05/2017.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    private String[] mImageUrls;

    public ImageAdapter(Context c, String[] imageUrls) {
        mContext = c;
        mImageUrls = imageUrls;
    }

    public String[] getImageUrls() {
        return mImageUrls;
    }

    public void setImageUrls(String[] imageUrls) {
        mImageUrls = imageUrls;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mImageUrls.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext)
                .load(mImageUrls[position])
                .into(imageView);

        return imageView;
    }

}
