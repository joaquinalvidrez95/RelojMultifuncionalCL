package com.citsadigital.relojmultifuncionalcl


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import kotlinx.android.synthetic.main.fragment_time.*


class TimeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNumberPicker(arrayOf(
                pickerMinutes,
                pickerSeconds
        ))

        pickerHours.apply { maxValue = 99;minValue = 0 }
    }

    private fun setupNumberPicker(pickers: Array<NumberPicker>) {
        for (p in pickers) {
            p.apply {
                maxValue = 59
                minValue = 0
            }
        }
    }
}
