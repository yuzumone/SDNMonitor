package net.yuzumone.sdnmonitor.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import net.yuzumone.sdnmonitor.R
import net.yuzumone.sdnmonitor.api.MonitorClient
import net.yuzumone.sdnmonitor.databinding.FragmentPriorityBinding
import net.yuzumone.sdnmonitor.widget.PriorityArrayAdapter
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class PriorityFragment : BaseFragment() {

    private lateinit var binding: FragmentPriorityBinding
    private lateinit var adapter: PriorityArrayAdapter
    @Inject
    lateinit var monitorClient: MonitorClient
    @Inject
    lateinit var compositeSubscription: CompositeSubscription

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPriorityBinding.inflate(inflater, container, false)
        initView()
        compositeSubscription.add(fetchPriorities())
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    fun initView() {
        adapter = PriorityArrayAdapter(activity)
        binding.listPriority.adapter = adapter
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        binding.swipeRefresh.setOnRefreshListener {
            compositeSubscription.clear()
            compositeSubscription.add(fetchPriorities())
        }
    }

    fun fetchPriorities(): Subscription {
        return monitorClient.getPriorities()
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

    override fun onDestroy() {
        compositeSubscription.unsubscribe()
        super.onDestroy()
    }
}