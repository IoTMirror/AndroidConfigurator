package org.iotmirror.iotmirrorconfigurator;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfigActivity extends AppCompatActivity implements IUpdateTwitter, Response.ErrorListener, Response.Listener<JSONObject>
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        updateTwitter();
    }

    public void logout(View v)
    {
        doLogout();
    }

    public void doLogout()
    {
        Commons.getInstance(getApplicationContext()).logout();
        finish();
    }

    @Override
    public void updateTwitter()
    {
        Commons commons = Commons.getInstance(getApplicationContext());
        String token = commons.getSessionToken();
        if(token!=null)
        {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    commons.getServiceUrl()+"twitter/users/"+token,null,this,this);
            commons.getRequestQueue().add(request);
        }
        else
        {
            doLogout();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error)
    {
        if(error.networkResponse!=null)
        {
            String message="";
            switch(error.networkResponse.statusCode)
            {
                case 401:
                {
                    message="User tokens invalid - not signed in";
                    break;
                }
                case 404:
                {
                    message="User not found - not signed in";
                    break;
                }
                case 429:
                {
                    message="Too many requests";
                    break;
                }
                default:
                {
                    message="Unknown response";
                }
            }
            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.twitterContainer, TwitterSignedOutFragment.newInstance());
        ft.commit();
    }

    @Override
    public void onResponse(JSONObject response)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        String name = "";
        try {
            name = response.get("name") + " (" + response.getString("screen_name")+")";
        } catch (JSONException e) {
        }
        ft.replace(R.id.twitterContainer,TwitterSignedInFragment.newInstance(name));
        ft.commit();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateTwitter();
    }
}
