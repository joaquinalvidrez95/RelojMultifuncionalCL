package com.citsadigital.relojmultifuncionalcl.fragment


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import com.citsadigital.relojmultifuncionalcl.R
import com.citsadigital.relojmultifuncionalcl.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_time.*


class TimeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_time, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNumberPicker(arrayOf(
                pickerMinutes,
                pickerSeconds
        ))

        pickerHours.apply { maxValue = 99;minValue = 0 }

        val mainViewModel = ViewModelProviders.of(activity!!)[MainViewModel::class.java]
        mainViewModel.getTimerStopwatch().observe(this, Observer {
            pickerHours.value = Integer.parseInt(it?.substring(0, 2))
            pickerMinutes.value = Integer.parseInt(it?.substring(2, 4))
            pickerSeconds.value = Integer.parseInt(it?.substring(4, 6))
        })
        buttonSendTime.setOnClickListener {
            mainViewModel.sendTimerStopwatch(pickerHours.value, pickerMinutes.value, pickerSeconds.value)
        }


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
