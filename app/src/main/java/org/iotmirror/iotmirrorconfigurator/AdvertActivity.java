package org.iotmirror.iotmirrorconfigurator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class AdvertActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advert);
        setAdvert();
    }

    protected void setAdvert() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2643006952981574/2079476040");

        Commons commons = Commons.getInstance(getApplicationContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                commons.getServiceUrl()+"ad_keywords/" + commons.getSessionToken(),null,this,this);
        commons.getRequestQueue().add(request);
    }

    @Override
    public void onResponse(JSONObject response) {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        try {
            JSONArray keywords = response.getJSONArray("keywords");

            for(int i = 0; i < keywords.length(); i++){
                String keyword = keywords.getString(i);
                adRequestBuilder.addKeyword(keyword);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AdRequest adRequest = adRequestBuilder.build();

        mAdView.loadAd(adRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if(error.networkResponse!=null && error.networkResponse.statusCode==400 & error.networkResponse.data!=null)
        {
            try {
                JSONObject response = new JSONObject(new String(error.networkResponse.data));
                String result = response.getString("Result");
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),"Unknown response",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void goNext(View view) {
        Intent intent = new Intent(getApplicationContext(),ConfigActivity.class);
        startActivity(intent);
        finish();
    }
}
