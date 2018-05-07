package com.citsadigital.relojmultifuncionalcl.fragment


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.SeekBarPreference
import com.citsadigital.relojmultifuncionalcl.R
import com.citsadigital.relojmultifuncionalcl.viewmodel.MainViewModel


class SettingsFragment : PreferenceFragmentCompat() {
    private var mainViewModel: MainViewModel? = null
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setupUi()
        mainViewModel = ViewModelProviders
                .of(activity!!)[MainViewModel::class.java]
        mainViewModel?.isTimeToRefreshLayout()?.observe(this, Observer {
            preferenceScreen.removeAll()
            setupUi()
        })
    }

    private fun setupUi() {
        addPreferencesFromResource(R.xml.pref_settings)
        findPreference(getString(R.string.pref_key_updatetime)).setOnPreferenceClickListener {
            mainViewModel?.sendDateTime()
            true
        }
        val volumePreference = findPreference(getString(R.string.pref_key_volume)) as SeekBarPreference
        updateIcon(volumePreference, volumePreference.value)
        volumePreference.setOnPreferenceChangeListener { preference, newValue ->
            updateIcon(preference, newValue)
            true
        }
    }

    private fun updateIcon(preference: Preference, newValue: Any?) {
        preference.setIcon(if (newValue as Int == 0) R.drawable.ic_volume_off_black_24dp else R.drawable.ic_volume_up_24dp)
    }

}
