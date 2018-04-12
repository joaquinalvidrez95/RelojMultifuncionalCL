package com.citsadigital.relojmultifuncionalcl.activity

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.citsadigital.relojmultifuncionalcl.R
import com.citsadigital.relojmultifuncionalcl.adapter.PageAdapter
import com.citsadigital.relojmultifuncionalcl.fragment.HomeFragment
import com.citsadigital.relojmultifuncionalcl.fragment.LoginDialogFragment
import com.citsadigital.relojmultifuncionalcl.fragment.SettingsFragment
import com.citsadigital.relojmultifuncionalcl.fragment.TimeFragment
import com.citsadigital.relojmultifuncionalcl.util.BluetoothConstants
import com.citsadigital.relojmultifuncionalcl.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val requestDeviceCode = 13
    private val requestEnableBt = 10
    private var mainViewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(mainToolbar as Toolbar?)
        setupFragments(mainViewPager, mainTabLayout)

        mainViewModel = ViewModelProviders.of(this)[MainViewModel::class.java]


        mainViewModel?.isConnected()?.observe(
                this,
                Observer { t ->
                    supportActionBar?.title =
                            if (t == true)
                                getString(R.string.toolbar_title_connected)
                            else getString(R.string.toolbar_title_disconnected)
                })

        mainViewModel?.getConnectionState()
                ?.observe(
                        this,
                        Observer { bundle ->
                            val message = bundle?.getString(getString(R.string.key_mainviewmodel_message))
                                    ?: ""
                            when (bundle?.getInt(getString(R.string.key_mainviewmodel_devicestate))) {
                                BluetoothConstants.DeviceState.CONNECTED ->
                                    LoginDialogFragment.newInstance(
                                            PreferenceManager
                                                    .getDefaultSharedPreferences(this)
                                                    .getString(
                                                            getString(
                                                                    R.string.pref_key_password),
                                                            getString(R.string.pref_default_password)
                                                    )
                                    ).show(supportFragmentManager, "login")
                                BluetoothConstants.DeviceState.DISPLAY_CONNECTED,
                                BluetoothConstants.DeviceState.DISCONNECTED,
                                BluetoothConstants.DeviceState.CONNECTION_FAILED -> showMessage(message)

                            }

                        }
                )
        mainViewModel?.getMessage()?.observe(this, Observer { t -> showMessage(t ?: "") })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestDeviceCode && resultCode == Activity.RESULT_OK) {
            val device = data?.extras?.getParcelable<BluetoothDevice>(getString(R.string.key_mainviewmodel_device))
            if (device != null) {
                mainViewModel?.onDeviceSelected(device)
            }
        }

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {

            R.id.menuitem_main_about -> startActivity(Intent(this, AboutActivity::class.java))
            R.id.menuitem_main_bluetooth -> {
                if (BluetoothAdapter.getDefaultAdapter().isEnabled) {
                    startActivityForResult(Intent(this, BluetoothActivity::class.java), requestDeviceCode)
                } else {
                    startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), requestEnableBt)
                }
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupFragments(viewPager: ViewPager, tabLayout: TabLayout) {
        val fragments = ArrayList<Fragment>().apply {
            add(HomeFragment())
            add(TimeFragment())
            add(SettingsFragment())
        }
        viewPager.apply {
            adapter = PageAdapter(fragments, supportFragmentManager)
            offscreenPageLimit = fragments.size
        }
        tabLayout.apply {
            setupWithViewPager(viewPager)
            getTabAt(0)?.apply {
                text = context.getString(R.string.title_tab_home)
                setIcon(R.drawable.ic_home_white_24dp)
            }
            getTabAt(1)?.apply {
                text = context.getString(R.string.title_tab_timerstopwatch)
                setIcon(R.drawable.ic_timer_white_24dp)
            }
            getTabAt(2)?.apply {
                text = context.getString(R.string.title_tab_settings)
                setIcon(R.drawable.ic_settings_white_24dp)
            }
        }
    }

    private fun showMessage(text: String) {
        Snackbar.make(mainViewPager, text, Snackbar.LENGTH_LONG).show()
    }
}
