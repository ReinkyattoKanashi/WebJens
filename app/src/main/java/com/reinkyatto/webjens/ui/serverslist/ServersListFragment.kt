package com.reinkyatto.webjens.ui.serverslist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reinkyatto.webjens.R
import com.reinkyatto.webjens.activity.MainActivity
import com.reinkyatto.webjens.arch.BaseFragment
import com.reinkyatto.webjens.databinding.FragmentServersListBinding
import com.reinkyatto.webjens.db.local.tables.serverlist.Server
import com.reinkyatto.webjens.ui.serverslist.adapter.ServersListAdapter
import com.reinkyatto.webjens.utils.safeDelay
import org.koin.android.ext.android.inject
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.system.exitProcess

class ServersListFragment : BaseFragment() {

    private val myAdapter: ServersListAdapter by lazy {
        ServersListAdapter(object : ServersListAdapter.ItemClickListener {
            override fun onClick(server: Server) {
                viewModel.setDefaultValuesForLivaData()
                navigate(
                    ServersListFragmentDirections.actionServersListFragmentToServerFragment(server.id)
                )
            }
        })
    }
    private lateinit var binding: FragmentServersListBinding

    private val viewModel: ServersListViewModel by inject()
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
        setupRecyclerView()
        viewModel.getDataFromDB()
        showNoActualView(true)
        (activity as MainActivity).apply {
            setInvisibleBackBtn()
            setVisibleProgressIndicator()
            setTitleText(R.string.servers_list_title)
        }
        reloadData(view, Random.nextInt(1..4))
    }

    private fun showNoActualView(state: Boolean) {
        when(state){
            true -> binding.NoActualIndicator.visibility = View.VISIBLE
            false -> binding.NoActualIndicator.visibility = View.GONE
        }

    }

    private fun reloadData(view: View, delay: Int) {
        view.safeDelay(delay * 1000L) {
            viewModel.getDataFromAPI()
        }
    }

    private fun registerObservers(view: View) {
        viewModel.apply {
            navigationEvent.observe(viewLifecycleOwner, ::navigate)
            dataSuccess.observe(viewLifecycleOwner, {
                myAdapter.update(it)
            })
            dataFailed.observe(viewLifecycleOwner, {
                when (it) {
                    "refresh" -> {
                        showSnackBar(view, "Ддос защита зажала данные, тварь")
                        reloadData(view, 1)
                    }
                    "wait&refresh" -> {
                        reloadData(view, 3)
                    }
                    "none" -> { }
                    else -> showSnackBar(view, it)
                }
            })
            noInternetConnection.observe(viewLifecycleOwner, {
                showSnackBar(view, it)
            })
            loadIndicatorVisibility.observe(viewLifecycleOwner, {
                when (it) {
                    true -> {
                        (activity as MainActivity).setVisibleProgressIndicator()
                        showNoActualView(true)
                    }
                    false -> {
                        showNoActualView(false)
                        (activity as MainActivity).setInvisibleProgressIndicator()
                        //showSnackBar(view, "Обновлено")
                    }
                }
                //(activity as MainActivity).set
            })
        }
    }

    private fun setupRecyclerView() {
        binding.rv.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
    }

    private fun navigate(direction: NavDirections) {
        findNavController().navigate(direction)
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