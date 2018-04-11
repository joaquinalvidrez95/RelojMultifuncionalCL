package com.citsadigital.relojmultifuncionalcl.custompreference;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Joaquín Alan Alvidrez Soto on 14/03/2018.
 */

public class ListPreferenceAutoSummary extends android.support.v7.preference.ListPreference {
    public ListPreferenceAutoSummary(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListPreferenceAutoSummary(Context context) {
        super(context);
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        setSummary(value);
    }

    @Override
    public void setSummary(CharSequence summary) {
        super.setSummary(getEntry());
    }


}
