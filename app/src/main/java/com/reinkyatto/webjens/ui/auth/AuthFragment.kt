package com.reinkyatto.webjens.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.reinkyatto.webjens.databinding.FragmentAuthBinding
import com.reinkyatto.webjens.remote.ApiRequests
import com.reinkyatto.webjens.utils.dataStore
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.system.exitProcess

class AuthFragment : Fragment() {
    private lateinit var binding: FragmentAuthBinding
    private val api: ApiRequests by lazy {
        getService()
    }

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(api)
    }

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


    private fun getService(): ApiRequests {
        val client = OkHttpClient.Builder()
            //.addInterceptor(TokenInterceptor(api_key)) // пригодится тогда, когда уже будем иметь токен, но явно не сейчас
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiRequests::class.java)
    }

    private fun navigate(direction: NavDirections) {
        findNavController().navigate(direction)
    }
    private fun registerObservers(view: View) {
        viewModel.apply {
            navigationEvent.observe(viewLifecycleOwner, ::navigate)
            authSuccess.observe(viewLifecycleOwner, { token ->
                suspend {
                    val key = stringPreferencesKey("token")
                    requireContext().dataStore.edit { data ->
                        data[key] = token
                    }
                }
                navigate(AuthFragmentDirections.actionAuthFragmentToServersListFragment())
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
        }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showSnackBar(view: View, text: String?) {
        text?.let { Snackbar.make(view, it, Snackbar.LENGTH_LONG).show() }
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

    companion object {
        private const val BASE_URL = "https://webjens.ru/main/api/"
    }
}
