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

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventory.data.Item
import com.example.inventory.databinding.FragmentAddItemBinding
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random
import com.github.dhaval2404.imagepicker.ImagePicker


/**
 * Fragment to add or update an item in the Inventory database.
 */
class AddItemFragment : Fragment() {

    private val pickImage = 100
    private var imageUri: Uri? = null
    private lateinit var dir: String
    private var isCal = false
    private var date = ""

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    // to share the ViewModel across fragments.
    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database
                .itemDao()
        )
    }
    private val navigationArgs: ItemDetailFragmentArgs by navArgs()

    lateinit var item: Item

    // Binding object instance corresponding to the fragment_add_item.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment
    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.itemName.text.toString(),
            binding.itemDetail.text.toString(),
            binding.imageView.drawable.toString(),
            this.date.toString()

        )
    }

    /**
     * Binds views with the passed in [item] information.
     */
    private fun bind(item: Item) {
        binding.apply {
            itemName.setText(item.itemName, TextView.BufferType.SPANNABLE)
            itemDetail.setText(item.itemDetail, TextView.BufferType.SPANNABLE)
            imageView.setImageURI(item.itemImage.toUri())
            dir = item.itemImage
            calendarView.isVisible = isCal
            gallery.setOnClickListener {
                ImagePicker.with(this@AddItemFragment).crop()
                    .saveDir(context?.getExternalFilesDir(null)!!).start()
            }
            viewCalender.setOnClickListener {
                calendarView.isVisible = !isCal
                isCal = !isCal
            }
            calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
                date = "$dayOfMonth/$month/$year"
            }

            saveAction.setOnClickListener {
                updateItem()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            dir = imageUri!!.path!!
            Log.d("yes", dir.toString())
            // imageUri?.let { saveToInternalStorage(it) }
            binding.imageView.setImageURI(imageUri)
        }
    }

    /*private fun saveToInternalStorage(uri: Uri) {
        val ran = Random.nextInt()
        val inputStream = context?.contentResolver?.openInputStream(uri)
        val output = FileOutputStream(File("${context?.filesDir?.absoluteFile}/$ran.png"))

        inputStream?.copyTo(output, 4 * 1024)
        dir = "${context?.filesDir?.absoluteFile}/$ran.png"
        Log.d("hello", uri.toString())

    }*/

    /**
     * Inserts the new Item into database and navigates up to list fragment.
     */
    private fun addNewItem() {
        if (isEntryValid()) {
            viewModel.addNewItem(
                binding.itemName.text.toString(),
                binding.itemDetail.text.toString(),
                dir.toString(),
                date
            )
            val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment("")
            findNavController().navigate(action)
        }
    }

    /**
     * Updates an existing Item in the database and navigates up to list fragment.
     */
    private fun updateItem() {
        if (isEntryValid()) {
            viewModel.updateItem(
                this.navigationArgs.itemId,
                this.binding.itemName.text.toString(),
                this.binding.itemDetail.text.toString(),
                dir.toString(),
                false,
                this.date
            )
            val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment("")
            findNavController().navigate(action)
        }
    }

    /**
     * Called when the view is created.
     * The itemId Navigation argument determines the edit item  or add new item.
     * If the itemId is positive, this method retrieves the information from the database and
     * allows the user to update it.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.itemId
        if (id > 0) {
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
                item = selectedItem
                bind(item)
            }
        } else {
            binding.saveAction.setOnClickListener {
                addNewItem()
            }
            binding.gallery.setOnClickListener {

                //val gallery =
                //Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                //val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                //startActivityForResult(gallery, 0)
                ImagePicker.with(this).saveDir(context?.getExternalFilesDir(null)!!)
                    .crop()                    //Crop image(Optional), Check Customization for more option
                    //.compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        1080,
                        1080
                    )    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start()

            }
            binding.calendarView.isVisible = isCal
            binding.viewCalender.setOnClickListener {
                binding.calendarView.isVisible = !isCal
                isCal = !isCal
            }
            binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
                if(dayOfMonth.toString().length == 2){
                    date = "$dayOfMonth/$month/$year"
                }else if (dayOfMonth.toString().length == 1){
                    date = "0$dayOfMonth/$month/$year"
                }

            }


        }
    }

    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}
