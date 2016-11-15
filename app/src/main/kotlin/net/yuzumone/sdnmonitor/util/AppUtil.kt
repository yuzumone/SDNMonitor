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

package net.yuzumone.sdnmonitor.util

import android.content.Context
import android.preference.PreferenceManager

class AppUtil {

    companion object {
        private val URI = "uri"
        private val ADDRESS = "address"
        private val PORT = "port"
        fun storeUri(context: Context, address: String, port: String) {
            val uri = "http://$address:$port"
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = preferences.edit()
            editor.putString(URI, uri)
            editor.putString(ADDRESS, address)
            editor.putString(PORT, port)
            editor.apply()
        }
        fun getUri(context: Context): String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val uri = preferences.getString(URI, "")
            return uri
        }
        fun getAddress(context: Context):String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val address = preferences.getString(ADDRESS, "")
            return address
        }
        fun getPort(context: Context): String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val port = preferences.getString(PORT, "")
            return port
        }
    }
}