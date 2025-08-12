package com.mjs.core.ui.classroom

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mjs.core.databinding.ItemClassroomDosenBinding
import com.mjs.core.domain.model.Kelas

class ClassroomAdapterDosen : RecyclerView.Adapter<ClassroomAdapterDosen.ListViewHolder>() {
    private val listData = ArrayList<Pair<String, List<Kelas>>>()
    var onItemClick: ((Kelas) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newListData: List<Pair<String, List<Kelas>>>?) {
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
            ItemClassroomDosenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount() = listData.size

    override fun onBindViewHolder(
        holder: ListViewHolder,
        position: Int,
    ) {
        val (semester, kelas) = listData[position]
        holder.bind(semester, kelas)
    }

    inner class ListViewHolder(
        private val binding: ItemClassroomDosenBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            semester: String,
            data: List<Kelas>,
        ) {
            binding.tvTitleClassroomSemester.text = semester
            val detailAdapter = ClassroomDetailAdapterDosen()
            detailAdapter.setData(data)
            detailAdapter.onItemClick = onItemClick
            binding.rvClassroomDetail.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = detailAdapter
                setHasFixedSize(true)
            }
        }
    }
}
