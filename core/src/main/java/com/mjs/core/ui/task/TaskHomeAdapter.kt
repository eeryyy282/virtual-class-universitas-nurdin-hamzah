package com.mjs.core.ui.task

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mjs.core.R
import com.mjs.core.databinding.ItemTaskHomeBinding
import com.mjs.core.domain.model.Tugas
import com.mjs.core.utils.DateUtils

class TaskHomeAdapter : RecyclerView.Adapter<TaskHomeAdapter.ListViewHolder>() {
    private var listData = ArrayList<Tugas>()
    var getClassName: ((String) -> String)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newListData: List<Tugas>?) {
        if (newListData == null) {
            listData.clear()
            notifyDataSetChanged()
            return
        }
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

    override fun getItemCount(): Int {
        val count = listData.size
        return count
    }

    inner class ListViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemTaskHomeBinding.bind(itemView)

        fun bind(data: Tugas) {
            with(binding) {
                tvTaskSubject.text = data.judulTugas
                tvTaskDeadline.text = DateUtils.formatDeadline(data.tanggalSelesai)
                getClassName?.let { getClassNameFunc ->
                    val className = getClassNameFunc(data.kelasId)
                    tvSubject.text = className
                } ?: run {
                    tvSubject.text = itemView.context.getString(R.string.unknown_class)
                }
            }
        }
    }
}
