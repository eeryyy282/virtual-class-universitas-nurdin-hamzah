package com.mjs.core.ui.submission

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mjs.core.R
import com.mjs.core.databinding.ItemSubmittedListBinding // Nama binding akan diubah nanti jika layout berubah

class SubmissionAdapter :
    ListAdapter<SubmissionListItem, SubmissionAdapter.SubmissionViewHolder>(
        DIFF_CALLBACK,
    ) {
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SubmissionViewHolder {
        val binding =
            ItemSubmittedListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubmissionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SubmissionViewHolder,
        position: Int,
    ) {
        val submissionListItem = getItem(position)
        holder.bind(submissionListItem)
    }

    inner class SubmissionViewHolder(
        private val binding: ItemSubmittedListBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: SubmissionListItem) {
            val submissionEntity = item.submissionEntity

            binding.tvStudentName.text = item.studentName
                ?: itemView.context.getString(R.string.name_not_available)
            binding.tvStudentId.text =
                itemView.context.getString(
                    R.string.student_id_label,
                    submissionEntity.studentId.toString(),
                )

            Glide
                .with(binding.root.context)
                .load(item.studentPhotoUrl)
                .placeholder(R.drawable.profile_photo)
                .error(R.drawable.profile_photo)
                .into(binding.ivStudentProfile)

            binding.tvSubmissionDate.text =
                submissionEntity.submissionDate.toString()

            if (submissionEntity.attachment != null) {
                binding.tvAttachmentName.text = submissionEntity.attachment
                binding.tvAttachmentName.visibility = View.VISIBLE
                binding.tvNoAttachmentMessage.visibility = View.GONE
            } else {
                binding.tvAttachmentName.visibility = View.GONE
                binding.tvNoAttachmentMessage.visibility = View.VISIBLE
            }

            if (submissionEntity.grade != null) {
                binding.tvGrade.text = submissionEntity.grade.toString()
                binding.tvGrade.visibility = View.VISIBLE
                binding.tvNoGradeMessage.visibility = View.GONE
            } else {
                binding.tvGrade.visibility = View.GONE
                binding.tvNoGradeMessage.visibility = View.VISIBLE
            }

            if (submissionEntity.note != null && submissionEntity.note.isNotBlank()) {
                binding.tvSubmissionNote.text = submissionEntity.note
                binding.tvSubmissionNote.visibility = View.VISIBLE
                binding.tvNoNoteMessage.visibility = View.GONE
            } else {
                binding.tvSubmissionNote.visibility = View.GONE
                binding.tvNoNoteMessage.visibility = View.VISIBLE
            }

            itemView.setOnClickListener {
                onItemClickCallback?.onItemClicked(submissionEntity.submissionId)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(submissionId: Long)
    }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<SubmissionListItem>() {
                override fun areItemsTheSame(
                    oldItem: SubmissionListItem,
                    newItem: SubmissionListItem,
                ): Boolean = oldItem.submissionEntity.submissionId == newItem.submissionEntity.submissionId

                override fun areContentsTheSame(
                    oldItem: SubmissionListItem,
                    newItem: SubmissionListItem,
                ): Boolean = oldItem == newItem
            }
    }
}
