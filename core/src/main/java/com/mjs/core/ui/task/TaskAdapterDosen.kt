package com.mjs.core.ui.task

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mjs.core.R
import com.mjs.core.databinding.ItemTaskDetailBinding
import com.mjs.core.domain.model.Tugas
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class TaskAdapterDosen : ListAdapter<Tugas, TaskAdapterDosen.ListViewHolder>(TUGAS_DIFF_CALLBACK) {
    var getClassName: ((String) -> String)? = null
    var getClassPhotoProfile: ((String) -> String?)? = null
    var onItemClick: ((Tugas) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder {
        val binding =
            ItemTaskDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        private val binding: ItemTaskDetailBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(data: Tugas) {
            with(binding) {
                tvMeetingTask.text = data.judulTugas
                tvDescriptionTask.text = data.deskripsi
                tvDeadlineDate.text = formatDeadline(data.tanggalSelesai)
                getClassName?.let { getClassNameFunc ->
                    tvSubject.text = getClassNameFunc(data.kelasId)
                }
                getClassPhotoProfile?.let { getClassPhotoProfileFunc ->
                    val classPhotoProfile = getClassPhotoProfileFunc(data.kelasId)
                    Glide
                        .with(itemView.context)
                        .load(classPhotoProfile)
                        .placeholder(R.drawable.classroom_photo)
                        .into(ivPhotoProfileSubject)
                } ?: run {
                    Glide
                        .with(itemView.context)
                        .load(R.drawable.classroom_photo)
                        .into(ivPhotoProfileSubject)
                }
            }
        }

        private fun formatDeadline(dateString: String): String =
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                date?.let {
                    val outputFormat =
                        SimpleDateFormat("HH.mm 'WIB' | dd MMMM yyyy", Locale("id", "ID"))
                    outputFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                    outputFormat.format(it)
                } ?: dateString
            } catch (e: Exception) {
                e.printStackTrace()
                dateString
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
