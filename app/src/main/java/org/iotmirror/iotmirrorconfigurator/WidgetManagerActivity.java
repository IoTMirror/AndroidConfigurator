package org.iotmirror.iotmirrorconfigurator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WidgetManagerActivity extends AppCompatActivity implements ICancelButtonListener, IWidgetStateListener {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_manager);
    }

    @Override
    public void onCancelButton()
    {
        finish();
    }

    @Override
    public void widgetStateChanged()
    {
        WidgetManagerFragment f = (WidgetManagerFragment)
                getSupportFragmentManager().findFragmentById(R.id.widgetManagerFragment);
        f.widgetStateChanged();
    }
}
