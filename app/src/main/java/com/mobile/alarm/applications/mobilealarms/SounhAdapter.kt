package com.mobile.alarm.applications.mobilealarms

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.mobile.alarm.applications.mobilealarms.DetailActivity
import com.mobile.alarm.applications.mobilealarms.utils.LaunTool

open class SounhAdapter(
    private val context: Context,
    private val datalist: List<Int>
) : RecyclerView.Adapter<SounhAdapter.WallpaperViewHolder>() {

    // 记录当前选中的 position
    var selectedPosition = RecyclerView.NO_POSITION
        internal set

    // 点击监听器
    private var onItemClickListener: OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    // 设置监听器的方法
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_sou, parent, false)
        return WallpaperViewHolder(view)
    }

    override fun onBindViewHolder(holder: WallpaperViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.imageView.setImageResource(datalist[position])
        holder.tvName.text = LaunTool.all_txt[position]

        // 根据是否选中来设置不同的背景
        if (position == selectedPosition) {
            holder.conItem.setBackgroundResource(R.drawable.bg_so) // 选中时的背景
        } else {
            holder.conItem.setBackgroundResource(R.drawable.bg_ss) // 默认背景
        }

        // 点击事件回调
        holder.getconItemView().setOnClickListener {
            if(App.audioPlayerUtil.isPlaying()){
                Toast.makeText(context,"Please stop playing first",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 更新选中状态
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(position)

            // 触发外部监听器
            onItemClickListener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int = datalist.size

    inner class WallpaperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: AppCompatImageView = itemView.findViewById(R.id.aiv_icon)
        var tvName: AppCompatTextView = itemView.findViewById(R.id.tv_name)
        var conItem: ConstraintLayout = itemView.findViewById(R.id.con_item)

        fun getconItemView(): ConstraintLayout {
            return conItem
        }
    }

    //更新选中状态
    fun updateSelectedPosition(position: Int) {
        Log.e("TAG", "updateSelectedPosition: ${position}", )
        val previousPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousPosition)
        notifyItemChanged(position)
    }
}

