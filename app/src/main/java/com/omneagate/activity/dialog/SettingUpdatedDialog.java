package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;
import com.omneagate.activity.TgLoginActivity;

/**
 * Created by root on 7/2/17.
 */
public class SettingUpdatedDialog extends Dialog implements View.OnClickListener {


    private final Activity context;
    private String strDialogText;
    private TextView dialogText;
    private Button confirmButton;


    public SettingUpdatedDialog(Activity context, String dialogText) {
        super(context);
        this.context = context;
        this.strDialogText = dialogText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_setting_updated);

        ConfigureInitView();

    }

    private void ConfigureInitView(){
        dialogText=(TextView)findViewById(R.id.dialogText);
        confirmButton=(Button)findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(this);
        dialogText.setText(""+strDialogText);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmButton:
                dismiss();
                Intent loginIntent =new Intent(context, TgLoginActivity.class);
                context.startActivity(loginIntent);
                context.finish();
                break;
        }
    }
}
