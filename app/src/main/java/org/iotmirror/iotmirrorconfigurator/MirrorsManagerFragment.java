package org.iotmirror.iotmirrorconfigurator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MirrorsManagerFragment extends Fragment implements View.OnClickListener{


    public MirrorsManagerFragment() {
        // Required empty public constructor
    }


    public static MirrorsManagerFragment newInstance() {
        MirrorsManagerFragment fragment = new MirrorsManagerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mirrors_manager, container, false);
        Button addMirrorButton = (Button) v.findViewById(R.id.addMirrorButton);
        addMirrorButton.setOnClickListener(this);
        loadMirrors();
        return v;
    }

    private void loadMirrors()
    {
        Commons commons = Commons.getInstance(getContext());
        String token = commons.getSessionToken();
        if(token!=null)
        {
            GetMirrorsResponseHandler handler = new GetMirrorsResponseHandler();
            JsonArrayRequest request = new JsonArrayRequest(commons.getServiceUrl()+"mirrors/"+token,handler,handler);
            commons.getRequestQueue().add(request);
        }
    }

    @Override
    public void onClick(View view) {
        EditText mirrorIDEdit = (EditText) getView().findViewById(R.id.mirrorIDEdit);
        if(mirrorIDEdit!=null)
        {
            int mirrorID=Integer.parseInt(mirrorIDEdit.getText().toString());
            Commons commons = Commons.getInstance(getContext());
            String token = commons.getSessionToken();
            if(token!=null)
            {
                AddMirrorResponseHandler handler = new AddMirrorResponseHandler(mirrorID);
                JSONObject content = new JSONObject();
                try {
                    content.put("DeviceId",mirrorID);
                    content.put("token",token);
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,commons.getServiceUrl()+"mirrors",content,handler,handler);
                    commons.getRequestQueue().add(request);
                } catch (JSONException e) {
                }
            }
        }

    }

    private void addMirror(int id)
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        MirrorFragment mf = MirrorFragment.newInstance(id);
        ft.add(R.id.mirrors,mf);
        ft.commit();
    }

    private class GetMirrorsResponseHandler implements Response.Listener<JSONArray>, Response.ErrorListener {

        @Override
        public void onResponse(JSONArray response)
        {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            for(Fragment f : fm.getFragments())
            {
                if(f instanceof MirrorFragment)
                {
                    ft.remove(f);
                }
            }
            ft.commit();
            for(int i=0; i<response.length();++i)
            {
                try {
                    addMirror(response.getInt(i));
                } catch (JSONException e) {

                }
            }
        }

        @Override
        public void onErrorResponse(VolleyError error)
        {
            Toast.makeText(getContext(),"Could not load mirrors list",Toast.LENGTH_SHORT).show();
        }
    }

    private class AddMirrorResponseHandler implements Response.Listener<JSONObject>, Response.ErrorListener
    {
        private int mirrorID;

        public AddMirrorResponseHandler(int mirrorID)
        {
            this.mirrorID=mirrorID;
        }

        @Override
        public void onErrorResponse(VolleyError error)
        {
            Toast.makeText(getContext(),"Could not add mirror",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(JSONObject response)
        {
            addMirror(mirrorID);
        }
    }
}
