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

package net.yuzumone.sdnmonitor.widget

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import net.yuzumone.sdnmonitor.R
import net.yuzumone.sdnmonitor.databinding.ItemPriorityBinding
import net.yuzumone.sdnmonitor.model.Priority

class PriorityArrayAdapter(context: Context) : ArrayAdapter<Priority>(context, 0) {

    private val inflater: LayoutInflater
    init {
        inflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val binding: ItemPriorityBinding

        if (view == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.item_priority, parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = view.tag as ItemPriorityBinding
        }
        val priority = getItem(position)
        binding.textName.text = priority.name
        binding.textMac.text = priority.mac
        return view!!
    }
}