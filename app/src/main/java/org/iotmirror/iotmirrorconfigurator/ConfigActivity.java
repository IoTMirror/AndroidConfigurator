package org.iotmirror.iotmirrorconfigurator;

import android.content.Intent;
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

public class ConfigActivity extends AppCompatActivity implements IUpdateTwitter, IUpdateGoogle {
    public class TwitterUpdateResponseHandler implements Response.ErrorListener, Response.Listener<JSONObject> {

        @Override
        public void onErrorResponse(VolleyError error)
        {
            if (error.networkResponse != null)
            {
                String message = null;
                switch (error.networkResponse.statusCode)
                {
                    case 401:
                    {
                        break;
                    }
                    case 404:
                    {
                        break;
                    }
                    case 429:
                    {
                        message = "Twitter: too many requests";
                        break;
                    }
                    default:
                    {
                        message = "Twitter: unknown response";
                    }
                }
                if(message!=null)
                {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
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
            try
            {
                name = response.get("name") + " (" + response.getString("screen_name") + ")";
            } catch (JSONException e)
            {
            }
            ft.replace(R.id.twitterContainer, TwitterSignedInFragment.newInstance(name));
            ft.commit();
        }

    }

    public class GoogleUpdateResponseHandler implements Response.ErrorListener, Response.Listener<JSONObject>
    {

        @Override
        public void onErrorResponse(VolleyError error)
        {
            if (error.networkResponse != null)
            {
                String message = null;
                switch (error.networkResponse.statusCode)
                {
                    case 401:
                    {
                        break;
                    }
                    case 404:
                    {
                        break;
                    }
                    case 429:
                    {
                        message = "Google: too many requests";
                        break;
                    }
                    default:
                    {
                        message = "Google: unknown response";
                    }
                }
                if(message!=null)
                {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.googleContainer, GoogleSignedOutFragment.newInstance());
            ft.commit();
        }

        @Override
        public void onResponse(JSONObject response)
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            String name = "";
            try
            {
                name = response.get("name") + " <" + response.getString("email") + ">";
            } catch (JSONException e)
            {
            }
            ft.replace(R.id.googleContainer, GoogleSignedInFragment.newInstance(name));
            ft.commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        updateTwitter();
        updateGoogle();
    }

    public void logout(View v)
    {
        doLogout();
    }

    public void openWidgetManager(View v)
    {
        Intent intent = new Intent(getApplicationContext(),WidgetManagerActivity.class);
        startActivity(intent);
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
            TwitterUpdateResponseHandler handler = new TwitterUpdateResponseHandler();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    commons.getServiceUrl()+"twitter/users/"+token,null,handler,handler);
            commons.getRequestQueue().add(request);
        }
        else
        {
            doLogout();
        }
    }

    @Override
    public void updateGoogle()
    {
        Commons commons = Commons.getInstance(getApplicationContext());
        String token = commons.getSessionToken();
        if(token!=null)
        {
            GoogleUpdateResponseHandler handler = new GoogleUpdateResponseHandler();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    commons.getServiceUrl()+"google/users/"+token,null,handler,handler);
            commons.getRequestQueue().add(request);
        }
        else
        {
            doLogout();
        }
    }

    public void manageMirrors(View v)
    {
        Intent intent = new Intent(getApplicationContext(),MirrorsManagerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateTwitter();
        updateGoogle();
    }

}
