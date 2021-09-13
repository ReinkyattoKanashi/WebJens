package com.reinkyatto.webjens.ui.server

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.reinkyatto.webjens.R
import com.reinkyatto.webjens.activity.MainActivity
import com.reinkyatto.webjens.arch.BaseFragment
import com.reinkyatto.webjens.databinding.FragmentServerBinding
import com.reinkyatto.webjens.db.local.tables.serverlist.Server
import com.reinkyatto.webjens.utils.Const
import com.reinkyatto.webjens.utils.DataLoad
import com.reinkyatto.webjens.utils.safeDelay
import org.koin.android.ext.android.inject
import kotlin.random.Random
import kotlin.random.nextInt

class ServerFragment : BaseFragment() {
    private lateinit var binding: FragmentServerBinding

    private val args: ServerFragmentArgs by navArgs()
    private val serverId: String by lazy { args.serverId }

    private val viewModel: ServerViewModel by inject()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        registerObservers(view)
        hideUIButtons(true)
        viewModel.getDataFromDB(serverId)
        (activity as MainActivity).setVisibleBackBtn()
        (activity as MainActivity).saveDirectionForBackBtn(ServerFragmentDirections.actionServerFragmentPop())
        (activity as MainActivity).setVisibleProgressIndicator()
        (activity as MainActivity).setTitleText(R.string.server_settings_title)
        reloadData(view, Random.nextInt(1..4))
    }

    private fun reloadData(view: View, delay: Int) {
        view.safeDelay(delay * 1000L) {
            viewModel.getDataFromAPI(serverId)
        }
    }

    private fun registerObservers(view: View) {
        viewModel.navigationEvent.observe(viewLifecycleOwner, ::navigate)
        viewModel.apply {
            navigationEvent.observe(viewLifecycleOwner, ::navigate)
            dataSuccess.observe(viewLifecycleOwner, {
                hideUIButtons(false)
                updateUIInfo(it)
                showSnackBar(view, "Данные обновлены")
            })
            dataFailed.observe(viewLifecycleOwner, {
                when (it) {
                    DataLoad.REFRESH -> {
                        showSnackBar(view, "Ддос защита зажала данные, тварь")
                        reloadData(view, 1)
                    }
                    DataLoad.LONG_REFRESH -> {
                        reloadData(view, 3)
                    }
                    DataLoad.NULL -> {
                    }
                    else -> showSnackBar(view, "it")
                }
            })
            noInternetConnection.observe(viewLifecycleOwner, {
                showSnackBar(view, it)
            })
            loadIndicatorVisibility.observe(viewLifecycleOwner, {
                when (it) {
                    true -> (activity as MainActivity).setVisibleProgressIndicator()
                    false -> {
                        (activity as MainActivity).setInvisibleProgressIndicator()
                    }
                }
                //(activity as MainActivity).set
            })
            actionSuccess.observe(viewLifecycleOwner, {
                showSnackBar(view, it)
            })
            actionFailed.observe(viewLifecycleOwner, {
                showSnackBar(view, it)
            })
            actualDataLoaded.observe(viewLifecycleOwner, {
                // todo unlockUIButtons
            })
            blockUIButtons.observe(viewLifecycleOwner, {
                blockUIButtons(it)
            })
            updateData.observe(viewLifecycleOwner, {
                if (it) {
                    getDataFromAPI(serverId)
                }
            })
        }
    }

    private fun hideUIButtons(hide: Boolean) {
        if (hide) {
            binding.apply {
                btPowerServer.visibility = View.GONE
                btReloadServer.visibility = View.GONE
                btReinstallServer.visibility = View.GONE
                btInfoServer.visibility = View.GONE
                btBackupServer.visibility = View.GONE
                btConsoleServer.visibility = View.GONE
            }
        } else {
            binding.apply {
                btPowerServer.visibility = View.VISIBLE
                btReloadServer.visibility = View.VISIBLE
                btReinstallServer.visibility = View.VISIBLE
                btInfoServer.visibility = View.VISIBLE
                btBackupServer.visibility = View.VISIBLE
                btConsoleServer.visibility = View.VISIBLE
            }
        }
    }

    private fun blockUIButtons(hide: Boolean) {
        if (hide) {
            binding.apply {
                btPowerServer.isClickable = false
                btReloadServer.isClickable = false
                btReinstallServer.isClickable = false
                btInfoServer.isClickable = false
                btBackupServer.isClickable = false
                btConsoleServer.isClickable = false
                btPowerServer.setTextColor(resources.getColor(R.color.server_status_offline_color))
                btReloadServer.setTextColor(resources.getColor(R.color.server_status_offline_color))
                btReinstallServer.setTextColor(resources.getColor(R.color.server_status_offline_color))
                btInfoServer.setTextColor(resources.getColor(R.color.server_status_offline_color))
                btBackupServer.setTextColor(resources.getColor(R.color.server_status_offline_color))
                btConsoleServer.setTextColor(resources.getColor(R.color.server_status_offline_color))
            }
        } else {
            binding.apply {
                btPowerServer.isClickable = true
                btReloadServer.isClickable = true
                btReinstallServer.isClickable = true
                btInfoServer.isClickable = true
                btBackupServer.isClickable = true
                btConsoleServer.isClickable = true
                btPowerServer.setTextColor(resources.getColor(R.color.white))
                btReloadServer.setTextColor(resources.getColor(R.color.white))
                btReinstallServer.setTextColor(resources.getColor(R.color.white))
                btInfoServer.setTextColor(resources.getColor(R.color.white))
                btBackupServer.setTextColor(resources.getColor(R.color.white))
                btConsoleServer.setTextColor(resources.getColor(R.color.white))
            }
        }
    }

    private fun updateUIInfo(it: Server) {
        binding.apply {
            tvNameServer.text = it.name
            cardServerName.text = it.name
            cardServerIP.text = "IP: ${it.ip}:${it.port}"
            cardServerID.text = it.id
            when (it.game) {
                Const.MINECRAFT -> {
                    cardServerImage.setImageResource(R.drawable.mine)
                    cardServerGradient.setBackgroundResource(R.drawable.gradient_foreground_green)
                }
                Const.SAMP -> {
                    cardServerImage.setImageResource(R.drawable.samp)
                    cardServerGradient.setBackgroundResource(R.drawable.gradient_foreground_orange)
                }
            }
            cardServerStatusDot.text = "•"
            when (it.status) {
                2 -> {
                    btPowerServer.text = "Выключить"
                    cardServerStatus.text = "online"
                    cardServerStatus.setTextColor(resources.getColor(R.color.server_status_online_color))
                    cardServerStatusDot.setTextColor(resources.getColor(R.color.server_status_online_color))
                }
                1 -> {
                    btPowerServer.text = "Включить"
                    cardServerStatus.text = "offline"
                    cardServerStatus.setTextColor(resources.getColor(R.color.server_status_offline_color))
                    cardServerStatusDot.setTextColor(resources.getColor(R.color.server_status_offline_color))
                }
                0 -> {
                    btPowerServer.visibility = View.GONE
                    cardServerStatus.text = "blocked"
                    cardServerStatus.setTextColor(resources.getColor(R.color.server_status_blocked_color))
                    cardServerStatusDot.setTextColor(resources.getColor(R.color.server_status_blocked_color))
                }
                -21 -> {
                    cardServerStatusDot.text = ""
                    cardServerStatus.setTextColor(resources.getColor(R.color.server_status_no_actual_color))
                    cardServerStatusDot.setTextColor(resources.getColor(R.color.server_status_no_actual_color))
                }
            }
        }
    }

    private fun navigate(direction: NavDirections) {
        findNavController().navigate(direction)
    }
}
