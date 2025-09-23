package com.mjs.core.ui.course

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mjs.core.databinding.ItemCourseDetailLecturerBinding
import com.mjs.core.domain.model.Course

@Suppress("DEPRECATION")
class CourseDetailAdapterLecturer : ListAdapter<Course, CourseDetailAdapterLecturer.ListViewHolder>(COURSE_DIFF_CALLBACK) {
    var onItemClick: ((Course) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder {
        val binding =
            ItemCourseDetailLecturerBinding.inflate(
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
        private val binding: ItemCourseDetailLecturerBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Course) {
            with(binding) {
                tvCourseName.text = data.courseName
                tvCourseId.text = data.courseId
                tvRoom.text = data.room
                tvCredits.text = "${data.credit} credits"
                Glide
                    .with(itemView.context)
                    .load(data.courseImage)
                    .into(ivCourse)
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
