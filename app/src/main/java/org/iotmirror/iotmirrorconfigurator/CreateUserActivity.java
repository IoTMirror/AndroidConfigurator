package org.iotmirror.iotmirrorconfigurator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateUserActivity extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject> {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
    }

    protected void createUser(View view)
    {
        EditText loginET = (EditText) findViewById(R.id.login);
        EditText passwordET = (EditText) findViewById(R.id.password);
        Commons commons = Commons.getInstance(getApplicationContext());
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("username",loginET.getText().toString());
            requestData.put("password",passwordET.getText().toString());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,commons.getServiceUrl()+"user",requestData,this,this);
            commons.getRequestQueue().add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void cancel(View view)
    {
        finish();
    }

    @Override
    public void onResponse(JSONObject response)
    {
        Toast.makeText(getApplicationContext(),"User created",Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onErrorResponse(VolleyError error)
    {
        if(error.networkResponse!=null && error.networkResponse.statusCode==409 & error.networkResponse.data!=null)
        {
            try {
                JSONObject response = new JSONObject(new String(error.networkResponse.data));
                String result = response.getString("error");
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),"Unknown response",Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Unknown response",Toast.LENGTH_SHORT).show();
        }
    }
}
