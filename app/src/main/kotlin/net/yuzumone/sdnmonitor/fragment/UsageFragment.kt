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
import android.graphics.Color
import android.os.Bundle
import android.support.v4.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import net.yuzumone.sdnmonitor.R
import net.yuzumone.sdnmonitor.api.MonitorClient
import net.yuzumone.sdnmonitor.databinding.FragmentUsageBinding
import net.yuzumone.sdnmonitor.model.Port
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import javax.inject.Inject

class UsageFragment : BaseFragment() {

    private lateinit var binding: FragmentUsageBinding
    private lateinit var id: String
    @Inject
    lateinit var monitorClient: MonitorClient
    @Inject
    lateinit var compositeSubscription: CompositeSubscription

    companion object {
        private val ARG_ID = "id"
        fun newInstance(id: String): UsageFragment {
            val fragment = UsageFragment()
            fragment.apply {
                arguments = Bundle().apply {
                    putString(ARG_ID, id)
                }
            }
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUsageBinding.inflate(inflater, container, false)
        initView()
        compositeSubscription.add(fetchPorts())
        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = arguments.getString(ARG_ID)
    }

    fun initView() {
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        binding.swipeRefresh.setOnRefreshListener {
            compositeSubscription.clear()
            compositeSubscription.add(fetchPorts())
        }

        val data = LineData()
        data.setValueTextColor(Color.BLACK)
        binding.chart.data = data

        binding.chart.let { chart ->
            chart.xAxis.apply {
                isEnabled = false
            }
            chart.axisLeft.apply {
                axisMinimum = 0f
            }
            chart.axisRight.apply {
                isEnabled = false
            }
        }
    }

    fun fetchPorts(): Subscription {
        return monitorClient.getPorts(id, 3)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted { binding.swipeRefresh.isRefreshing = false }
                .subscribe (
                        { response ->
                            bindView(response)
                        },
                        { error ->
                            Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                        }
                )
    }

    fun bindView(response: List<Port>) {
        binding.chart.data = createLineDataSet(response)
        binding.chart.invalidate()
    }

    fun createLineDataSet(response: List<Port>): LineData {
        val bandwidths = ArrayMap<String, List<Int>>()
        val map = initializeMap(response)
        for (p in response) {
            for (b in p.bandwidths) {
                map[b.getDate().time] = map[b.getDate().time]!! + b.bandwidth.toInt()
            }
            val list = ArrayList<Int>()
            for ((key, value) in map) {
                list.add(value)
            }
            bandwidths.put(p.name, list)
        }

        val dataSets = ArrayList<ILineDataSet>()
        for ((key, value) in bandwidths) {
            val entries = ArrayList<Entry>()
            for ((i, v) in value.withIndex()) {
                entries.add(Entry(i.toFloat(), v.toFloat()))
            }
            val lineDataSets = LineDataSet(entries, key)
            lineDataSets.color = ColorTemplate.COLORFUL_COLORS[bandwidths.indexOfKey(key)]
            dataSets.add(lineDataSets)
        }
        return LineData(dataSets)
    }

    fun initializeMap(response: List<Port>): SortedMap<Long, Int> {
        val map = ArrayMap<Long, Int>()
        for (p in response) {
            for (b in p.bandwidths) {
                if (map[b.getDate().time] == null) {
                    map.put(b.getDate().time, 0)
                }
            }
        }
        return map.toSortedMap()
    }

    override fun onDestroy() {
        compositeSubscription.unsubscribe()
        super.onDestroy()
    }
}