package com.mjs.core.ui.classroom

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Import Glide
import com.mjs.core.R // Import R class for drawable resources
import com.mjs.core.databinding.ItemClassroomDetailBinding
import com.mjs.core.domain.model.Kelas

@Suppress("DEPRECATION")
class ClassroomAdapterMahasiswa : RecyclerView.Adapter<ClassroomAdapterMahasiswa.ListViewHolder>() {
    private var listData = ArrayList<Kelas>()
    var onItemClick: ((Kelas) -> Unit)? = null
    var getDosenName: ((Int) -> String)? = null

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
        @SuppressLint("SetTextI18n")
        fun bind(data: Kelas) {
            with(binding) {
                tvSubject.text = data.namaKelas
                tvCodeSubject.text = data.kelasId
                tvClassroomLocation.text = data.ruang
                tvDosenClassroom.text =
                    getDosenName?.invoke(data.nidn) ?: data.nidn.toString()
                tvCreditsSubject.text = "${data.credit} SKS"
                tvCategorySubject.text = data.category

                if (!data.classImage.isNullOrEmpty()) {
                    Glide
                        .with(itemView.context)
                        .load(data.classImage)
                        .placeholder(R.drawable.classroom_photo)
                        .error(R.drawable.classroom_photo)
                        .into(ivClassroom)
                } else {
                    ivClassroom.setImageResource(R.drawable.classroom_photo)
                }
            }
        }

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(listData[adapterPosition])
                }
            }
        }
    }
}
