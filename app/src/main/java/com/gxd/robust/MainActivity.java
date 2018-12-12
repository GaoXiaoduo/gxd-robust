package com.gxd.robust;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.meituan.robust.PatchExecutor;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final int REQUEST_CODE_SDCARD_READ = 1;

    TextView tvTitle = null;

    Button btnModify = null;

    Button btnLoad = null;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTitle = (TextView) this.findViewById(R.id.tvTitle);
        btnModify = (Button) this.findViewById(R.id.btnModify);
        btnLoad = (Button) this.findViewById(R.id.btnLoad);
        btnModify.setOnClickListener(this);
        btnLoad.setOnClickListener(this);
    }

    @Override
    public void onClick (View v)
    {

        switch (v.getId())
        {
            case R.id.btnLoad:
                Toast.makeText(this, "button load on click", Toast.LENGTH_LONG).show();
                if (isGrantSDCardReadPermission())
                {
                    runRobust();
                }
                else
                {
                    requestPermission();
                }
                break;
            case R.id.btnModify:
                Toast.makeText(this, "button jump on click", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, RobustActivity.class));
                break;
            default:
                break;
        }
    }

    private boolean isGrantSDCardReadPermission ()
    {

        return PermissionUtils.isGrantSDCardReadPermission(this);
    }

    private void requestPermission ()
    {

        PermissionUtils.requestSDCardReadPermission(this, REQUEST_CODE_SDCARD_READ);
    }

    @Override
    public void onRequestPermissionsResult (
            int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults)
    {

        switch (requestCode)
        {
            case REQUEST_CODE_SDCARD_READ:
                handlePermissionResult();
                break;

            default:
                break;
        }
    }

    private void handlePermissionResult ()
    {

        if (isGrantSDCardReadPermission())
        {
            runRobust();
        }
        else
        {
            Toast.makeText(this, "failure because without sd card read permission", Toast
                    .LENGTH_SHORT).show();
        }

    }

    private void runRobust ()
    {

        new PatchExecutor(getApplicationContext(), new PatchManipulateImp(),
                new RobustCallBackImp()).start();
    }
}
//./gradlew clean  assembleRelease --stacktrace --no-daemon