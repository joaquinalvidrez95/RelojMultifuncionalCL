package com.citsadigital.relojmultifuncionalcl

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(mainToolbar as Toolbar?)
        setupFragments(mainViewPager, mainTabLayout)
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
                text = "Temporizador/cronómetro"
                setIcon(R.drawable.ic_timer_white_24dp)
            }
            getTabAt(2)?.apply {
                text = context.getString(R.string.title_tab_settings)
                setIcon(R.drawable.ic_settings_white_24dp)
            }
        }
    }
}
