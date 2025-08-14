package com.mjs.core.ui.classroom

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mjs.core.R
import com.mjs.core.databinding.ItemClassroomDetailMahasiswaBinding
import com.mjs.core.domain.model.Kelas

@Suppress("DEPRECATION")
class ClassroomAdapterMahasiswa : ListAdapter<Kelas, ClassroomAdapterMahasiswa.ListViewHolder>(KELAS_DIFF_CALLBACK) {
    var onItemClick: ((Kelas) -> Unit)? = null
    var getDosenName: ((Int) -> String)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder {
        val binding =
            ItemClassroomDetailMahasiswaBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ListViewHolder,
        position: Int,
    ) {
        val data = getItem(position)
        holder.bind(data)
    }

    inner class ListViewHolder(
        private val binding: ItemClassroomDetailMahasiswaBinding,
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
                    onItemClick?.invoke(getItem(adapterPosition))
                }
            }
        }
    }

    companion object {
        private val KELAS_DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Kelas>() {
                override fun areItemsTheSame(
                    oldItem: Kelas,
                    newItem: Kelas,
                ): Boolean = oldItem.kelasId == newItem.kelasId

                override fun areContentsTheSame(
                    oldItem: Kelas,
                    newItem: Kelas,
                ): Boolean = oldItem == newItem
            }
    }
}
