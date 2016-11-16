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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import net.yuzumone.sdnmonitor.R
import net.yuzumone.sdnmonitor.api.MonitorClient
import net.yuzumone.sdnmonitor.databinding.FragmentPredictionBinding
import net.yuzumone.sdnmonitor.model.Bandwidth
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import javax.inject.Inject

class PredictionFragment : BaseFragment() {

    private lateinit var binding: FragmentPredictionBinding
    private lateinit var id: String
    @Inject
    lateinit var monitorClient: MonitorClient
    @Inject
    lateinit var compositeSubscription: CompositeSubscription

    companion object {
        private val ARG_ID = "id"
        fun newInstance(id: String): PredictionFragment {
            val fragment = PredictionFragment()
            fragment.apply {
                arguments = Bundle().apply {
                    putString(ARG_ID, id)
                }
            }
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = arguments.getString(ARG_ID)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPredictionBinding.inflate(inflater, container, false)
        initView()
        compositeSubscription.add(fetchPredictions())
        return binding.root
    }

    fun initView() {
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        binding.swipeRefresh.setOnRefreshListener {
            compositeSubscription.clear()
            compositeSubscription.add(fetchPredictions())
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

    fun fetchPredictions(): Subscription {
        return monitorClient.getPredictions(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted { binding.swipeRefresh.isRefreshing = false }
                .subscribe (
                        { response ->
                            bindData(response.reversed())
                        },
                        { error ->
                            Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                        }
                )
    }

    fun bindData(response: List<Bandwidth>) {
        val entries = ArrayList<Entry>()
        for ((index, value) in response.withIndex()) {
            entries.add(Entry(index.toFloat(), value.bandwidth.toFloat()))
        }
        val dataSet = LineDataSet(entries, "bandwidth")
        val lineData = LineData(dataSet)
        binding.chart.data = lineData
        binding.chart.invalidate()
    }

    override fun onDestroy() {
        compositeSubscription.unsubscribe()
        super.onDestroy()
    }
}