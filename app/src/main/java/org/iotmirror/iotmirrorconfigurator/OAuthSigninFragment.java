package org.iotmirror.iotmirrorconfigurator;

import android.content.Context;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OAuthSigninListener} interface
 * to handle interaction events.
 * Use the {@link OAuthSigninFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OAuthSigninFragment extends Fragment
{

    private OAuthSigninListener mListener;
    private Uri uri;

    public class OAuthSigninWebChromeClient extends WebChromeClient
    {
        @Override
        public void onCloseWindow(WebView window)
        {
            super.onCloseWindow(window);
            mListener.oauthFinished();
        }
    }

    public class OAuthSigninWebClient extends WebViewClient
    {
        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);
            String[] url_elements = Uri.parse(url).getPath().split("/");
            if (url_elements.length > 0)
            {
                if ("signin".equals(url_elements[url_elements.length - 1].split("[?]")[0]))
                {
                    mListener.oauthFinished();
                }
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
        {
            super.onReceivedError(view, request, error);
            mListener.oauthFinished();
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse)
        {
            super.onReceivedHttpError(view, request, errorResponse);
            mListener.oauthFinished();
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
        {
            super.onReceivedSslError(view, handler, error);
            mListener.oauthFinished();
        }
    }

    public OAuthSigninFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param uri Uri to open
     *
     * @return A new instance of fragment OAuthSigninFragment.
     */
    public static OAuthSigninFragment newInstance(Uri uri)
    {
        OAuthSigninFragment fragment = new OAuthSigninFragment();
        Bundle args = new Bundle();
        args.putString("uri",uri.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            this.uri = Uri.parse(getArguments().getString("uri"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_oauth_signin, container, false);
        WebView wbv = (WebView) v.findViewById(R.id.webview);
        wbv.setWebViewClient(new OAuthSigninWebClient());
        wbv.setWebChromeClient(new OAuthSigninWebChromeClient());
        wbv.getSettings().setJavaScriptEnabled(true);
        wbv.loadUrl(uri.toString());
        return v;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OAuthSigninListener)
        {
            mListener = (OAuthSigninListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OAuthSigninListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }
}
