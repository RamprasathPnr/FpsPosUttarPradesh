package com.omneagate.Util;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by root on 22/9/16.
 */
public class EditboxFilter implements InputFilter {

    int maxDigitsBeforeDecimalPoint;
    int maxDigitsAfterDecimalPoint;

    public EditboxFilter(int beforedecimal, int afterdecimal) {
        this.maxDigitsBeforeDecimalPoint = beforedecimal;
        this.maxDigitsAfterDecimalPoint = afterdecimal;

    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        StringBuilder builder = new StringBuilder(dest);
        builder.replace(dstart, dend, source
                .subSequence(start, end).toString());
        if (!builder.toString().matches(
                "(([1-9]{1})([0-9]{0," + (maxDigitsBeforeDecimalPoint - 1) + "})?)?(\\.[0-9]{0," + maxDigitsAfterDecimalPoint + "})?"
        )) {
            if (source.length() == 0)
                return dest.subSequence(dstart, dend);
            return "";
        }
        return null;
    }
}
