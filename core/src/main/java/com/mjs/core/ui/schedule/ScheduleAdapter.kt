package com.mjs.core.ui.schedule

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mjs.core.databinding.ItemScheduleBinding
import com.mjs.core.domain.model.Course

class ScheduleAdapter : RecyclerView.Adapter<ScheduleAdapter.ListViewHolder>() {
    private var listData = ArrayList<Pair<String, List<Course>>>()
    var onItemClick: ((Course) -> Unit)? = null
    var getLecturerNameById: ((Long) -> String?)? =
        null
    var isForLecturerView: Boolean = false

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newListData: List<Pair<String, List<Course>>>?) {
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
        val (day, courseScheduleList) = listData[position]
        holder.bind(day, courseScheduleList, onItemClick, getLecturerNameById, isForLecturerView)
    }

    override fun getItemCount(): Int = listData.size

    inner class ListViewHolder(
        private val binding: ItemScheduleBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            day: String,
            courseScheduleList: List<Course>,
            onItemClick: ((Course) -> Unit)?,
            getLecturerNameCallback: ((Long) -> String?)?,
            isLecturerView: Boolean,
        ) {
            with(binding) {
                tvTitleDay.text = day
                rvScheduleDetail.layoutManager = LinearLayoutManager(itemView.context)
                val scheduleDetailAdapter = ScheduleDetailAdapter()
                scheduleDetailAdapter.onItemClick = onItemClick
                scheduleDetailAdapter.getLecturerName = getLecturerNameCallback
                scheduleDetailAdapter.isForLecturerView = isLecturerView
                scheduleDetailAdapter.submitList(courseScheduleList)
                rvScheduleDetail.adapter = scheduleDetailAdapter
            }
        }
    }
}
