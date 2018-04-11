package com.citsadigital.relojmultifuncionalcl.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by Joaquín Alan Alvidrez Soto on 03/04/2018.
 */
class PageAdapter(private val fragments: List<Fragment>, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment = fragments[position]
    override fun getCount(): Int = fragments.size
}