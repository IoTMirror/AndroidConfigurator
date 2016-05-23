package org.iotmirror.iotmirrorconfigurator;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 */
public class WidgetManagerFragment extends Fragment implements View.OnClickListener, IWidgetStateListener{

    public class DataResponseHandler implements Response.ErrorListener, Response.Listener<JSONObject>
    {

        @Override
        public void onErrorResponse(VolleyError error)
        {
            Toast.makeText(getContext(),"error",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(JSONObject response)
        {
            widgets = new Vector<>();
            if(fragments!=null)
            {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                for(Fragment f : fragments)
                {
                    ft.remove(f);
                }
                ft.commit();
            }
            fragments = new Vector<>();
            try {
                JSONArray widgetsArray = response.getJSONArray("Widgets");
                for(int i=0; i<widgetsArray.length();++i)
                {
                    JSONObject widget = widgetsArray.getJSONObject(i);
                    int color = Color.WHITE;
                    String name = widget.getString("WidgetName");
                    int column = widget.getJSONObject("WidgetPosition").getInt("X");
                    int row = widget.getJSONObject("WidgetPosition").getInt("Y");
                    int colSpan = widget.getJSONObject("WidgetSize").getInt("X");
                    int rowSpan = widget.getJSONObject("WidgetSize").getInt("Y");
                    switch(name)
                    {
                        case "Twitter":
                        {
                            color = ContextCompat.getColor(getContext(),R.color.twitter);
                            break;
                        }
                        case "Gmail":
                        {
                            color = ContextCompat.getColor(getContext(),R.color.gmail);
                            break;
                        }
                        case "GoogleTasks":
                        {
                            color = ContextCompat.getColor(getContext(),R.color.tasks);
                            break;
                        }
                        case "GoogleCalendar":
                        {
                            color = ContextCompat.getColor(getContext(),R.color.calendar);
                            break;
                        }

                    }
                    Widget w = new Widget(name,column,row,colSpan,rowSpan,color);
                    widgets.add(w);
                    if(fragments!=null)
                    {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        WidgetInfoFragment f = WidgetInfoFragment.newInstance(w);
                            ft.add(R.id.widgetsInfo,f);
                        ft.commit();
                        fragments.add(f);
                    }
                }
            } catch (JSONException e) {
                Toast.makeText(getContext(),"Invalid server response",Toast.LENGTH_SHORT).show();
            }
            reloadWidgets();
        }
    }

    public class UpdateResponseHandler implements Response.ErrorListener, Response.Listener<JSONObject>
    {
        private Widget widget;
        UpdateResponseHandler(Widget widget)
        {
            this.widget = widget;
        }

        @Override
        public void onErrorResponse(VolleyError error)
        {
            Toast.makeText(getContext(),"Saving "+widget.getName()+" widget has failed.",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(JSONObject response)
        {
            Toast.makeText(getContext(),widget.getName()+" widget saved.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    Vector<Widget> widgets;
    Vector<Fragment> fragments;
    ICancelButtonListener mListener;

    public WidgetManagerFragment() {
        widgets = null;
    }

    public static WidgetManagerFragment newInstance()
    {
        return new WidgetManagerFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_widget_manager, container, false);
        Button sB = (Button) v.findViewById(R.id.saveButton);
        Button cB = (Button) v.findViewById(R.id.cancelButton);
        if(sB!=null)
        {
            sB.setOnClickListener(this);
        }
        if(cB!=null)
        {
            cB.setOnClickListener(this);
        }
        loadData();
        return v;
    }

    public void loadData()
    {
        Commons commons = Commons.getInstance(getContext());
        String token = commons.getSessionToken();
        if(token!=null)
        {
            DataResponseHandler handler = new DataResponseHandler();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    commons.getServiceUrl()+"widgets/"+token,null,handler,handler);
            commons.getRequestQueue().add(request);
        }
    }

    public void reloadWidgets()
    {
        WidgetManagerView wmv = (WidgetManagerView) getView().findViewById(R.id.widget_manager);
        wmv.removeWidgets();
        if(widgets !=null)
        {
            for(Widget w : widgets)
            {
                if(w.isActive())
                {
                    wmv.addWidget(w);
                }
            }
        }
    }

    public void saveData()
    {
        Commons commons = Commons.getInstance(getContext());
        String token = commons.getSessionToken();
        if(widgets !=null)
        {
            for(Widget w : widgets)
            {
                UpdateResponseHandler handler = new UpdateResponseHandler(w);
                JSONObject json = new JSONObject();
                try {
                    json.put("widget",w.getName());
                    json.put("token",token);
                    if(w.isActive())
                    {
                        json.put("x",w.getColumn());
                        json.put("y",w.getRow());
                        json.put("width",w.getColSpan());
                        json.put("height",w.getRowSpan());
                    }
                    else
                    {
                        json.put("delete","True");
                    }
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                            commons.getServiceUrl()+"widgets",json,handler,handler);
                    commons.getRequestQueue().add(request);
                } catch (JSONException e) {
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view==getView().findViewById(R.id.saveButton))
        {
            saveData();
        }
        else
        {
            if(mListener!=null)mListener.onCancelButton();
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof ICancelButtonListener)
        {
            mListener = (ICancelButtonListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                    + " must implement ICancelButtonListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void widgetStateChanged() {
        reloadWidgets();
    }
}
