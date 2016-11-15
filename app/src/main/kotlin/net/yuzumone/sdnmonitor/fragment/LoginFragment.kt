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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import net.yuzumone.sdnmonitor.R
import net.yuzumone.sdnmonitor.api.MonitorClient
import net.yuzumone.sdnmonitor.databinding.FragmentLoginBinding
import net.yuzumone.sdnmonitor.util.AppUtil
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class LoginFragment : BaseFragment() {

    private lateinit var binding: FragmentLoginBinding
    @Inject
    lateinit var client: OkHttpClient
    @Inject
    lateinit var compositeSubscription: CompositeSubscription

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    private fun initView() {
        binding.editAddress.setText(AppUtil.getAddress(activity))
        binding.editPort.setText(AppUtil.getPort(activity))
        binding.buttonLogin.setOnClickListener {
            val address = binding.editAddress.text.toString()
            val port = binding.editPort.text.toString()
            connection(address, port)
        }
    }

    private fun connection(address: String, port: String) {
        val retrofit = Retrofit.Builder().client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl("http://$address:$port/v1/")
                .build()
        val service = retrofit.create(MonitorClient.MonitorService::class.java)
        val subscription = service.ping()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { binding.progressBar.visibility = View.VISIBLE }
                .doOnCompleted { binding.progressBar.visibility = View.GONE }
                .subscribe(
                        { response ->
                            AppUtil.storeUri(activity, address, port)
                            val fragment = SwitchListFragment()
                            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
                        },
                        { error ->
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                        }
                )
        compositeSubscription.add(subscription)
    }

    override fun onDestroy() {
        compositeSubscription.unsubscribe()
        super.onDestroy()
    }
}