package com.omneagate.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.omneagate.Util.CircleProgressBar.CircleProgressBar;
import com.omneagate.activity.R;

/*Custom progress dialog for user*/
public class CustomProgressDialog extends ProgressDialog {

    //Circle Progress bar
    CircleProgressBar progress;

    //Constructor
    public CustomProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_custom_progress_dialog);
        progress = (CircleProgressBar) findViewById(R.id.progressBar1);
        progress.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);


    }

    @Override
    public void show() {
        super.show();
        Log.i("Progress bar", "Progress Bar appearance");
    }

    @Override
    public void dismiss() {
        super.dismiss();
        Log.i("Progress bar", "Progress Bar Dismiss");
    }
}
