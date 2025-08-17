package com.mjs.core.ui.classroom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mjs.core.databinding.ItemClassroomDetailDosenBinding
import com.mjs.core.domain.model.Kelas

@Suppress("DEPRECATION")
class ClassroomDetailAdapterDosen : ListAdapter<Kelas, ClassroomDetailAdapterDosen.ListViewHolder>(KELAS_DIFF_CALLBACK) {
    var onItemClick: ((Kelas) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder {
        val binding =
            ItemClassroomDetailDosenBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ListViewHolder,
        position: Int,
    ) {
        val data = getItem(position)
        holder.bind(data)
    }

    inner class ListViewHolder(
        private val binding: ItemClassroomDetailDosenBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Kelas) {
            with(binding) {
                tvSubject.text = data.namaKelas
                tvCodeSubject.text = data.kelasId
                tvClassroomLocation.text = data.ruang
                tvCreditsSubject.text = data.credit.toString()
                Glide
                    .with(itemView.context)
                    .load(data.classImage)
                    .into(ivClassroom)
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
