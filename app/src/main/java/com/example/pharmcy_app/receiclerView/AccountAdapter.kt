package com.example.pharmcy_app.receiclerView

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.pharmcy_app.R


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmcy_app.databinding.AccountItemBinding

class AccountAdapter():RecyclerView.Adapter<AccountAdapter.ViewHolder>() {
    private var dataList: MutableList<String> = ArrayList<String>()
    internal fun setDataList(dataList: MutableList<String>){
        this.dataList = dataList
    }

    class ViewHolder(val binding: AccountItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {
            binding.textItem.text = data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AccountItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList.get(position))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}
