package com.mobile.alarm.applications.mobilealarms.lan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.alarm.applications.mobilealarms.R


class AppListAdapter(
    private val onClick: (AppInfo) -> Unit,
    private val onLongClick: (AppInfo) -> Unit
) : RecyclerView.Adapter<AppListAdapter.AppViewHolder>() {

    private var apps: List<AppInfo> = emptyList()

    fun submitList(list: List<AppInfo>) {
        apps = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]
        holder.bind(app, onClick, onLongClick)
    }

    override fun getItemCount() = apps.size

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon = itemView.findViewById<AppCompatImageView>(R.id.ivIcon)
        private val tvName = itemView.findViewById<TextView>(R.id.tvName)

        fun bind(
            app: AppInfo,
            onClick: (AppInfo) -> Unit,
            onLongClick: (AppInfo) -> Unit
        ) {
            ivIcon.setImageDrawable(app.icon)
            tvName.text = app.name

            itemView.setOnClickListener { onClick(app) }
            itemView.setOnLongClickListener {
                onLongClick(app)
                true
            }
        }
    }
}