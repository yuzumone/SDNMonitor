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

package net.yuzumone.sdnmonitor.api

import android.content.Context
import net.yuzumone.sdnmonitor.model.*
import net.yuzumone.sdnmonitor.util.AppUtil
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MonitorClient @Inject constructor(context: Context, client: OkHttpClient) {

    private val service: MonitorService

    init {
        val uri = AppUtil.getUri(context) + "/v1/"
        val retrofit = Retrofit.Builder().client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(uri)
                .build()
        service = retrofit.create(MonitorService::class.java)
    }

    fun getPriorities(): Observable<List<Priority>> {
        return service.getPriorities()
    }

    fun getPriorities(count: Int): Observable<List<Priority>> {
        return service.getPriorities(count)
    }

    fun getPredictions(id: String): Observable<List<Bandwidth>> {
        return service.getPredictions(id)
    }

    fun getPredictions(id: String, count: Int): Observable<List<Bandwidth>> {
        return service.getPredictions(id, count)
    }

    fun getGuarantee(id: String): Observable<Bandwidth> {
        return service.getGuarantee(id)
    }

    fun getSwitches(): Observable<List<Switch>> {
        return service.getSwitches()
    }

    fun getSwitches(count: Int): Observable<List<Switch>> {
        return service.getSwitches(count)
    }

    fun showSwitch(id: String): Observable<Switch> {
        return service.showSwitch(id)
    }

    fun getPorts(id: String): Observable<List<Port>> {
        return service.getPorts(id)
    }

    fun getPorts(id: String, count: Int): Observable<List<Port>> {
        return service.getPorts(id, count)
    }

    fun showPort(mac: String): Observable<Port> {
        return service.showPort(mac)
    }

    fun getStatuses(): Observable<List<Status>> {
        return service.getStatuses()
    }

    fun getStatuses(count: Int): Observable<List<Status>> {
        return service.getStatuses(count)
    }

    interface MonitorService {
        @GET("priorities/priority.json")
        fun getPriorities(): Observable<List<Priority>>

        @GET("priorities/priority.json")
        fun getPriorities(@Query("count") count: Int): Observable<List<Priority>>

        @GET("predictions/prediction.json")
        fun getPredictions(@Query("datapath_id") id: String): Observable<List<Bandwidth>>

        @GET("predictions/prediction.json")
        fun getPredictions(@Query("datapath_id") id: String,
                           @Query("count") count: Int): Observable<List<Bandwidth>>

        @GET("guarantee/bandwidth.json")
        fun getGuarantee(@Query("datapath_id") id:String): Observable<Bandwidth>

        @GET("switches/switch.json")
        fun getSwitches(): Observable<List<Switch>>

        @GET("switches/switch.json")
        fun getSwitches(@Query("count") count: Int): Observable<List<Switch>>

        @GET("switches/show.json")
        fun showSwitch(@Query("datapath_id") id: String): Observable<Switch>

        @GET("ports/usage.json")
        fun getPorts(@Query("datapath_id") id: String): Observable<List<Port>>

        @GET("ports/usage.json")
        fun getPorts(@Query("datapath_id") id: String,
                     @Query("count") count: Int): Observable<List<Port>>

        @GET("ports/show.json")
        fun showPort(@Query("hw_addr") mac: String): Observable<Port>

        @GET("system/ping.json")
        fun ping(): Observable<Ping>

        @GET("system/status.json")
        fun getStatuses(): Observable<List<Status>>

        @GET("system/status.json")
        fun getStatuses(@Query("count") count: Int): Observable<List<Status>>
    }
}