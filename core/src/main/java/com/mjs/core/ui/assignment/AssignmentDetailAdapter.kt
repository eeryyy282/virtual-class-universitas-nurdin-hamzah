package com.mjs.core.ui.assignment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mjs.core.R
import com.mjs.core.databinding.ItemAssignmentDetailBinding
import com.mjs.core.domain.model.Assignment
import com.mjs.core.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.Locale

class AssignmentDetailAdapter : ListAdapter<Assignment, AssignmentDetailAdapter.ListViewHolder>(ASSIGNMENT_DIFF_CALLBACK) {
    var getCoursesName: ((String) -> String)? = null
    var getCoursesPhotoProfile: ((String) -> String?)? = null
    var onItemClick: ((Assignment) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder {
        val binding =
            ItemAssignmentDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        private val binding: ItemAssignmentDetailBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(data: Assignment) {
            with(binding) {
                tvCourseName.text = getCoursesName?.invoke(data.courseId) ?: ""
                tvAssignmentTitle.text = data.assignmentTitle
                tvAssignmentDescription.text = data.description

                val deadlineString: String? =
                    data.deadlineDate.let {
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(it)
                    }
                tvDeadlineDate.text = DateUtils.formatDeadline(deadlineString)

                getCoursesPhotoProfile?.let { getClassPhotoProfileFunc ->
                    val classPhotoProfile = getClassPhotoProfileFunc(data.courseId)
                    Glide
                        .with(itemView.context)
                        .load(classPhotoProfile)
                        .placeholder(R.drawable.course_photo_profile)
                        .into(ivCoursePhotoProfile)
                } ?: run {
                    Glide
                        .with(itemView.context)
                        .load(R.drawable.course_photo_profile)
                        .into(ivCoursePhotoProfile)
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
        private val ASSIGNMENT_DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Assignment>() {
                override fun areItemsTheSame(
                    oldItem: Assignment,
                    newItem: Assignment,
                ): Boolean = oldItem.assignmentId == newItem.assignmentId

                override fun areContentsTheSame(
                    oldItem: Assignment,
                    newItem: Assignment,
                ): Boolean = oldItem == newItem
            }
    }
}
