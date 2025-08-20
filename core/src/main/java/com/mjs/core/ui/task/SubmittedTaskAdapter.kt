package com.mjs.core.ui.task

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mjs.core.R
import com.mjs.core.databinding.ItemSubmittedListBinding

class SubmittedTaskAdapter : ListAdapter<SubmissionListItem, SubmittedTaskAdapter.ViewHolder>(DIFF_CALLBACK) {
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ItemSubmittedListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val submissionListItem = getItem(position)
        holder.bind(submissionListItem)
    }

    inner class ViewHolder(
        private val binding: ItemSubmittedListBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: SubmissionListItem) {
            val submission = item.submissionEntity

            binding.tvStudentName.text = item.studentName ?: "Nama Tidak Tersedia"
            binding.tvStudentNim.text = "NIM: ${submission.nim}"

            Glide
                .with(binding.root.context)
                .load(item.studentPhotoUrl)
                .placeholder(R.drawable.profile_photo)
                .error(R.drawable.profile_photo)
                .into(binding.ivStudentProfile)

            binding.tvSubmissionDate.text = submission.submissionDate

            if (submission.attachment != null) {
                binding.tvAttachmentName.text = submission.attachment
                binding.tvAttachmentName.visibility = View.VISIBLE
                binding.tvNoAttachmentMessage.visibility = View.GONE
            } else {
                binding.tvAttachmentName.visibility = View.GONE
                binding.tvNoAttachmentMessage.visibility = View.VISIBLE
            }

            if (submission.grade != null) {
                binding.tvGrade.text = submission.grade.toString()
                binding.tvGrade.visibility = View.VISIBLE
                binding.tvNoGradeMessage.visibility = View.GONE
            } else {
                binding.tvGrade.visibility = View.GONE
                binding.tvNoGradeMessage.visibility = View.VISIBLE
            }

            if (submission.note != null && submission.note.isNotBlank()) {
                binding.tvSubmissionNote.text = submission.note
                binding.tvSubmissionNote.visibility = View.VISIBLE
                binding.tvNoNoteMessage.visibility = View.GONE
            } else {
                binding.tvSubmissionNote.visibility = View.GONE
                binding.tvNoNoteMessage.visibility = View.VISIBLE
            }

            itemView.setOnClickListener {
                onItemClickCallback?.onItemClicked(item.submissionEntity.submissionId)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(submissionId: Int)
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
