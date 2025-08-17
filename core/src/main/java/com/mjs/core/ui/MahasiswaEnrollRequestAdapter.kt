package com.mjs.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mjs.core.R
import com.mjs.core.databinding.ItemMahasiswaEnrollRequestBinding
import com.mjs.core.domain.model.Mahasiswa

class MahasiswaEnrollRequestAdapter(
    private val onAcceptClickListener: (Mahasiswa) -> Unit,
    private val onRejectClickListener: (Mahasiswa) -> Unit,
) : ListAdapter<Mahasiswa, MahasiswaEnrollRequestAdapter.MahasiswaViewHolder>(MahasiswaDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MahasiswaViewHolder {
        val binding =
            ItemMahasiswaEnrollRequestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return MahasiswaViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MahasiswaViewHolder,
        position: Int,
    ) {
        val mahasiswa = getItem(position)
        holder.bind(mahasiswa, onAcceptClickListener, onRejectClickListener)
    }

    class MahasiswaViewHolder(
        private val binding: ItemMahasiswaEnrollRequestBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            mahasiswa: Mahasiswa,
            onAcceptClickListener: (Mahasiswa) -> Unit,
            onRejectClickListener: (Mahasiswa) -> Unit,
        ) {
            binding.tvUsername.text = mahasiswa.nama
            binding.tvUserId.text = mahasiswa.nim.toString()

            Glide
                .with(itemView.context)
                .load(mahasiswa.fotoProfil)
                .placeholder(R.drawable.profile_photo)
                .error(R.drawable.profile_photo)
                .into(binding.photoProfileUser)

            binding.btnAccept.setOnClickListener {
                onAcceptClickListener(mahasiswa)
            }

            binding.btnReject.setOnClickListener {
                onRejectClickListener(mahasiswa)
            }
        }
    }

    class MahasiswaDiffCallback : DiffUtil.ItemCallback<Mahasiswa>() {
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
