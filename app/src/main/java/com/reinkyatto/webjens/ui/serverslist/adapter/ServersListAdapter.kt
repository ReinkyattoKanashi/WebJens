package com.reinkyatto.webjens.ui.serverslist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.reinkyatto.webjens.R
import com.reinkyatto.webjens.db.local.tables.serverlist.Server
import com.reinkyatto.webjens.utils.Const.MINECRAFT
import com.reinkyatto.webjens.utils.Const.SAMP

class ServersListAdapter(private val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<ServersListAdapter.TestViewHolder>() {

    private var list = emptyList<Server>()

    fun update(list: List<Server>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class TestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val serverId: TextView = view.findViewById<View>(R.id.server_id) as TextView
        val serverName: TextView = view.findViewById<View>(R.id.server_name) as TextView
        val serverIP: TextView = view.findViewById<View>(R.id.server_ip) as TextView
        private val serverStatusIndicator: CardView =
            view.findViewById<View>(R.id.serverStatusIndication) as CardView
        val gameImage: ImageView = view.findViewById(R.id.cardGameImage) as ImageView
        val serverItemClickZone: View = view.findViewById(R.id.serverItemClickZone) as View

        fun setStatus(status: Int) {
            when (status) {
                2 -> {
                    setColorIndication(
                        R.color.server_status_online_color
                    )
                }
                1 -> {
                    setColorIndication(
                        R.color.server_status_offline_color
                    )
                }
                0 -> {
                    setColorIndication(
                        R.color.server_status_blocked_color
                    )
                }
                else -> { // server not actual -> gray. status "-21"
                    setColorIndication(
                        R.color.server_status_no_actual_color
                    )
                }
            }
        }

        private fun setColorIndication(serverStatusColor: Int) {
            serverStatusIndicator.apply {
                setCardBackgroundColor(resources.getColor(serverStatusColor))
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.server_item, parent, false)
        return TestViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.apply {
            serverName.text = list[position].name
            serverIP.text = "IP: ${list[position].ip}:${list[position].port}"
            serverId.text = "ID: ${list[position].id}"
            setStatus(list[position].status)
            when (list[position].game) {
                MINECRAFT -> {
                    if(list[position].status != -21) {
                        gameImage.setImageResource(R.drawable.mine)
                    }
                    else gameImage.setImageResource(R.drawable.mine_bw)
                }
                SAMP -> {
                    if(list[position].status != -21) {
                        gameImage.setImageResource(R.drawable.samp)
                    }
                    else gameImage.setImageResource(R.drawable.samp_bw)
                }
            }
            serverItemClickZone.setOnClickListener {//todo область клика
                itemClickListener.onClick(list[position])
            }
        }
    }

    override fun getItemCount() = list.size

    interface ItemClickListener {
        fun onClick(server: Server)
    }
}