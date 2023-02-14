package com.example.pharmcy_app.ui.pharmacie

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import com.example.pharmcy_app.R
import com.example.pharmcy_app.UploadImageFragment
import com.example.pharmcy_app.databinding.FragmentPharmacieFormBinding
import com.example.pharmcy_app.receiclerView.DataPharmacie
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class PharmacieFormFragment : Fragment() {
    private var _binding: FragmentPharmacieFormBinding? = null
    private val binding get() = _binding!!
    private  var pharmacie: DataPharmacie? = DataPharmacie()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
        childFragmentManager.setFragmentResultListener("sendMsg", this) { key, bundle ->
            val imgUrl = bundle.getString("imgUrl")
            pharmacie?.img = imgUrl.toString()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        var item: MenuItem = menu.getItem(0)
        item.setVisible(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentPharmacieFormBinding.inflate(inflater, container, false)

        val gson = Gson()
        if(arguments != null){
            Log.i("data", arguments?.getString("data").toString())
            if(arguments?.getString("data") != null){
                pharmacie = gson.fromJson(arguments?.getString("data"), DataPharmacie::class.java)
                init(pharmacie as DataPharmacie)
            }
            if(arguments?.getDouble("latitude") != null){
                pharmacie?.latitude = arguments?.getDouble("latitude") as Double
                pharmacie?.longitude = arguments?.getDouble("longitude") as Double
                binding.adresseMap.setText("lat=${pharmacie?.latitude}, long=${pharmacie?.longitude}")
            }

        }

        binding.fabBtnCheck.setOnClickListener {
            pharmacie?.nom = binding.nom.text.toString()
            pharmacie?.details = binding.details.text.toString()
            pharmacie?.adresse = binding.adresse.text.toString()
            if(pharmacie!!.key.isEmpty()){
                addPharmacie(pharmacie as DataPharmacie)
            }else{
                editPharmacie(pharmacie as DataPharmacie)
            }
        }

        binding.adresseMap.setOnClickListener {
            var bundle = Bundle()
            bundle.putDouble("latitude", 0.0)
            bundle.putDouble("longitude", 0.0)
            bundle.putString("pharmacieName", binding.nom.text.toString())
            pharmacie?.nom = binding.nom.text.toString()

            pharmacie?.details = binding.details.text.toString()
            pharmacie?.adresse = binding.adresse.text.toString()
            val output = gson.toJson(pharmacie)
            bundle.putSerializable("data", output)
            view?.findNavController()?.navigate(R.id.nav_pharmacie_map, bundle)
        }



        val root: View = binding.root
        return root
    }

    private fun init(ph: DataPharmacie){
        binding.nom.setText(ph.nom)
        binding.adresse.setText(ph.adresse)
        binding.details.setText(ph.details)
        Toast.makeText(context, ph.longitude.toString(), Toast.LENGTH_LONG).show()
        binding.adresseMap.setText("lat=${ph?.latitude}, long=${ph?.longitude}")
        this.childFragmentManager
            .findFragmentById(R.id.fragment_image)?.setFragmentResult("initImg",
                bundleOf("imgUrl" to ph.img))
    }

    private fun addPharmacie(ph: DataPharmacie){
        val database = Firebase.database
        val myRef = database.getReference("pharmacyApp/pharmacies")
        myRef.push().setValue(ph)
        view?.findNavController()?.navigate(R.id.nav_pharmacie)
    }

    private fun editPharmacie(ph: DataPharmacie){
        binding.loading.visibility = View.VISIBLE
        val database = Firebase.database
        val myRef = database.getReference("pharmacyApp")

        val updates: MutableMap<String, Any> = hashMapOf(
            "/pharmacies/${ph.key}" to ph,
        )
        myRef.updateChildren(updates).addOnSuccessListener {
            binding.loading.visibility = View.GONE
            view?.findNavController()?.navigate(R.id.nav_pharmacie)
        }
    }



}