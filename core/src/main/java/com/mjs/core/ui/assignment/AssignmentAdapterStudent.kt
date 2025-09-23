package com.mjs.core.ui.assignment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mjs.core.databinding.ItemAssignmentBinding
import com.mjs.core.domain.model.Assignment

class AssignmentAdapterStudent : RecyclerView.Adapter<AssignmentAdapterStudent.ListViewHolder>() {
    private var notFinishedAssignments = ArrayList<Assignment>()
    private var lateAssignments = ArrayList<Assignment>()
    var getCoursesName: ((String) -> String)? = null
    var getCoursesPhotoProfile: ((String) -> String?)? = null
    var onItemClick: ((Assignment) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(
        notFinished: List<Assignment>?,
        late: List<Assignment>?,
    ) {
        notFinishedAssignments.clear()
        notFinished?.let { notFinishedAssignments.addAll(it) }

        lateAssignments.clear()
        late?.let { lateAssignments.addAll(it) }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder {
        val binding =
            ItemAssignmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: ListViewHolder,
        position: Int,
    ) {
        holder.bind(
            notFinishedAssignments,
            lateAssignments,
            getCoursesName,
            getCoursesPhotoProfile,
            onItemClick,
        )
    }

    override fun getItemCount(): Int = 1

    inner class ListViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemAssignmentBinding.bind(itemView)

        fun bind(
            notFinished: List<Assignment>,
            late: List<Assignment>,
            getCoursesNameFunc: ((String) -> String)?,
            getCoursesPhotoProfileFunc: ((String) -> String?)?,
            onItemClickFunc: ((Assignment) -> Unit)?,
        ) {
            with(binding) {
                rvNotFinishedAssignment.layoutManager = LinearLayoutManager(itemView.context)
                val notFinishedAdapter = AssignmentDetailAdapter()
                notFinishedAdapter.getCoursesName = getCoursesNameFunc
                notFinishedAdapter.getCoursesPhotoProfile = getCoursesPhotoProfileFunc
                notFinishedAdapter.onItemClick = onItemClickFunc
                notFinishedAdapter.submitList(notFinished)
                rvNotFinishedAssignment.adapter = notFinishedAdapter

                if (notFinished.isEmpty()) {
                    rvNotFinishedAssignment.visibility = View.GONE
                    ivNoNotFinishedAssignments.visibility = View.VISIBLE
                    tvNoNotFinishedAssignments.visibility = View.VISIBLE
                } else {
                    rvNotFinishedAssignment.visibility = View.VISIBLE
                    ivNoNotFinishedAssignments.visibility = View.GONE
                    tvNoNotFinishedAssignments.visibility = View.GONE
                }

                rvLateAssignment.layoutManager = LinearLayoutManager(itemView.context)
                val lateAdapter = AssignmentDetailAdapter()
                lateAdapter.getCoursesName = getCoursesNameFunc
                lateAdapter.getCoursesPhotoProfile = getCoursesPhotoProfileFunc
                lateAdapter.onItemClick = onItemClickFunc
                lateAdapter.submitList(late)
                rvLateAssignment.adapter = lateAdapter

                if (late.isEmpty()) {
                    rvLateAssignment.visibility = View.GONE
                    ivNoLateAssignments.visibility = View.VISIBLE
                    tvNoLateAssignments.visibility = View.VISIBLE
                } else {
                    rvLateAssignment.visibility = View.VISIBLE
                    ivNoLateAssignments.visibility = View.GONE
                    tvNoLateAssignments.visibility = View.GONE
                }
            }
        }
    }
}
