package com.citsadigital.relojmultifuncionalcl


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat


class SettingsFragment : PreferenceFragmentCompat() {
    //    private var mainViewModel: MainViewModel? = null
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setupUi()
//        mainViewModel = ViewModelProviders
//                .of(activity!!)[MainViewModel::class.java]
//        mainViewModel?.isTimeToRefreshLayout()?.observe(this, Observer {
//            preferenceScreen.removeAll()
//            setupUi()
//        })
    }

    private fun setupUi() {
        addPreferencesFromResource(R.xml.pref_settings)
        findPreference(getString(R.string.pref_key_updatetime)).setOnPreferenceClickListener {
            //mainViewModel?.sendDateTime()
            true
        }
        findPreference(getString(R.string.pref_key_volume)).setOnPreferenceChangeListener { preference, newValue ->
            preference.setIcon(if (newValue as Int == 0) R.drawable.ic_volume_off_black_24dp else R.drawable.ic_volume_up_24dp)
            true
        }
    }

}
