package com.mjs.core.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mjs.core.databinding.ItemScheduleDetailBinding
import com.mjs.core.domain.model.Kelas

class ScheduleDetailAdapter : RecyclerView.Adapter<ScheduleDetailAdapter.ListViewHolder>() {
    private var listData = ArrayList<Kelas>()
    var onItemClick: ((Kelas) -> Unit)? = null
    var getDosenName: ((String) -> String)? = null
    var isForDosenView: Boolean = false

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newListData: List<Kelas>?) {
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
            ItemScheduleDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
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
        private val binding: ItemScheduleDetailBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Kelas) {
            with(binding) {
                tvScheduleClassroom.text =
                    data.jadwal
                        .split(",")
                        .getOrElse(1) { data.jadwal }
                        .trim()
                textSubjectSchedule.text = data.namaKelas
                tvSubjectRoom.text = data.ruang

                if (isForDosenView) {
                    tvDosenSubject.visibility = View.GONE
                } else {
                    tvDosenSubject.visibility = View.VISIBLE
                    tvDosenSubject.text =
                        getDosenName?.invoke(data.nidn.toString()) ?: data.nidn.toString()
                }
            }
        }

        init {
            binding.root.setOnClickListener {
                @Suppress("DEPRECATION")
                onItemClick?.invoke(listData[adapterPosition])
            }
        }
    }
}
