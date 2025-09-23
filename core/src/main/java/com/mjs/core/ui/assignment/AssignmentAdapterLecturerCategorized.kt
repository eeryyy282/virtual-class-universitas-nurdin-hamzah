package com.mjs.core.ui.assignment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mjs.core.databinding.ItemAssignmentLecturerCategorizedBinding
import com.mjs.core.domain.model.Assignment

class AssignmentAdapterLecturerCategorized : RecyclerView.Adapter<AssignmentAdapterLecturerCategorized.ListViewHolder>() {
    private var activeAssignment = ArrayList<Assignment>()
    private var pastDeadlineAssignment = ArrayList<Assignment>()
    var getCourseName: ((String) -> String)? = null
    var getCoursePhotoProfile: ((String) -> String?)? = null
    var onItemClick: ((Assignment) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(
        newActiveAssignments: List<Assignment>?,
        newPastDeadlineAssignments: List<Assignment>?,
    ) {
        activeAssignment.clear()
        newActiveAssignments?.let { activeAssignment.addAll(it) }

        pastDeadlineAssignment.clear()
        newPastDeadlineAssignments?.let { pastDeadlineAssignment.addAll(it) }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder {
        val binding =
            ItemAssignmentLecturerCategorizedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return ListViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: ListViewHolder,
        position: Int,
    ) {
        holder.bind(
            activeAssignment,
            pastDeadlineAssignment,
            getCourseName,
            getCoursePhotoProfile,
            onItemClick,
        )
    }

    override fun getItemCount(): Int = 1

    inner class ListViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemAssignmentLecturerCategorizedBinding.bind(itemView)

        fun bind(
            currentActiveAssignments: List<Assignment>,
            currentPastDeadlineAssignments: List<Assignment>,
            getCourseNameFunc: ((String) -> String)?,
            getCoursePhotoProfileFunc: ((String) -> String?)?,
            onItemClickFunc: ((Assignment) -> Unit)?,
        ) {
            with(binding) {
                val activeAdapter = AssignmentAdapterLecturer()
                activeAdapter.getCourseName = getCourseNameFunc
                activeAdapter.getCoursePhotoProfile = getCoursePhotoProfileFunc
                activeAdapter.onItemClick = onItemClickFunc
                activeAdapter.submitList(currentActiveAssignments)
                rvActiveAssignments.apply {
                    layoutManager = LinearLayoutManager(itemView.context)
                    adapter = activeAdapter
                    setHasFixedSize(true)
                }

                if (currentActiveAssignments.isEmpty()) {
                    rvActiveAssignments.visibility = View.GONE
                    ivNoActiveAssignments.visibility = View.VISIBLE
                    tvNoActiveAssignments.visibility = View.VISIBLE
                } else {
                    rvActiveAssignments.visibility = View.VISIBLE
                    ivNoActiveAssignments.visibility = View.GONE
                    tvNoActiveAssignments.visibility = View.GONE
                }

                val pastDeadlineAdapter = AssignmentAdapterLecturer()
                pastDeadlineAdapter.getCourseName = getCourseNameFunc
                pastDeadlineAdapter.getCoursePhotoProfile = getCoursePhotoProfileFunc
                pastDeadlineAdapter.onItemClick = onItemClickFunc
                pastDeadlineAdapter.submitList(currentPastDeadlineAssignments)
                rvPastDeadlineAssignments.apply {
                    layoutManager = LinearLayoutManager(itemView.context)
                    adapter = pastDeadlineAdapter
                    setHasFixedSize(true)
                }

                if (currentPastDeadlineAssignments.isEmpty()) {
                    rvPastDeadlineAssignments.visibility = View.GONE
                    ivNoPastDeadlineAssignments.visibility = View.VISIBLE
                    tvNoPastDeadlineAssignments.visibility = View.VISIBLE
                } else {
                    rvPastDeadlineAssignments.visibility = View.VISIBLE
                    ivNoPastDeadlineAssignments.visibility = View.GONE
                    tvNoPastDeadlineAssignments.visibility = View.GONE
                }
            }
        }
    }
}
