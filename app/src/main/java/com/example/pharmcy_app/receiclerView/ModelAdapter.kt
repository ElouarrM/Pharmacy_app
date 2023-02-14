package com.example.pharmcy_app.receiclerView

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
//import com.bumptech.glide.Glide
import com.example.pharmcy_app.R
import com.example.pharmcy_app.databinding.GridItemBinding
import org.json.JSONObject
import com.google.gson.Gson

class ModelAdapter(): RecyclerView.Adapter<ModelAdapter.ViewHolder>() {
    private var dataList: MutableList<DataModel> = ArrayList<DataModel>()
    internal fun setDataList(dataList: MutableList<DataModel>){
        this.dataList = dataList
    }
    class ViewHolder(val binding: GridItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DataModel) {
            if(data is DataPharmacie){
                binding.nom.text = data.nom
                if("https" in data.img){
                    Glide.with(binding.root).load(data.img).into(binding.img)
                }else{
                    if(data.img.isNotEmpty() && data.img != "null"){
                        binding.img.setImageBitmap(decode(data.img))
                    }
                }
                binding.linearVerticalGroup.visibility = View.GONE
            }else{
                if(data is DataMedicament){
                    binding.linearVerticalGroup.visibility = View.VISIBLE
                    binding.nom.text = data.nom
                    if("https" in data.img){
                        Glide.with(binding.root).load(data.img).into(binding.img)
                    }else{
                        if(data.img.isNotEmpty() && data.img != "null"){
                            binding.img.setImageBitmap(decode(data.img))
                        }
                    }

                    binding.price.text = data.prix.toString() + "DH"
                    binding.nbrAchat.text = "(" + data.nbrAchat.toString() + " achat)"
                }

            }
        }

        private fun decode(imageString: String): Bitmap {

            // Decode base64 string to image
            val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) as Bitmap
            return bitmap
        }

    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GridItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList.get(position))
        var bundle = Bundle()
        val gson = Gson()
        holder.itemView.setOnClickListener{
            if(dataList.get(position) is DataMedicament){
                val output = gson.toJson(dataList.get(position))
                bundle.putSerializable("data",output)
                holder.itemView.findNavController().navigate(R.id.nav_medicament_details,bundle)
            }else{
                val output = gson.toJson(dataList.get(position))
                bundle.putSerializable("data",output)
                holder.itemView.findNavController().navigate(R.id.nav_pharmacie_details, bundle)
            }
        }

    }




}