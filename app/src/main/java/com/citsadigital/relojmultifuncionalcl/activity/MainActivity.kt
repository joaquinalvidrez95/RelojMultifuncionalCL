package com.citsadigital.relojmultifuncionalcl.activity

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.citsadigital.relojmultifuncionalcl.R
import com.citsadigital.relojmultifuncionalcl.adapter.PageAdapter
import com.citsadigital.relojmultifuncionalcl.fragment.HomeFragment
import com.citsadigital.relojmultifuncionalcl.fragment.SettingsFragment
import com.citsadigital.relojmultifuncionalcl.fragment.TimeFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val requestDeviceCode = 13
    private val requestEnableBt = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(mainToolbar as Toolbar?)
        setupFragments(mainViewPager, mainTabLayout)
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
}
