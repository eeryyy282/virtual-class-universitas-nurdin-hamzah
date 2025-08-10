package com.mjs.core.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mjs.core.databinding.ItemTaskDetailBinding
import com.mjs.core.domain.model.Tugas
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class TaskAdapterDosen : RecyclerView.Adapter<TaskAdapterDosen.ListViewHolder>() {
    private var listData = ArrayList<Tugas>()
    var getClassName: ((String) -> String)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newListData: List<Tugas>?) {
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
            ItemTaskDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: ListViewHolder,
        position: Int,
    ) {
        val data = listData[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = listData.size

    @Suppress("DEPRECATION")
    inner class ListViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemTaskDetailBinding.bind(itemView)

        @SuppressLint("SimpleDateFormat")
        fun bind(data: Tugas) {
            with(binding) {
                tvMeetingTask.text = data.judulTugas
                tvDescriptionTask.text = data.deskripsi
                tvDeadlineDate.text = formatDeadline(data.tanggalSelesai)
                getClassName?.let { getClassNameFunc ->
                    tvSubject.text = getClassNameFunc(data.kelasId)
                }
            }
        }

        private fun formatDeadline(dateString: String): String =
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                date?.let {
                    val outputFormat =
                        SimpleDateFormat("HH.mm 'WIB' | dd MMMM yyyy", Locale("id", "ID"))
                    outputFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                    outputFormat.format(it)
                } ?: dateString
            } catch (e: Exception) {
                e.printStackTrace()
                dateString
            }
    }
}
