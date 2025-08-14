package com.mjs.core.ui.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mjs.core.R
import com.mjs.core.databinding.ItemTaskHomeBinding
import com.mjs.core.domain.model.Tugas
import com.mjs.core.utils.DateUtils

class TaskHomeAdapter : ListAdapter<Tugas, TaskHomeAdapter.ListViewHolder>(TUGAS_DIFF_CALLBACK) {
    var getClassName: ((String) -> String)? = null
    var getClassPhotoProfile: ((String) -> String?)? = null
    var onItemClick: ((Tugas) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder {
        val binding =
            ItemTaskHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        private val binding: ItemTaskHomeBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
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

                getClassPhotoProfile?.let { getClassPhotoProfileFunc ->
                    val classPhotoUrl = getClassPhotoProfileFunc(data.kelasId)
                    Glide
                        .with(itemView.context)
                        .load(classPhotoUrl)
                        .placeholder(R.drawable.classroom_photo)
                        .error(R.drawable.classroom_photo)
                        .into(ivSubjectPhotoProfile)
                } ?: run {
                    Glide
                        .with(itemView.context)
                        .load(R.drawable.classroom_photo)
                        .into(ivSubjectPhotoProfile)
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
        private val TUGAS_DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Tugas>() {
                override fun areItemsTheSame(
                    oldItem: Tugas,
                    newItem: Tugas,
                ): Boolean = oldItem.assignmentId == newItem.assignmentId

                override fun areContentsTheSame(
                    oldItem: Tugas,
                    newItem: Tugas,
                ): Boolean = oldItem == newItem
            }
    }
}
