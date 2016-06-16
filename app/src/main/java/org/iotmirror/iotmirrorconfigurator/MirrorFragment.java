package org.iotmirror.iotmirrorconfigurator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MirrorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MirrorFragment extends Fragment implements View.OnClickListener{

    private int mirrorID;

    public MirrorFragment() {
        // Required empty public constructor
    }

    public static MirrorFragment newInstance(int mirrorID) {
        MirrorFragment fragment = new MirrorFragment();
        Bundle args = new Bundle();
        args.putInt("mirrorID", mirrorID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mirrorID = getArguments().getInt("mirrorID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mirror, container, false);
        TextView mirrorIDText = (TextView) v.findViewById(R.id.mirrorID);
        mirrorIDText.setText(""+mirrorID);
        Button deleteButton = (Button) v.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);
        Button startTeachingButton = (Button) v.findViewById(R.id.startTeachingButton);
        startTeachingButton.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        if(view==null)return;
        if(view.getId()==R.id.deleteButton)
        {
            Commons commons = Commons.getInstance(getContext());
            String token = commons.getSessionToken();
            if(token!=null)
            {
                DeleteMirrorResponseHandler handler = new DeleteMirrorResponseHandler();
                JSONObject content = new JSONObject();
                try {
                    content.put("DeviceId",mirrorID);
                    content.put("token",token);
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,commons.getServiceUrl()+"mirrors/delete",content,handler,handler);
                    commons.getRequestQueue().add(request);
                } catch (JSONException e) {
                }
            }
        }
        else if(view.getId()==R.id.startTeachingButton)
        {
            Commons commons = Commons.getInstance(getContext());
            String token = commons.getSessionToken();
            if(token!=null)
            {
                StartTeachingResponseHandler handler = new StartTeachingResponseHandler();
                JSONObject content = new JSONObject();
                try {
                    content.put("DeviceId",mirrorID);
                    content.put("token",token);
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,commons.getServiceUrl()+"teaching",content,handler,handler);
                    commons.getRequestQueue().add(request);
                } catch (JSONException e) {
                }
            }
        }
    }

    public void deleteMirror()
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(this);
        ft.commit();
    }

    private class DeleteMirrorResponseHandler implements Response.Listener<JSONObject>, Response.ErrorListener
    {

        @Override
        public void onErrorResponse(VolleyError error)
        {
            Toast.makeText(getContext(),"Could not delete mirror",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(JSONObject response)
        {
            deleteMirror();
        }
    }

    private class StartTeachingResponseHandler implements Response.Listener<JSONObject>, Response.ErrorListener
    {

        @Override
        public void onErrorResponse(VolleyError error)
        {
            Toast.makeText(getContext(),"Could not start teaching session",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(JSONObject response)
        {
            Toast.makeText(getContext(),"Teaching session started",Toast.LENGTH_SHORT).show();
        }
    }
}
