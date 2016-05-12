package org.iotmirror.iotmirrorconfigurator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class OAuthSigninActivity extends AppCompatActivity implements OAuthSigninListener
{

    public static Intent getNewIntent(Context context, Uri uri)
    {
        Intent browserIntent = new Intent(context,OAuthSigninActivity.class);
        browserIntent.putExtra("uri",uri.toString());
        return browserIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth_signin);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Uri uri = Uri.parse(getIntent().getStringExtra("uri"));
        ft.replace(R.id.oauth_container,OAuthSigninFragment.newInstance(uri));
        ft.commit();
    }

    @Override
    public void oauthFinished()
    {
        finish();
    }
}
