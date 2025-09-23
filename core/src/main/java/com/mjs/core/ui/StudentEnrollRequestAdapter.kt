package com.mjs.core.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mjs.core.R
import com.mjs.core.databinding.ItemStudentEnrollRequestBinding
import com.mjs.core.domain.model.Student

class StudentEnrollRequestAdapter(
    private val onAcceptClickListener: (Student) -> Unit,
    private val onRejectClickListener: (Student) -> Unit,
) : ListAdapter<Student, StudentEnrollRequestAdapter.StudentViewHolder>(StudentDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): StudentViewHolder {
        val binding =
            ItemStudentEnrollRequestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: StudentViewHolder,
        position: Int,
    ) {
        val student = getItem(position)
        holder.bind(student, onAcceptClickListener, onRejectClickListener)
    }

    class StudentViewHolder(
        private val binding: ItemStudentEnrollRequestBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(
            student: Student,
            onAcceptClickListener: (Student) -> Unit,
            onRejectClickListener: (Student) -> Unit,
        ) {
            binding.tvUsername.text = student.name
            binding.tvUserId.text = "NIM: ${student.studentId}"

            Glide
                .with(itemView.context)
                .load(student.profilePicture)
                .placeholder(R.drawable.profile_photo)
                .error(R.drawable.profile_photo)
                .into(binding.photoProfileUser)

            binding.btnAccept.setOnClickListener {
                onAcceptClickListener(student)
            }

            binding.btnReject.setOnClickListener {
                onRejectClickListener(student)
            }
        }
    }

    class StudentDiffCallback : DiffUtil.ItemCallback<Student>() {
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
