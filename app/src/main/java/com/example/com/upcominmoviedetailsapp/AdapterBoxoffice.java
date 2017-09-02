package com.example.com.upcominmoviedetailsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

/**
 * Created by AAMIT on 8/30/2017.
 */
public class AdapterBoxoffice extends RecyclerView.Adapter<AdapterBoxoffice.ViewholderBoxoffice> {
    Context context;
    private static MyClickListener myClickListener;
    ArrayList<Movielistbean> arrayList;
    ImageLoader imageLoader;
    AppController appController;
    LruBitmapCache lruBitmapCache;
    String url = "https://image.tmdb.org/t/p/w500";
    AdapterBoxoffice(Context context,ArrayList<Movielistbean> arrayList){
        this.context = context;
        this.arrayList = arrayList;
        appController = AppController.getInstance();
        imageLoader = appController.getImageLoader();
    }

    @Override
    public ViewholderBoxoffice onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.movielistlayout,parent,false);
        ViewholderBoxoffice viewholderBoxoffice =new ViewholderBoxoffice(view);
        return viewholderBoxoffice;
    }

    @Override
    public void onBindViewHolder(final ViewholderBoxoffice holder, int position) {

        holder.movieTitle.setText(arrayList.get(position).getTitle());
        holder.movieReleaseDate.setText(arrayList.get(position).getRelease_date());

        if(arrayList.get(position).getAdult().equals("false")){
            holder.adult.setText("U/A");
        }else {
            holder.adult.setText("A");
        }
        String btmImg = arrayList.get(position).getPoster_path();
        if(btmImg != null){

            imageLoader.get(url+btmImg, new ImageLoader.ImageListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                     holder.movieThumbnail.setImageBitmap(response.getBitmap());

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewholderBoxoffice extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView movieThumbnail;
        TextView movieTitle,movieReleaseDate;
        TextView adult;
        public ViewholderBoxoffice(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            movieThumbnail = (ImageView) itemView.findViewById(R.id.movieThumbnail);
            movieTitle = (TextView) itemView.findViewById(R.id.movieTitle);
            movieReleaseDate = (TextView) itemView.findViewById(R.id.movieReleaseDate);
            adult = (TextView) itemView.findViewById(R.id.adult);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }


    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
