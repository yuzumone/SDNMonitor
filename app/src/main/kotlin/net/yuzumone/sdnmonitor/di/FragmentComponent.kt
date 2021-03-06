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

package net.yuzumone.sdnmonitor.di

import dagger.Subcomponent
import net.yuzumone.sdnmonitor.fragment.*

@FragmentScope
@Subcomponent(modules = arrayOf(FragmentModule::class))
interface FragmentComponent {
    fun inject(fragment: LoginFragment)

    fun inject(fragment: SwitchListFragment)

    fun inject(fragment: ViewPagerFragment)

    fun inject(fragment: PriorityFragment)

    fun inject(fragment: PredictionFragment)

    fun inject(fragment: UsageFragment)

    fun inject(fragment: StatusFragment)
}