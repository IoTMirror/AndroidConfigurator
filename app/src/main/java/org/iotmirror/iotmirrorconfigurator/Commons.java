package org.iotmirror.iotmirrorconfigurator;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Commons {
    private static Commons instance;
    private String serviceUrl;
    private RequestQueue requestQueue;
    private String sessionToken;

    public static synchronized Commons getInstance(Context context)
    {
        if(instance==null)
        {
            instance = new Commons(context);
        }
        return instance;
    }

    private Commons(Context context)
    {
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.serviceUrl="https://put-mirror-config.herokuapp.com/";
    }

    public synchronized RequestQueue getRequestQueue()
    {
        return requestQueue;
    }

    public synchronized String getServiceUrl() {
        return serviceUrl;
    }

    public synchronized void setSessionToken(String sessionToken)
    {
        this.sessionToken = sessionToken;
    }

    public synchronized String getSessionToken()
    {
        return sessionToken;
    }

    public synchronized void login(String sessionToken)
    {
        setSessionToken(sessionToken);
    }

    public synchronized void logout()
    {
        setSessionToken(null);
    }
}
