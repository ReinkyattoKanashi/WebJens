package com.reinkyatto.webjens.ui.serverslist

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.reinkyatto.webjens.R
import com.reinkyatto.webjens.databinding.FragmentAuthBinding
import com.reinkyatto.webjens.databinding.FragmentServersListBinding
import com.reinkyatto.webjens.remote.ApiRequests
import com.reinkyatto.webjens.ui.auth.AuthFragment
import com.reinkyatto.webjens.ui.auth.AuthViewModel
import com.reinkyatto.webjens.ui.auth.AuthViewModelFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.system.exitProcess

class ServersListFragment : Fragment() {

    private lateinit var binding: FragmentServersListBinding
    private val api: ApiRequests by lazy {
        getService()
    }

    private val viewModel: ServersListViewModel by viewModels ()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServersListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        registerObservers(view)
    }

    private fun registerObservers(view: View) {

    }


    private fun getService(): ApiRequests {
        val client = OkHttpClient.Builder()
            //.addInterceptor(TokenInterceptor(api_key)) // пригодится тогда, когда уже будем иметь токен, но явно не сейчас
            .build()

        return Retrofit.Builder()
            .baseUrl("BASE_URL")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiRequests::class.java)
    }

    private fun navigate(direction: NavDirections) {
        findNavController().navigate(direction)
    }


    // menu set
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //if (item.itemId == R.id.action_log_out) {
        //    viewModel.onLogOutClick()
        //}
        return true
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