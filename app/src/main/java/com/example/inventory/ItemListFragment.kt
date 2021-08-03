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

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.databinding.ItemListFragmentBinding


/**
 * Main fragment displaying details for all items in the database.
 */
class ItemListFragment() : Fragment() {


    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database.itemDao()
        )
    }

    private var _binding: ItemListFragmentBinding? = null
    private val binding get() = _binding!!
    private val navigationArgs : ItemListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ItemListFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ItemListAdapter(
            { yes, no ->
                yes.setOnClickListener {
                    viewModel.updateItem(
                        no.id,
                        no.itemName,
                        no.itemDetail,
                        no.itemImage,
                        true,
                        no.date
                    )
                }
            }, {
                val action =
                    ItemListFragmentDirections.actionItemListFragmentToItemDetailFragment(it.id)
                this.findNavController().navigate(action)
            }
        )

        val swipe = object : swipeDelete(binding.recyclerView.context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.RIGHT -> viewModel.deleteItem(adapter.currentList[position])
                    ItemTouchHelper.LEFT -> {
                        val action =
                            ItemListFragmentDirections.actionItemListFragmentToAddItemFragment(
                                getString(R.string.edit_fragment_title),
                                adapter.currentList[position].id
                            )
                        this@ItemListFragment.findNavController().navigate(action)
                    }

                }

            }
        }

        val itemTouchHelper = ItemTouchHelper(swipe)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)





        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)

        binding.recyclerView.adapter = adapter


        val temp = navigationArgs.data;
        Log.d("yes",temp)
        binding.screenName.text = temp



        // Attach an observer on the allItems list to update the UI automatically when the data
        // changes.
        viewModel.allItems.observe(this.viewLifecycleOwner) { items ->

            items.let {
                adapter.submitList(items.filter { !it.isDone })
                binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if(temp == "todo"){
                            adapter.submitList(items.filter {
                                it.itemName.contains(
                                    newText!!,
                                    true
                                ) && !it.isDone
                            })
                        } else if (temp == "done"){
                            adapter.submitList(items.filter {
                                it.itemName.contains(
                                    newText!!,
                                    true
                                ) && it.isDone
                            })
                        }

                        return true
                    }

                })

                if(temp == "todo"){
                    adapter.submitList(items.filter { !it.isDone })
                } else if(temp == "done"){
                    adapter.submitList(items.filter { it.isDone })
                }




            }


        }


        binding.floatingActionButton.setOnClickListener {
            val action = ItemListFragmentDirections.actionItemListFragmentToAddItemFragment(
                getString(R.string.add_fragment_title)
            )
            this.findNavController().navigate(action)
        }






    }


}
