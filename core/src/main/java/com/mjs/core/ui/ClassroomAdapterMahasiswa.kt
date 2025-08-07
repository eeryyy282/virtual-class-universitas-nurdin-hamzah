package com.mjs.core.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mjs.core.databinding.ItemClassroomDetailBinding
import com.mjs.core.domain.model.Kelas

class ClassroomAdapterMahasiswa : RecyclerView.Adapter<ClassroomAdapterMahasiswa.ListViewHolder>() {
    private var listData = ArrayList<Kelas>()
    var onItemClick: ((Kelas) -> Unit)? = null
    var getDosenName: ((String) -> String)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newListData: List<Kelas>?) {
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
            ItemClassroomDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ListViewHolder,
        position: Int,
    ) {
        val data = listData[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = listData.size

    inner class ListViewHolder(
        private val binding: ItemClassroomDetailBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Kelas) {
            with(binding) {
                tvSubject.text = data.namaKelas
                tvCodeSubject.text = data.deskripsi
                tvClassroomLocation.text = data.jadwal
                tvDosenClassroom.text =
                    getDosenName?.invoke(data.nidn.toString()) ?: data.nidn.toString()
            }
        }

        init {
            binding.root.setOnClickListener {
                @Suppress("DEPRECATION")
                onItemClick?.invoke(listData[adapterPosition])
            }
        }
    }
}
