package com.example.pharmcy_app.ui.medicament

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmcy_app.R
import com.example.pharmcy_app.databinding.FragmentMedicamentBinding
import com.example.pharmcy_app.receiclerView.DataMedicament
import com.example.pharmcy_app.receiclerView.DataModel
import com.example.pharmcy_app.receiclerView.DataPharmacie
import com.example.pharmcy_app.receiclerView.ModelAdapter
import com.example.pharmcy_app.ui.FragmentCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MedicamentFragment : Fragment() {
    private lateinit var fragmentCallback: FragmentCallback
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentCallback = context as FragmentCallback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        var searchItem = menu.findItem(R.id.app_bar_search)
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.toString().isNotEmpty()) {
                            searchInList(newText)
                    } else {
                        recyclerView.adapter = medicamentAdapter
                        medicamentAdapter.setDataList(dataList)
                    }
                    return false
                }
            })
        }

    }
    fun searchInList(value: String){

        //dataList nom
        var saerchList = ArrayList<DataModel>()
        for (medicament  in dataList){
            if (value.uppercase() in (medicament as DataMedicament).nom.uppercase()){
                saerchList.add(medicament)
            }
        }
        recyclerView.adapter = medicamentAdapter
        medicamentAdapter.setDataList(saerchList)

    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var medicamentAdapter: ModelAdapter
    private var dataList: MutableList<DataModel> = ArrayList<DataModel>()
    private var _binding: FragmentMedicamentBinding? = null
    private val binding get() = _binding!!
    private var role: Int = 2
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMedicamentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setData()
        val sharedPreference =  activity?.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
        role = sharedPreference?.getInt("role", Int.MAX_VALUE) as Int
        if(role == 1){
            binding.fabBtnAdd.visibility = View.VISIBLE
        }
        binding.fabBtnAdd.setOnClickListener {
            view?.findNavController()?.navigate(R.id.nav_medicament_form)
        }

        fragmentCallback.navigationDetected(R.id.page_medicament)
        return root
    }





    private fun setData() {
        val database = Firebase.database
        val myRef = database.getReference("pharmacyApp/medicaments")
        recyclerView = binding.recyclerViewMedicament
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        medicamentAdapter = ModelAdapter()
        recyclerView.adapter = medicamentAdapter
        binding.loading.visibility = View.VISIBLE
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.loading.visibility = View.GONE
                dataSnapshot.children.forEach {
                    var medicament  = it.getValue( DataMedicament::class.java)  as DataMedicament
                    medicament.key = it.key.toString()
                    dataList.add(medicament)
                }
                // set medicament
                recyclerView.adapter = medicamentAdapter
                medicamentAdapter.setDataList(dataList)

            }

            override fun onCancelled(error: DatabaseError) {
                binding.loading.visibility = View.GONE
                // Failed to read value
                Log.w("Tag error", "Failed to read value.", error.toException())
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        dataList.clear()
//        _binding = null
    }
}