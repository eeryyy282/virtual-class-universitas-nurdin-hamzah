package com.mjs.core.ui.task

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mjs.core.databinding.ItemTaskDosenCategorizedBinding
import com.mjs.core.domain.model.Tugas

class TaskAdapterDosenCategorized : RecyclerView.Adapter<TaskAdapterDosenCategorized.ListViewHolder>() {
    private var activeTasks = ArrayList<Tugas>()
    private var pastDeadlineTasks = ArrayList<Tugas>()
    var getClassName: ((String) -> String)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(
        newActiveTasks: List<Tugas>?,
        newPastDeadlineTasks: List<Tugas>?,
    ) {
        activeTasks.clear()
        newActiveTasks?.let { activeTasks.addAll(it) }

        pastDeadlineTasks.clear()
        newPastDeadlineTasks?.let { pastDeadlineTasks.addAll(it) }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder {
        val binding =
            ItemTaskDosenCategorizedBinding.inflate(
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
        holder.bind(activeTasks, pastDeadlineTasks, getClassName)
    }

    override fun getItemCount(): Int = 1

    inner class ListViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemTaskDosenCategorizedBinding.bind(itemView)

        fun bind(
            currentActiveTasks: List<Tugas>,
            currentPastDeadlineTasks: List<Tugas>,
            getClassNameFunc: ((String) -> String)?,
        ) {
            with(binding) {
                val activeAdapter = TaskAdapterDosen()
                activeAdapter.getClassName = getClassNameFunc
                activeAdapter.setData(currentActiveTasks)
                rvActiveTasks.apply {
                    layoutManager = LinearLayoutManager(itemView.context)
                    adapter = activeAdapter
                    setHasFixedSize(true)
                }

                if (currentActiveTasks.isEmpty()) {
                    rvActiveTasks.visibility = View.GONE
                    ivNoActiveTasks.visibility = View.VISIBLE
                    tvNoActiveTasks.visibility = View.VISIBLE
                } else {
                    rvActiveTasks.visibility = View.VISIBLE
                    ivNoActiveTasks.visibility = View.GONE
                    tvNoActiveTasks.visibility = View.GONE
                }

                val pastDeadlineAdapter = TaskAdapterDosen()
                pastDeadlineAdapter.getClassName = getClassNameFunc
                pastDeadlineAdapter.setData(currentPastDeadlineTasks)
                rvPastDeadlineTasks.apply {
                    layoutManager = LinearLayoutManager(itemView.context)
                    adapter = pastDeadlineAdapter
                    setHasFixedSize(true)
                }

                if (currentPastDeadlineTasks.isEmpty()) {
                    rvPastDeadlineTasks.visibility = View.GONE
                    ivNoPastDeadlineTasks.visibility = View.VISIBLE
                    tvNoPastDeadlineTasks.visibility = View.VISIBLE
                } else {
                    rvPastDeadlineTasks.visibility = View.VISIBLE
                    ivNoPastDeadlineTasks.visibility = View.GONE
                    tvNoPastDeadlineTasks.visibility = View.GONE
                }
            }
        }
    }
}
