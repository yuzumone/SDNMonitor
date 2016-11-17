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
import android.view.*
import android.widget.Toast
import net.yuzumone.sdnmonitor.R
import net.yuzumone.sdnmonitor.api.MonitorClient
import net.yuzumone.sdnmonitor.databinding.FragmentSwitchListBinding
import net.yuzumone.sdnmonitor.util.OnToggleElevationListener
import net.yuzumone.sdnmonitor.widget.SwitchArrayAdapter
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class SwitchListFragment : BaseFragment() {

    private lateinit var binding: FragmentSwitchListBinding
    private lateinit var adapter: SwitchArrayAdapter
    private lateinit var listener: OnToggleElevationListener
    @Inject
    lateinit var monitorClient: MonitorClient
    @Inject
    lateinit var compositeSubscription: CompositeSubscription

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSwitchListBinding.inflate(inflater, container, false)
        initView()
        setHasOptionsMenu(true)
        compositeSubscription.add(fetchSwitches())
        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
        if (context is OnToggleElevationListener) {
            listener = context
        }
    }

    fun initView() {
        listener.onToggleElevation(true)
        adapter = SwitchArrayAdapter(activity)
        binding.listSwitch.adapter = adapter
        binding.listSwitch.setOnItemClickListener { adapterView, view, i, l ->
            val switch = adapter.getItem(i)
            val fragment = ViewPagerFragment.newInstance(switch.id)
            fragmentManager.beginTransaction().replace(R.id.container, fragment)
                    .addToBackStack(null).commit()
        }
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        binding.swipeRefresh.setOnRefreshListener {
            compositeSubscription.clear()
            compositeSubscription.add(fetchSwitches())
        }
    }

    fun fetchSwitches(): Subscription {
        return monitorClient.getSwitches()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted { binding.swipeRefresh.isRefreshing = false }
                .subscribe (
                        { response ->
                            adapter.clear()
                            adapter.addAll(response)
                            adapter.notifyDataSetChanged()
                        },
                        { error ->
                            Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                        }
                )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity.menuInflater.inflate(R.menu.menu_switch, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_status -> {
                val fragment = StatusFragment()
                fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        compositeSubscription.unsubscribe()
        super.onDestroy()
    }
}