/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.inventory

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.view.get
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.data.Item
import com.example.inventory.databinding.ItemListItemBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * [ListAdapter] implementation for the recyclerview.
 */

class ItemListAdapter(
    private val onButtonClicked: (View, Item) -> Unit,
    private val onItemClicked: (Item) -> Unit
) :
    ListAdapter<Item, ItemListAdapter.ItemViewHolder>(DiffCallback) {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemListItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val current = getItem(position)
        

        /*holder.itemView.setOnClickListener {
            onItemClicked(current)
        }*/



        onButtonClicked(holder.itemView.findViewById(R.id.item_button), current)



        holder.bind(current)


    }

    class ItemViewHolder(private var binding: ItemListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isExpanded = false


        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: Item) {

            binding.itemName.text = item.itemName
            binding.itemButton.isChecked = item.isDone
            binding.detail.text = item.itemDetail
            binding.date.text = item.date
            if (item.isDone){
                binding.root.removeView(binding.root.rootView)
            }
            binding.linear.setOnClickListener {
                binding.expand.isVisible = !isExpanded
                isExpanded = !isExpanded
            }

            binding.but.setOnClickListener {
                val action = ItemListFragmentDirections.actionItemListFragmentToItemDetailFragment(item.id)
                binding.root.findNavController().navigate(action)
            }
            if(item.isDone){
                binding.itemButton.isClickable = false
            }
            val currentDate = LocalDateTime.now()
            val currentDateFormatted = currentDate.format(DateTimeFormatter.BASIC_ISO_DATE).subSequence(6,8).toString().toInt()
            if(!item.isDone){
                if(item.date.subSequence(0,2).toString().toInt() > currentDateFormatted + 10 ){
                    Log.d("date","${item.date.subSequence(0,2).toString().toInt()}")
                    binding.root.strokeColor = Color.parseColor("#30d50b")
                }else if (item.date.subSequence(0,2).toString().toInt() in (currentDateFormatted + 5)..(currentDateFormatted + 10)){
                    Log.d("date","${item.date.subSequence(0,2).toString().toInt()}")
                    binding.root.strokeColor = Color.parseColor("#d5b40b")
                }
                else if (item.date.subSequence(0,2).toString().toInt() < currentDateFormatted + 5){
                    Log.d("date","${item.date.subSequence(0,2).toString().toInt()}")
                    binding.root.strokeColor = Color.parseColor("#d50b0b")
                }
            }











        }


    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.itemName == newItem.itemName
            }
        }
    }


}
