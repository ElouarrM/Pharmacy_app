package com.example.pharmcy_app.ui.medicament

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import com.example.pharmcy_app.R
import com.example.pharmcy_app.databinding.FragmentMedicamentFormBinding
import com.example.pharmcy_app.receiclerView.DataMedicament
import com.example.pharmcy_app.receiclerView.DataPharmacie
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class MedicamentFormFragment : Fragment() {

    private var _binding: FragmentMedicamentFormBinding? = null
    private val binding get() = _binding!!
    private  var medicamentImg: String? = null
    private var pharmacieName: String = ""
    private  val pharmacieList = ArrayList<String>()
    private  var medicament: DataMedicament? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
        childFragmentManager.setFragmentResultListener("sendMsg", this) { key, bundle ->
            val imgUrl = bundle.getString("imgUrl")
            medicamentImg = imgUrl
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
        _binding = FragmentMedicamentFormBinding.inflate(inflater, container, false)



        binding.pharmacieNames.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                pharmacieName = parent.getItemAtPosition(position).toString()
            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        val gson = Gson()
        if(arguments != null){
            medicament = gson.fromJson(arguments?.getString("data"), DataMedicament::class.java)
            init(medicament as DataMedicament)
        }

        binding.fabBtnCheck.setOnClickListener {
            if(arguments == null){
                medicament = DataMedicament("", binding.nom.text.toString(), medicamentImg.toString(),
                    binding.prix.text.toString().toInt(),
                    0, binding.details.text.toString(), pharmacieName, binding.stock.text.toString().toInt())
                addMedicament(medicament as DataMedicament)
            }else{
                var med = medicament as DataMedicament
                if(medicamentImg == null ) medicamentImg = med.img
                medicament = DataMedicament(med.key, binding.nom.text.toString(), medicamentImg.toString(),
                    binding.prix.text.toString().toInt(),
                    0, binding.details.text.toString(), pharmacieName, binding.stock.text.toString().toInt())
                editPharmacie(medicament as DataMedicament)
            }
        }

        setData()

        val root: View = binding.root
        return root
    }


    private fun init(med: DataMedicament){
        binding.nom.setText(med.nom)
        binding.prix.setText(med.prix.toString())
        binding.stock.setText(med.stock.toString())
        binding.details.setText(med.details)

        this.childFragmentManager
            .findFragmentById(R.id.fragment_image)?.setFragmentResult("initImg",
                bundleOf("imgUrl" to med.img)
            )
    }


    private fun addMedicament(med: DataMedicament){
        val database = Firebase.database
        val myRef = database.getReference("pharmacyApp/medicaments")
        myRef.push().setValue(med)
        view?.findNavController()?.navigate(R.id.nav_medicament)
    }

    private fun editPharmacie(med: DataMedicament){
        binding.loading.visibility = View.VISIBLE
        val database = Firebase.database
        val myRef = database.getReference("pharmacyApp")

        val updates: MutableMap<String, Any> = hashMapOf(
            "/medicaments/${med.key}" to med,
        )
        myRef.updateChildren(updates).addOnSuccessListener {
            binding.loading.visibility = View.GONE
            view?.findNavController()?.navigate(R.id.nav_medicament)
        }
    }

    private fun setData(){
        val database = Firebase.database
        val myRef = database.getReference("pharmacyApp/pharmacies")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                dataSnapshot.children.forEach {
                    var pharmacie  = it.getValue( DataPharmacie::class.java)
                    pharmacie = pharmacie as DataPharmacie
                    pharmacieList.add(pharmacie.nom)
                }
                val spin = binding.pharmacieNames

                val adapter: ArrayAdapter<String> = ArrayAdapter(context as Context,   android.R.layout.simple_spinner_item,
                    pharmacieList)
                spin.adapter = adapter

                if(pharmacieList.indexOf(medicament?.titlePharmacie) != -1){
                    binding.pharmacieNames.setSelection(pharmacieList.indexOf(medicament?.titlePharmacie));
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Tag error", "Failed to read value.", error.toException())
            }
        })
    }




}