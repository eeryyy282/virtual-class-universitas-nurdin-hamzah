package com.mjs.core.ui.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mjs.core.databinding.ItemScheduleDetailBinding
import com.mjs.core.domain.model.Kelas

class ScheduleDetailAdapter : ListAdapter<Kelas, ScheduleDetailAdapter.ListViewHolder>(KELAS_DIFF_CALLBACK) {
    var onItemClick: ((Kelas) -> Unit)? = null
    var getDosenName: ((String) -> String)? = null
    var isForDosenView: Boolean = false

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
        val data = getItem(position)
        holder.bind(data)
    }

    @Suppress("DEPRECATION")
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
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(getItem(adapterPosition))
                }
            }
        }
    }

    companion object {
        private val KELAS_DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Kelas>() {
                override fun areItemsTheSame(
                    oldItem: Kelas,
                    newItem: Kelas,
                ): Boolean = oldItem.kelasId == newItem.kelasId

                override fun areContentsTheSame(
                    oldItem: Kelas,
                    newItem: Kelas,
                ): Boolean = oldItem == newItem
            }
    }
}
