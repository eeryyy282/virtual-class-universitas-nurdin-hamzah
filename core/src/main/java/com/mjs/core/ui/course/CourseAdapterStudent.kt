package com.mjs.core.ui.course

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mjs.core.R
import com.mjs.core.databinding.ItemCourseDetailBinding
import com.mjs.core.domain.model.Course

@Suppress("DEPRECATION")
class CourseAdapterStudent : ListAdapter<Course, CourseAdapterStudent.ListViewHolder>(COURSE_DIFF_CALLBACK) {
    var onItemClick: ((Course) -> Unit)? = null
    var getLecturerNameById: ((Long) -> String?)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder {
        val binding =
            ItemCourseDetailBinding.inflate(
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
        private val binding: ItemCourseDetailBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Course) {
            with(binding) {
                tvCourseName.text = data.courseName
                tvCourseId.text = data.courseId
                tvRoom.text = data.room
                tvLecturerName.text =
                    getLecturerNameById?.invoke(data.lecturerId) ?: data.lecturerId.toString()
                tvCredits.text = "${data.credit} credits"

                if (!data.courseImage.isNullOrEmpty()) {
                    Glide
                        .with(itemView.context)
                        .load(data.courseImage)
                        .placeholder(R.drawable.course_photo_profile)
                        .error(R.drawable.course_photo_profile)
                        .into(ivCourse)
                } else {
                    ivCourse.setImageResource(R.drawable.course_photo_profile)
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
