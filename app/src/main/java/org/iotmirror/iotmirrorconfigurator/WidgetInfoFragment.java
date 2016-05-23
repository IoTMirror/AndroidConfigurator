package org.iotmirror.iotmirrorconfigurator;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class WidgetInfoFragment extends Fragment implements View.OnClickListener{

    protected Widget widget;
    IWidgetStateListener mListener;

    public WidgetInfoFragment()
    {
    }

    public static WidgetInfoFragment newInstance(Widget w) {

        Bundle args = new Bundle();
        args.putSerializable("widget",w);
        WidgetInfoFragment fragment = new WidgetInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            widget = (Widget) getArguments().getSerializable("widget");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_widget_info, container, false);
        CheckBox active = (CheckBox) v.findViewById(R.id.activeCheckbox);
        TextView name = (TextView) v.findViewById(R.id.widgetNameTextView);
        active.setOnClickListener(this);
        if(widget!=null)
        {
            active.setChecked(widget.isActive());
            active.setTextColor(widget.getColor());
            name.setText(widget.getName());
            name.setTextColor(widget.getColor());
        }
        return v;
    }

    @Override
    public void onClick(View view)
    {
        if(widget!=null)
        {
            widget.setActive(((CheckBox) getView().findViewById(R.id.activeCheckbox)).isChecked());
            if(mListener!=null)mListener.widgetStateChanged();
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof IWidgetStateListener)
        {
            mListener = (IWidgetStateListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                    + " must implement IWidgetStateListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }
}
