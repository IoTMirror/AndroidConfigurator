package org.iotmirror.iotmirrorconfigurator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IUpdateTwitter} interface
 * to handle interaction events.
 * Use the {@link TwitterSignedOutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TwitterSignedOutFragment extends Fragment implements View.OnClickListener {

    private IUpdateTwitter mListener;

    public TwitterSignedOutFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TwitterSignedOutFragment.
     */
    public static TwitterSignedOutFragment newInstance()
    {
        TwitterSignedOutFragment fragment = new TwitterSignedOutFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_twitter_signed_out, container, false);
        Button b = (Button) v.findViewById(R.id.signInButton);
        b.setOnClickListener(this);
        return v;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof IUpdateTwitter)
        {
            mListener = (IUpdateTwitter) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
            case R.id.signInButton:
            {
                Commons commons = Commons.getInstance(getContext().getApplicationContext());
                String token = commons.getSessionToken();
                if(token!=null)
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(commons.getServiceUrl()+"twitter/signin/"+token));
                    startActivity(browserIntent);
                }
                else
                {
                    if (mListener != null)
                    {
                        mListener.updateTwitter();
                    }
                }
            }
            default:
            {

            }
        }
    }
}
