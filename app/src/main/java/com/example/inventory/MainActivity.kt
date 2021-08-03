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
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ButtonBarLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration

import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appBarConfiguration = AppBarConfiguration.Builder(R.id.itemListFragment).build()

        // Retrieve NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
           .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        // Set up the action bar for use with the NavController
        setupActionBarWithNavController(this, navController,appBarConfiguration)


        val bottom_nav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottom_nav.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.Todo -> {

                    val action = ItemListFragmentDirections.actionItemListFragmentSelf("todo")
                    navController.navigate(action)
                    true
                }
                R.id.Done -> {

                    val action = ItemListFragmentDirections.actionItemListFragmentSelf("done")
                    navController.navigate(action)
                    true
                }
                else -> false
            }
        }
        //bottom_nav.setupWithNavController(navController)




    }

    /**
     * Handle navigation when the user chooses Up from the action bar.
     */
    override fun onSupportNavigateUp(): Boolean {

            return navController.navigateUp() || super.onSupportNavigateUp()


    }

}
