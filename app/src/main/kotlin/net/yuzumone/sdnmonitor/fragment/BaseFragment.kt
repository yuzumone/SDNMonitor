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

import android.support.v4.app.Fragment
import net.yuzumone.sdnmonitor.activity.MainActivity
import net.yuzumone.sdnmonitor.di.FragmentComponent
import net.yuzumone.sdnmonitor.di.FragmentModule

abstract class BaseFragment : Fragment() {

    fun getComponent(): FragmentComponent {
        val mainActivity = activity as MainActivity
        return mainActivity.getComponent().plus(FragmentModule(this))
    }
}