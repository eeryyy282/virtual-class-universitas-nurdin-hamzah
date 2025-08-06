package com.mjs.core.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mjs.core.databinding.ItemScheduleBinding
import com.mjs.core.domain.model.Kelas

class ScheduleAdapter : RecyclerView.Adapter<ScheduleAdapter.ListViewHolder>() {
    private var listData = ArrayList<Pair<String, List<Kelas>>>()
    var onItemClick: ((Kelas) -> Unit)? = null
    var getDosenName: ((String) -> String)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newListData: List<Pair<String, List<Kelas>>>?) {
        if (newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder {
        val binding =
            ItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ListViewHolder,
        position: Int,
    ) {
        val (day, scheduleList) = listData[position]
        holder.bind(day, scheduleList, onItemClick, getDosenName)
    }

    override fun getItemCount(): Int = listData.size

    inner class ListViewHolder(
        private val binding: ItemScheduleBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            day: String,
            scheduleList: List<Kelas>,
            onItemClick: ((Kelas) -> Unit)?,
            getDosenName: ((String) -> String)?,
        ) {
            with(binding) {
                tvTitleDay.text = day

                rvScheduleDetail.layoutManager = LinearLayoutManager(itemView.context)
                val scheduleDetailAdapter = ScheduleDetailAdapter()
                scheduleDetailAdapter.onItemClick = onItemClick
                scheduleDetailAdapter.getDosenName = getDosenName
                scheduleDetailAdapter.setData(scheduleList)
                rvScheduleDetail.adapter = scheduleDetailAdapter
            }
        }
    }
}
