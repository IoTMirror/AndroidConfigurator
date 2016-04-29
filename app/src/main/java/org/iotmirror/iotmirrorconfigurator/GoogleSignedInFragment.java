package org.iotmirror.iotmirrorconfigurator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IUpdateGoogle} interface
 * to handle interaction events.
 * Use the {@link GoogleSignedInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoogleSignedInFragment extends Fragment implements View.OnClickListener, Response.ErrorListener, Response.Listener<JSONObject>
{
    private String name;

    private IUpdateGoogle mListener;

    public GoogleSignedInFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GoogleSignedInFragment.
     */
    public static GoogleSignedInFragment newInstance(String name)
    {
        GoogleSignedInFragment fragment = new GoogleSignedInFragment();
        Bundle args = new Bundle();
        args.putString("name",name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.name=getArguments().getString("name");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_google_signed_in, container, false);
        Button b = (Button) v.findViewById(R.id.signOutButton);
        b.setOnClickListener(this);
        if(name!=null)
        {
            TextView nameView = (TextView) v.findViewById(R.id.nameTextView);
            nameView.setText(name);
        }
        return v;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof IUpdateGoogle)
        {
            mListener = (IUpdateGoogle) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement IUpdateGoogle");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.signOutButton:
            {
                Commons commons = Commons.getInstance(getContext().getApplicationContext());
                String token = commons.getSessionToken();
                if(token!=null)
                {
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                            commons.getServiceUrl()+"google/signout/"+token,null,this,this);
                    commons.getRequestQueue().add(request);
                }
                else
                {
                    if (mListener != null)
                    {
                        mListener.updateGoogle();
                    }
                }
            }
            default:
            {

            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error)
    {
        if (mListener != null)
        {
            mListener.updateGoogle();
        }
    }

    @Override
    public void onResponse(JSONObject response)
    {
        if (mListener != null)
        {
            mListener.updateGoogle();
        }
    }
}
