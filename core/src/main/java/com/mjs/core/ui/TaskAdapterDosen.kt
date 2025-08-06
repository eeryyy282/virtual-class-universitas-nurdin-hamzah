package com.mjs.core.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mjs.core.databinding.ItemTaskDetailBinding
import com.mjs.core.domain.model.Tugas

class TaskAdapterDosen : RecyclerView.Adapter<TaskAdapterDosen.ListViewHolder>() {
    private var listData = ArrayList<Tugas>()
    var getClassName: ((Int) -> String)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newListData: List<Tugas>?) {
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
            ItemTaskDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: ListViewHolder,
        position: Int,
    ) {
        val data = listData[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = listData.size

    inner class ListViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemTaskDetailBinding.bind(itemView)

        fun bind(data: Tugas) {
            with(binding) {
                tvMeetingTask.text = data.judulTugas
                tvDescriptionTask.text = data.deskripsi
                tvDeadlineDate.text = data.deadline
                getClassName?.let { getClassNameFunc ->
                    tvSubject.text = getClassNameFunc(data.kelasId)
                }
            }
        }
    }
}
