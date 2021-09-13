package com.reinkyatto.webjens.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.reinkyatto.webjens.activity.MainActivity
import com.reinkyatto.webjens.arch.BaseFragment
import com.reinkyatto.webjens.databinding.FragmentAuthBinding
import android.view.MotionEvent

import android.view.View.OnTouchListener
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import com.reinkyatto.webjens.utils.safeDelay
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.system.exitProcess


class AuthFragment : BaseFragment() {
    private lateinit var binding: FragmentAuthBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel



        registerObservers(view)
    }




//    this.keyword.setOnTouchListener(new RightDrawableOnTouchListener(keyword) {
//        @Override
//        public boolean onDrawableTouch(final MotionEvent event) {
//            return onClickSearch(keyword,event);
//        }
//    });
//
//    private boolean onClickSearch(final View view, MotionEvent event) {
//        // do something
//        event.setAction(MotionEvent.ACTION_CANCEL);
//        return false;
//    }

    private fun navigate(direction: NavDirections) {
        findNavController().navigate(direction)
    }

    private fun registerObservers(view: View) {
        viewModel.apply {
            navigationEvent.observe(viewLifecycleOwner, ::navigate)
            authSuccess.observe(viewLifecycleOwner, {
                if (it) {
                    //Toast.makeText(requireContext(), token, Toast.LENGTH_SHORT).show()
                    (activity as MainActivity).setAuthMode(false)
                    navigate(AuthFragmentDirections.actionAuthFragmentToServersListFragment())
                }
            })
            authFailed.observe(viewLifecycleOwner, {
                showSnackBar(view, it)
                // error message. Incorrect login or password
            })
            fieldIsEmpty.observe(viewLifecycleOwner, {
                showSnackBar(view, it)
                // error message. Put data in fields!
            })
            noInternetConnection.observe(viewLifecycleOwner, {
                showSnackBar(view, it)
            })
            closeKeyboard.observe(viewLifecycleOwner, {
                if (it) {
                    requireContext().hideKeyboard(view)
                }
            })
            blockLogBtnAndShowProgress.observe(viewLifecycleOwner, {
                when(it){
                    true -> {
                        binding.apply {
                            etEmail.isEnabled = false
                            etPassword.isEnabled = false
                            loginBtn.isClickable = false
                            lpi.visibility = View.VISIBLE
                        }
                    }
                    false -> {
                        binding.apply {
                            etEmail.isEnabled = true
                            etPassword.isEnabled = true
                            loginBtn.isClickable = true
                            lpi.visibility = View.GONE
                        }
                    }
                }
            })
            responseIsNotSuccess.observe(viewLifecycleOwner, {
                if(it) view.safeDelay(Random.nextLong(1,5)){viewModel.onClickLogin()}
            })
        }
    }


    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                showAreYouSureDialog()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    private fun showAreYouSureDialog() {
        requireActivity().let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setMessage("Вы точно хотите выйти?")
                setPositiveButton(
                    "Да"
                ) { _, _ ->
                    exitProcess(1)
                }
                setNegativeButton(
                    "Нет"
                ) { _, _ ->
                    // User cancelled the dialog
                }
            }
            builder.create()
        }.show()
    }
}