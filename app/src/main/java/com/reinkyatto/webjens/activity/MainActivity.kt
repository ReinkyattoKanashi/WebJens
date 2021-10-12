package com.reinkyatto.webjens.activity

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavDirections
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.reinkyatto.webjens.R
import com.reinkyatto.webjens.databinding.ActivityMainBinding
import org.koin.android.ext.android.bind

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WebJens)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btMenuImg.setOnClickListener {
            binding.dlMenu.openDrawer(GravityCompat.END)
        }

        binding.btBackImg.setOnClickListener {
            navigate(savedDirection)
        }

        setInvisibleProgressIndicator()
        setAuthMode(true)
    }

    fun setAuthMode(mode: Boolean){
        when(mode){
            true -> {
                setInvisibleActionBar()
                lockDrawerMenu()
            }
            false -> {
                setVisibleActionBar()
                unlockDrawerMenu()
            }
        }
    }

    private lateinit var savedDirection: NavDirections

    fun saveDirectionForBackBtn(direction: NavDirections){
        savedDirection = direction
    }

    private fun navigate(direction: NavDirections) {
        findNavController(this,R.id.nav_host_fragment).navigate(direction)
    }

    fun setVisibleProgressIndicator() {
        binding.lpi.visibility = View.VISIBLE
    }

    fun setInvisibleProgressIndicator() {
        binding.lpi.visibility = View.GONE
    }


    private fun setVisibleActionBar() {
        binding.clToolbar.visibility = View.VISIBLE
    }

    private fun setInvisibleActionBar() {
        binding.clToolbar.visibility = View.GONE
    }

    fun setVisibleBackBtn() {
        binding.btBackImg.visibility = View.VISIBLE
        binding.btBackImg.isClickable = true
    }

    fun setInvisibleBackBtn() {
        binding.btBackImg.isClickable = false
        binding.btBackImg.visibility = View.INVISIBLE
    }

    private fun lockDrawerMenu(){
        binding.dlMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED) // locked
    }


    private fun unlockDrawerMenu(){
        binding.dlMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED) // unlocked
    }

    fun setTitleText(resTitle: Int){
        binding.tvTitle.setText(resTitle)
    }

    fun clearTitleText(){
        binding.tvTitle.text = ""
    }


}