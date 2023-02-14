package com.example.pharmcy_app.ui.medicament

import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.pharmcy_app.R
import com.example.pharmcy_app.databinding.FragmentMedicamentBinding
import com.example.pharmcy_app.databinding.FragmentMedicamentDetailsBinding
import com.example.pharmcy_app.receiclerView.DataMedicament
import com.example.pharmcy_app.receiclerView.DataModel
import com.example.pharmcy_app.ui.pharmacie.PharmacieDetailsFragment
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.flow.merge

class FragmentMedicamentDetails : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentMedicamentDetailsBinding? = null
    private val binding get() = _binding!!
    private  var role: Int = 2
    private  var isResrved = false
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
        _binding = FragmentMedicamentDetailsBinding.inflate(inflater, container, false)
        val sharedPreference =  activity?.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
        role = sharedPreference?.getInt("role", Int.MAX_VALUE) as Int
        var key = sharedPreference?.getString("key", "") as String

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
        val med = gson.fromJson(arguments?.getString("data"), DataMedicament::class.java)
        setData(med)
        reserverMedicament(DataMedicament(), key)

        binding.fabBtnEdit.setOnClickListener {
            var bundle = Bundle()
            val gson = Gson()
            var medJson = gson.toJson(med)
            bundle.putSerializable("data", medJson)
            view?.findNavController()?.navigate(R.id.nav_medicament_form, bundle)
        }

        binding.fabBtnDelete.setOnClickListener {
            val builder = AlertDialog.Builder(activity as Context)
            builder.setMessage("Do u want to remove this medicament ?")
                .setPositiveButton("Yes",
                    DialogInterface.OnClickListener { dialog, id ->
                        binding.loading.visibility = View.VISIBLE
                        val database = Firebase.database
                        val myRef = database.getReference("pharmacyApp")
                        myRef.child("medicaments/" + med.key).removeValue().addOnSuccessListener {
                            binding.loading.visibility = View.GONE
                            view?.findNavController()?.navigate(R.id.nav_medicament)
                        }
                    })
                .setNegativeButton("No", null)
            val dialog = builder.create()
            dialog.show()
        }

        binding.reservMed.setOnClickListener {
            if(isResrved){
                isResrved = false
            }else{
                isResrved = true
            }
            if(isResrved){
                binding.reservMed.setText("Reserver")
                binding.reservMed.setBackgroundColor(R.color.teal_700)
            }else{
                binding.reservMed.setText("Detruire")
                binding.reservMed.setBackgroundColor(R.color.black)
            }

            reserverMedicament(med, key, true)
        }
        return root
    }

    private fun reserverMedicament(med: DataMedicament, key: String, change: Boolean = false){
        val database = Firebase.database
        var gson = Gson()
        var arr: ArrayList<String> = ArrayList()
        var newReservation: ArrayList<String> = ArrayList()
        val myRef = database.getReference("Users/${key}/reservation")
        myRef.get().addOnSuccessListener {
            if(it.value.toString() != "null" && it.value.toString() != "[]"){
                arr = gson.fromJson(it.value.toString(), ArrayList::class.java) as ArrayList<String>
                val index = arr.indexOf(med.key)
                if(index == -1){
                    if(change){
                        arr.add(med.key)
                    }else{
                        isResrved = false
                        binding.reservMed.setText("Detruire")
                        binding.reservMed.setBackgroundColor(R.color.black)
                    }
                }else{

                    if(change) {
                        arr.removeAt(index)
                    }else{
                        isResrved = true
                        binding.reservMed.setText("Reserver")
                        binding.reservMed.setBackgroundColor(R.color.teal_700)
                    }
                }
            }else{
                if(change){
                    arr.add(med.key)
                }else{
                    isResrved = true
                    binding.reservMed.setText("Reserver")
                    binding.reservMed.setBackgroundColor(R.color.teal_700)

                }
            }
            if(change){
                myRef.setValue(gson.toJson(arr))
            }
        }
    }

    private  fun setData(medicament: DataMedicament){
        binding.nom.text = medicament.nom
        if("https" in medicament.img){
            Glide.with(binding.root).load(medicament.img).into(binding.image)
        }else{
            binding.image.setImageBitmap(decode(medicament.img))
        }
        binding.prix.text = medicament.prix.toString() + "DH"
        binding.details.text = medicament.details.toString()
        binding.titlePharmacie.text = medicament.titlePharmacie.toString()
        binding.stock.text = medicament.stock.toString()
    }

    private fun decode(imageString: String): Bitmap {
        val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) as Bitmap
        return bitmap
    }

}