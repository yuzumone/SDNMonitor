/*
 * Copyright (C) 2016 yuzumone
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package net.yuzumone.sdnmonitor.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import net.yuzumone.sdnmonitor.MainApplication
import net.yuzumone.sdnmonitor.R
import net.yuzumone.sdnmonitor.databinding.ActivityMainBinding
import net.yuzumone.sdnmonitor.di.ActivityComponent
import net.yuzumone.sdnmonitor.di.ActivityModule
import net.yuzumone.sdnmonitor.fragment.LoginFragment
import net.yuzumone.sdnmonitor.util.OnToggleElevationListener

class MainActivity : AppCompatActivity(), OnToggleElevationListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initToolbar()
        getComponent().inject(this)
        val loginFragment = LoginFragment()
        supportFragmentManager.beginTransaction().add(R.id.container, loginFragment).commit()
    }

    fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            } else {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount != 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onToggleElevation(bool: Boolean) {
        val elevation = if (bool) resources.getDimension(R.dimen.elevation) else 0F
        binding.toolbar.elevation = elevation
    }

    fun getComponent(): ActivityComponent {
        val mainApplication = application as MainApplication
        return mainApplication.appComponent.plus(ActivityModule(this))
    }
}
