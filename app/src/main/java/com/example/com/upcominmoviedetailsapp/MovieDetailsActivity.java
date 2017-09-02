package com.example.com.upcominmoviedetailsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity  implements ViewPager.OnPageChangeListener, View.OnClickListener{

    LinearLayout pager_indicator;
    TextView title,overview,developedby;
    RatingBar rating;
    private int dotsCount;
    private ImageView[] dots;
    String movieid;
    final List<String> imagearray = new ArrayList<>();
    String url;
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        movieid = getIntent().getStringExtra("MovieID");
        url = "https://api.themoviedb.org/3/movie/"+movieid+"?api_key=b7cd3340a794e5a2f35e3abb820b497f";
        ArrayList<String> list = new ArrayList();
        title = (TextView) findViewById(R.id.movietitlename);
        overview = (TextView) findViewById(R.id.overview);
        developedby = (TextView) findViewById(R.id.developedby);
        developedby.setOnClickListener(this);
        rating = (RatingBar) findViewById(R.id.rating);
        getimagerequest();
    }

    private void getimagerequest() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        String url = "https://api.themoviedb.org/3/movie/"+movieid+"/images?api_key=b7cd3340a794e5a2f35e3abb820b497f";
        final String imageurl = "https://image.tmdb.org/t/p/w500";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ViewPager viewPager = null;

                            JSONArray jsonArray = response.getJSONArray("backdrops");
                            if (jsonArray.length() == 0) {
                                jsonArray=response.getJSONArray("posters");
                            }
                            for( i=0;i<jsonArray.length();i++){
                                JSONObject jsonobject = jsonArray.getJSONObject(i);
                                String file_path =  jsonobject.getString("file_path");
                                imagearray.add(imageurl+file_path);
                            }
                            viewPager = (ViewPager) findViewById(R.id.pager_introduction);
                            pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
                            viewPager.setAdapter(new CustomPagerAdapter(MovieDetailsActivity.this,imagearray));
                            viewPager.setCurrentItem(0);
                            viewPager.setOnPageChangeListener(MovieDetailsActivity.this);
                            setUiPageViewController();
                            getmoviedetailsdata();

                            Log.d("Response", response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Response", "Error: " + error.getMessage());
                // hide the progress dialog
                pDialog.hide();

            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void getmoviedetailsdata() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String overviewtext = response.getString("overview");
                            String titletext = response.getString("title");
                            float popularity = Float.parseFloat(response.getString("popularity"));
                            title.setText(titletext);
                            overview.setText(overviewtext);
                            rating.setRating(popularity/20.0F);
                            Log.d("Response", response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Response", "Error: " + error.getMessage());
                // hide the progress dialog
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void setUiPageViewController() {
        dotsCount = 5;
        if(imagearray.size()<5){
            dotsCount = imagearray.size();
        }
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 0, 4, 0);
            pager_indicator.addView(dots[i], params);
        }
        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(MovieDetailsActivity.this,DeveloperInfo.class));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        String url;
        List<String> imagearray;

        public CustomPagerAdapter(Context context,List<String> imagearray) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.imagearray = imagearray;
        }

        @Override
        public int getCount() {
            int count = 5;
            if(imagearray.size()<5){
                count = imagearray.size();
            }
            return count;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

            final  ImageView imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);

            AppController.getInstance().getImageLoader().get(imagearray.get(position), new ImageLoader.ImageListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    imageView.setImageBitmap(response.getBitmap());

                }
            });
            //  imageView.setImageResource(mResources[position]);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}