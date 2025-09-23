package com.mjs.core.ui.course

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mjs.core.databinding.ItemCourseBinding
import com.mjs.core.domain.model.Course

class CourseAdapterLecturer : RecyclerView.Adapter<CourseAdapterLecturer.ListViewHolder>() {
    private val listData = ArrayList<Pair<String, List<Course>>>()
    var onItemClick: ((Course) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newListData: List<Pair<String, List<Course>>>?) {
        if (newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder {
        val binding =
            ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount() = listData.size

    override fun onBindViewHolder(
        holder: ListViewHolder,
        position: Int,
    ) {
        val (semester, courses) = listData[position]
        holder.bind(semester, courses)
    }

    inner class ListViewHolder(
        private val binding: ItemCourseBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            semester: String,
            data: List<Course>,
        ) {
            binding.tvTitleCourseSemester.text = semester
            val detailAdapter = CourseDetailAdapterLecturer()
            detailAdapter.submitList(data)
            detailAdapter.onItemClick = onItemClick
            binding.rvCourseDetail.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = detailAdapter
                setHasFixedSize(true)
            }
        }
    }
}
