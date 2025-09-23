package com.mjs.core.ui.assignment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mjs.core.R
import com.mjs.core.databinding.ItemAssignmentHomeBinding
import com.mjs.core.domain.model.Assignment
import com.mjs.core.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.Locale

class AssignmentHomeAdapter : ListAdapter<Assignment, AssignmentHomeAdapter.ListViewHolder>(ASSIGNMENT_DIFF_CALLBACK) {
    var getCourseName: ((String) -> String)? = null
    var getCoursePhotoProfile: ((String) -> String?)? = null
    var onItemClick: ((Assignment) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder {
        val binding =
            ItemAssignmentHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        private val binding: ItemAssignmentHomeBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Assignment) {
            with(binding) {
                tvAssignmentTitle.text = data.assignmentTitle
                val deadlineString: String? =
                    data.deadlineDate.let {
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(it)
                    }
                tvAssignmentDeadline.text = DateUtils.formatDeadline(deadlineString)
                getCourseName?.let { getCourseNameFunc ->
                    val courseName = getCourseNameFunc(data.courseId)
                    tvCourseName.text = courseName
                } ?: run {
                    tvCourseName.text = itemView.context.getString(R.string.unknown_course)
                }

                getCoursePhotoProfile?.let { getCoursePhotoProfileFunc ->
                    val coursePhotoUrl = getCoursePhotoProfileFunc(data.courseId)
                    Glide
                        .with(itemView.context)
                        .load(coursePhotoUrl)
                        .placeholder(R.drawable.course_photo_profile)
                        .error(R.drawable.course_photo_profile)
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
