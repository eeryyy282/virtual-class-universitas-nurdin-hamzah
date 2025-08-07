package com.mjs.core.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mjs.core.databinding.ItemTaskHomeBinding
import com.mjs.core.domain.model.Tugas

class TaskHomeAdapter : RecyclerView.Adapter<TaskHomeAdapter.ListViewHolder>() {
    private var listData = ArrayList<Tugas>()
    var getClassName: ((Int) -> String)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newListData: List<Tugas>?) {
        if (newListData == null) return
        listData.clear()
        newListData.sortedByDescending { it.tanggalSelesai }.take(2).let {
            listData.addAll(it)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder {
        val binding =
            ItemTaskHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        private val binding = ItemTaskHomeBinding.bind(itemView)

        fun bind(data: Tugas) {
            with(binding) {
                tvTaskSubject.text = data.judulTugas
                tvTaskDeadline.text = data.tanggalSelesai
                getClassName?.let { getClassNameFunc ->
                    tvSubject.text = getClassNameFunc(data.kelasId)
                }
            }
        }
    }
}
