package com.reinkyatto.webjens.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.reinkyatto.webjens.activity.MainActivity
import com.reinkyatto.webjens.arch.BaseFragment
import com.reinkyatto.webjens.databinding.FragmentSplashBinding
import com.reinkyatto.webjens.utils.safeDelay
import kotlin.random.Random
import kotlin.random.nextInt

class SplashFragment : BaseFragment() {
    private lateinit var binding: FragmentSplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        viewModel.navigationEvent.observe(viewLifecycleOwner, ::navigate)
        viewModel.authStatus.observe(viewLifecycleOwner, {
            when (it) {
                1 -> {
                    (activity as MainActivity).setAuthMode(false)

                    navigate(SplashFragmentDirections.actionSplashFragmentToServersListFragment())
                }
                0 -> {
                    Snackbar.make(view, "Истек срок действия активной сессии", Snackbar.LENGTH_LONG)
                        .show()
                    navigate(SplashFragmentDirections.actionSplashFragmentToAuthFragment())
                }
                else -> { // -1 means user log out, or never open app
                    navigate(SplashFragmentDirections.actionSplashFragmentToAuthFragment())
                }
            }
        })
        viewModel.noAnswer.observe(viewLifecycleOwner, {
            if(it){
                view.safeDelay(Random.nextInt(2..3)*1000L){
                    viewModel.checkToken() // try again
                }
            }
        })
        viewModel.checkToken()
    }

    private fun navigate(direction: NavDirections) {
        findNavController().navigate(direction)
    }
}