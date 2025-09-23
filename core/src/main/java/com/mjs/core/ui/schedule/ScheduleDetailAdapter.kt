package com.mjs.core.ui.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mjs.core.databinding.ItemScheduleDetailBinding
import com.mjs.core.domain.model.Course
import com.mjs.core.utils.DateUtils

class ScheduleDetailAdapter : ListAdapter<Course, ScheduleDetailAdapter.ListViewHolder>(COURSE_DIFF_CALLBACK) {
    var onItemClick: ((Course) -> Unit)? = null
    var getLecturerName: ((Long) -> String?)? = null
    var isForLecturerView: Boolean = false

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
        val courseData = getItem(position)
        holder.bind(courseData)
    }

    inner class ListViewHolder(
        private val binding: ItemScheduleDetailBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: Course) {
            with(binding) {
                tvScheduleTime.text = DateUtils.formatScheduleTime(course.schedule.toString())
                tvCourseName.text = course.courseName
                tvRoom.text = course.room

                if (isForLecturerView) {
                    tvLecturerName.visibility = View.GONE
                } else {
                    tvLecturerName.visibility = View.VISIBLE
                    tvLecturerName.text =
                        getLecturerName?.invoke(course.lecturerId) ?: course.lecturerId.toString()
                }
            }
        }

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(getItem(position))
                }
            }
        }
    }

    companion object {
        private val COURSE_DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Course>() {
                override fun areItemsTheSame(
                    oldItem: Course,
                    newItem: Course,
                ): Boolean = oldItem.courseId == newItem.courseId

                override fun areContentsTheSame(
                    oldItem: Course,
                    newItem: Course,
                ): Boolean = oldItem == newItem
            }
    }
}
