package com.mjs.core.ui

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
    var getClassName: ((Int) -> String)? = null

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
            getClassNameFunc: ((Int) -> String)?,
        ) {
            with(binding) {
                rvNotFinishedTask.layoutManager = LinearLayoutManager(itemView.context)
                val notFinishedAdapter = TaskDetailAdapter()
                notFinishedAdapter.getClassName = getClassNameFunc
                notFinishedAdapter.setData(notFinished)
                rvNotFinishedTask.adapter = notFinishedAdapter

                rvLateTask.layoutManager = LinearLayoutManager(itemView.context)
                val lateAdapter = TaskDetailAdapter()
                lateAdapter.getClassName = getClassNameFunc
                lateAdapter.setData(late)
                rvLateTask.adapter = lateAdapter
            }
        }
    }
}
