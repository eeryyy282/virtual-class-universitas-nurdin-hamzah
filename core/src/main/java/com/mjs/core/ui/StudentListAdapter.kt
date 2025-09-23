package com.mjs.core.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mjs.core.R
import com.mjs.core.databinding.ItemStudentListBinding
import com.mjs.core.domain.model.Student

class StudentListAdapter(
    private val onItemClick: (Student) -> Unit,
) : ListAdapter<Student, StudentListAdapter.UserViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): UserViewHolder {
        val binding =
            ItemStudentListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: UserViewHolder,
        position: Int,
    ) {
        val student = getItem(position)
        holder.bind(student)
        holder.itemView.setOnClickListener {
            onItemClick(student)
        }
    }

    inner class UserViewHolder(
        private val binding: ItemStudentListBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(student: Student) {
            binding.tvUsername.text = student.name
            binding.tvUserId.text = "NIM: ${student.studentId}"
            Glide
                .with(itemView.context)
                .load(student.profilePicture)
                .placeholder(R.drawable.profile_photo)
                .error(R.drawable.profile_photo)
                .into(binding.photoProfileUser)
        }
    }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Student>() {
                override fun areItemsTheSame(
                    oldItem: Student,
                    newItem: Student,
                ): Boolean = oldItem.studentId == newItem.studentId

                override fun areContentsTheSame(
                    oldItem: Student,
                    newItem: Student,
                ): Boolean = oldItem == newItem
            }
    }
}
