package com.mjs.core.ui.task

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mjs.core.databinding.ItemTaskBinding
import com.mjs.core.domain.model.Tugas

class TaskAdapterMahasiswa : RecyclerView.Adapter<TaskAdapterMahasiswa.ListViewHolder>() {
    private var notFinishedTasks = ArrayList<Tugas>()
    private var lateTasks = ArrayList<Tugas>()
    var getClassName: ((String) -> String)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(
        notFinished: List<Tugas>?,
        late: List<Tugas>?,
    ) {
        notFinishedTasks.clear()
        notFinished?.let { notFinishedTasks.addAll(it) }

        lateTasks.clear()
        late?.let { lateTasks.addAll(it) }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: ListViewHolder,
        position: Int,
    ) {
        holder.bind(notFinishedTasks, lateTasks, getClassName)
    }

    override fun getItemCount(): Int = 1

    inner class ListViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemTaskBinding.bind(itemView)

        fun bind(
            notFinished: List<Tugas>,
            late: List<Tugas>,
            getClassNameFunc: ((String) -> String)?,
        ) {
            with(binding) {
                rvNotFinishedTask.layoutManager = LinearLayoutManager(itemView.context)
                val notFinishedAdapter = TaskDetailAdapter()
                notFinishedAdapter.getClassName = getClassNameFunc
                notFinishedAdapter.setData(notFinished)
                rvNotFinishedTask.adapter = notFinishedAdapter

                if (notFinished.isEmpty()) {
                    rvNotFinishedTask.visibility = View.GONE
                    ivNoNotFinishedTasks.visibility = View.VISIBLE
                    tvNoNotFinishedTasks.visibility = View.VISIBLE
                } else {
                    rvNotFinishedTask.visibility = View.VISIBLE
                    ivNoNotFinishedTasks.visibility = View.GONE
                    tvNoNotFinishedTasks.visibility = View.GONE
                }

                rvLateTask.layoutManager = LinearLayoutManager(itemView.context)
                val lateAdapter = TaskDetailAdapter()
                lateAdapter.getClassName = getClassNameFunc
                lateAdapter.setData(late)
                rvLateTask.adapter = lateAdapter

                if (late.isEmpty()) {
                    rvLateTask.visibility = View.GONE
                    ivNoLateTasks.visibility = View.VISIBLE
                    tvNoLateTasks.visibility = View.VISIBLE
                } else {
                    rvLateTask.visibility = View.VISIBLE
                    ivNoLateTasks.visibility = View.GONE
                    tvNoLateTasks.visibility = View.GONE
                }
            }
        }
    }
}
