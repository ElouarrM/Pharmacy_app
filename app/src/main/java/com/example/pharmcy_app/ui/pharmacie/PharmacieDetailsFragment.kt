package com.example.pharmcy_app.ui.pharmacie

import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.pharmcy_app.R
import com.example.pharmcy_app.UploadImageFragment
import com.example.pharmcy_app.databinding.FragmentPharmacieDetailsBinding
import com.example.pharmcy_app.receiclerView.DataPharmacie
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson


class PharmacieDetailsFragment : Fragment() {

    private var _binding: FragmentPharmacieDetailsBinding? = null
    private val binding get() = _binding!!
    private var role: Int = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        var item: MenuItem = menu.getItem(0)
        item.setVisible(false)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPharmacieDetailsBinding.inflate(inflater, container, false)
        val sharedPreference =  activity?.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
        role = sharedPreference?.getInt("role", Int.MAX_VALUE) as Int
        if(role == 1){
            binding.fabOpen.visibility = View.VISIBLE
        }else{
            binding.fabOpen.visibility = View.GONE
        }

        binding.fabOpen.setOnClickListener {
            if(binding.fabBtnDelete.visibility == View.VISIBLE){
                binding.fabBtnDelete.visibility = View.GONE
                binding.fabBtnEdit.visibility = View.GONE
                binding.fabOpen.setImageResource(android.R.drawable.arrow_up_float)
            }else{
                binding.fabBtnDelete.visibility = View.VISIBLE
                binding.fabBtnEdit.visibility = View.VISIBLE
                binding.fabOpen.setImageResource(android.R.drawable.arrow_down_float)
            }
        }
        val root: View = binding.root
        val gson = Gson()
        val pharmacie = gson.fromJson(arguments?.getString("data"), DataPharmacie::class.java)
        if(pharmacie != null)
        this.setData(pharmacie)

        binding.textButton.setOnClickListener {
            var bundle = Bundle()
            bundle.putDouble("latitude", pharmacie.latitude)
            bundle.putDouble("longitude", pharmacie.longitude)
            bundle.putString("pharmacieName", pharmacie.nom)
            view?.findNavController()?.navigate(R.id.nav_pharmacie_map, bundle)
        }
        binding.fabBtnEdit.setOnClickListener {
            var bundle = Bundle()
            val gson = Gson()
            var pharmacieJson = gson.toJson(pharmacie)
            bundle.putSerializable("data", pharmacieJson)
            view?.findNavController()?.navigate(R.id.nav_pharmacie_form, bundle)
        }

        binding.fabBtnDelete.setOnClickListener {
            val builder = AlertDialog.Builder(activity as Context)
            builder.setMessage("Do u want to remove this pharmacie ?")
                .setPositiveButton("Yes",
                    DialogInterface.OnClickListener { dialog, id ->
                        binding.loading.visibility = View.VISIBLE
                        val database = Firebase.database
                        val myRef = database.getReference("pharmacyApp")
                        myRef.child("pharmacies/" + pharmacie.key).removeValue().addOnSuccessListener {
                            binding.loading.visibility = View.GONE
                            view?.findNavController()?.navigate(R.id.nav_pharmacie)
                        }
                    })
                .setNegativeButton("No", null)
            val dialog = builder.create()
            dialog.show()
        }
        return root
    }



    private  fun setData(pharmacie: DataPharmacie){
        binding.nom.text = pharmacie.nom
        if("https" in pharmacie.img){
            Glide.with(binding.root).load(pharmacie.img).into(binding.image)
        }else{
            if(pharmacie.img.isNotEmpty() && pharmacie.img != "null"){
                binding.image.setImageBitmap(decode(pharmacie.img))
            }
        }
        binding.details.text = pharmacie.details
        binding.adressePharmacie.text = pharmacie.adresse

    }

    private fun decode(imageString: String): Bitmap {
        val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) as Bitmap
        return bitmap
    }
}