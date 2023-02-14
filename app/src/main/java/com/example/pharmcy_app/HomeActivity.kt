package com.example.pharmcy_app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.pharmcy_app.databinding.ActivityHomeBinding
import com.example.pharmcy_app.ui.FragmentCallback

import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity(), FragmentCallback {

    override fun navigationDetected(pageId: Int) {
        if (::binding.isInitialized) {
            val bottomNav = binding.appBarMain.bottomNavigation
            if (bottomNav != null && bottomNav.selectedItemId != pageId) {
                bottomNav.selectedItemId = pageId
            }
        }

    }

    private fun navigate(itemId: Int): Boolean {
        if (itemId == R.id.nav_logout) {
            firebaseAut.signOut()
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent);
            finish()
            return true
        }
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navController.navigate(itemId)
        binding.drawerLayout.closeDrawers()
        return false

    }

    private fun getBinding(): View {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseAut: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAut = FirebaseAuth.getInstance()
        setContentView(getBinding())
        setSupportActionBar(binding.appBarMain.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_account, R.id.nav_home, R.id.nav_medicament, R.id.nav_pharmacie, R.id.nav_reservation
            ), drawerLayout
        )
//        appBarConfiguration

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val bottomNav = binding.appBarMain.bottomNavigation
        bottomNav.selectedItemId = R.id.page_account

        bottomNav.setOnItemSelectedListener {
            if (it.itemId == R.id.page_account) {
                navigate(R.id.nav_account)
            }
            if (it.itemId == R.id.page_home) {
                navigate(R.id.nav_home)
            }
            if (it.itemId == R.id.page_pharmacie) {
                navigate(R.id.nav_pharmacie)
            }
            if (it.itemId == R.id.page_medicament) {
                navigate(R.id.nav_medicament)
            }
            true
        }

        navView.setNavigationItemSelectedListener {
            navigate(it.itemId)
            true
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        var searchItem = menu.findItem(R.id.app_bar_search)
//        searchItem?.expandActionView()
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            val searchHint = "Entrer ici ..."
            searchView.queryHint = searchHint


        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}