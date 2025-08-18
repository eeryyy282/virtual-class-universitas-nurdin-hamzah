package com.mjs.core.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mjs.core.R
import com.mjs.core.databinding.ItemMahasiswaListBinding
import com.mjs.core.domain.model.Mahasiswa

class MahasiswaListAdapter(
    private val onItemClick: (Mahasiswa) -> Unit,
) : ListAdapter<Mahasiswa, MahasiswaListAdapter.UserViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): UserViewHolder {
        val binding =
            ItemMahasiswaListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: UserViewHolder,
        position: Int,
    ) {
        val mahasiswa = getItem(position)
        holder.bind(mahasiswa)
        holder.itemView.setOnClickListener {
            onItemClick(mahasiswa)
        }
    }

    inner class UserViewHolder(
        private val binding: ItemMahasiswaListBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(mahasiswa: Mahasiswa) {
            binding.tvUsername.text = mahasiswa.nama
            binding.tvUserId.text =
                "NIM: ${mahasiswa.nim}"
            Glide
                .with(itemView.context)
                .load(mahasiswa.fotoProfil)
                .placeholder(R.drawable.profile_photo)
                .error(R.drawable.profile_photo)
                .into(binding.photoProfileUser)
        }
    }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Mahasiswa>() {
                override fun areItemsTheSame(
                    oldItem: Mahasiswa,
                    newItem: Mahasiswa,
                ): Boolean = oldItem.nim == newItem.nim

                override fun areContentsTheSame(
                    oldItem: Mahasiswa,
                    newItem: Mahasiswa,
                ): Boolean = oldItem == newItem
            }
    }
}
