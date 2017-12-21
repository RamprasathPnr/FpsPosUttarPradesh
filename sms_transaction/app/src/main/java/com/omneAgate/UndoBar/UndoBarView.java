package com.omneagate.UndoBar;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.omneagate.activity.R;

public class UndoBarView extends MaxWidthRelativeLayout {

    Context context;

    public UndoBarView(Context context) {
        super(context);
        this.context = context;
    }

    public UndoBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public UndoBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public UndoBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }


    private TextView mMessage;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mMessage = (TextView) findViewById(R.id.message);
      /*  Typeface tfBamini = Typeface.createFromAsset(context.getAssets(),"fonts/Bamini.ttf");
        mMessage.setTypeface(tfBamini);*/
    }

    void setMessage(CharSequence message) {
        /*if (GlobalAppState.language.equals("ta")) {
            Typeface tfBamini = Typeface.createFromAsset(context.getAssets(), "fonts/Bamini.ttf");
            mMessage.setTypeface(tfBamini);
            mMessage.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, message.toString()));
        } else {*/
        mMessage.setText(message);
//        }

    }


    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.message = mMessage.getText().toString();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setMessage(ss.message);
    }

    @SuppressWarnings("NullableProblems")
    private static class SavedState extends BaseSavedState {

        String message;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(message);
        }

        private SavedState(Parcel in) {
            super(in);
            message = in.readString();
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
