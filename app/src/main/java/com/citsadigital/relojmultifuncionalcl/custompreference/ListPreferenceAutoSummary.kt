package com.citsadigital.relojmultifuncionalcl.custompreference

import android.content.Context
import android.util.AttributeSet

/**
 * Created by Joaquín Alan Alvidrez Soto on 14/03/2018.
 */

class ListPreferenceAutoSummary : android.support.v7.preference.ListPreference {
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context) : super(context)

    override fun setValue(value: String) {
        super.setValue(value)
        summary = value
    }

    override fun setSummary(summary: CharSequence) {
        super.setSummary(entry)
    }


}
