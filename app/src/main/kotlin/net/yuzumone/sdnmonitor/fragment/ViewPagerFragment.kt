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

package net.yuzumone.sdnmonitor.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.yuzumone.sdnmonitor.databinding.FragmentViewpagerBinding
import net.yuzumone.sdnmonitor.util.OnToggleElevationListener
import java.util.*

class ViewPagerFragment : BaseFragment() {

    private lateinit var binding: FragmentViewpagerBinding
    private lateinit var id: String
    private lateinit var listener: OnToggleElevationListener

    companion object {
        private val ARG_ID = "id"
        fun newInstance(id: String): ViewPagerFragment {
            val fragment = ViewPagerFragment()
            val args = Bundle()
            args.putString(ARG_ID, id)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentViewpagerBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
        if (context is OnToggleElevationListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            id = arguments.getString(ARG_ID)
        }
    }

    fun initView() {
        listener.onToggleElevation(false)
        val adapter = ViewPagerAdapter(childFragmentManager)
        binding.pager.adapter = adapter
        binding.tab.setupWithViewPager(binding.pager)
        val priorityFragment = PriorityFragment()
        adapter.add("Priority", priorityFragment)
        val predictionFragment = PredictionFragment.newInstance(id)
        adapter.add("Prediction", predictionFragment)
    }

    class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val fragments = ArrayList<Fragment>()
        private val titles = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titles[position]
        }

        fun add(title: String, fragment: Fragment) {
            fragments.add(fragment)
            titles.add(title)
            notifyDataSetChanged()
        }
    }
}