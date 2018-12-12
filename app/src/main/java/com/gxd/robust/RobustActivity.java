package com.gxd.robust;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.meituan.robust.patch.annotaion.Add;
import com.meituan.robust.patch.annotaion.Modify;

public class RobustActivity extends AppCompatActivity implements View.OnClickListener
{
    TextView tvTitle = null;

    Button btnModify = null;

    @Modify
    @Override
    protected void onCreate (Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robust);
        tvTitle = (TextView) this.findViewById(R.id.tvTitle);
        btnModify = (Button) this.findViewById(R.id.btnModify);
        btnModify.setOnClickListener(this);
        //tvTitle.setText(getString());
        tvTitle.setText(getModify());
    }

    private String getString ()
    {

        return "Hello Robust!";
    }

    @Add
    private String getModify ()
    {

        return "Robust Modify!";
    }

    @Override
    public void onClick (View v)
    {

        switch (v.getId())
        {
            case R.id.btnModify:
                Toast.makeText(this, "button on click", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }
}
//./gradlew clean  assembleRelease --stacktrace --no-daemon
