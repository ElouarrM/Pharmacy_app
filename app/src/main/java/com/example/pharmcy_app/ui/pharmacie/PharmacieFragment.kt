package com.example.pharmcy_app.ui.pharmacie

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmcy_app.R
import com.example.pharmcy_app.databinding.FragmentPharmacieBinding
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

class PharmacieFragment : Fragment() {
    private lateinit var fragmentCallback: FragmentCallback
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentCallback = context as FragmentCallback
    }
    private var _binding: FragmentPharmacieBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var pharmacieAdapter: ModelAdapter
    private var dataList: MutableList<DataModel> = ArrayList<DataModel>()
    private  var role: Int = 2

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
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
                        recyclerView.adapter = pharmacieAdapter
                        pharmacieAdapter.setDataList(dataList)
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
            //medicament = medicament as DataMedicament
            if (value.uppercase() in (medicament as DataPharmacie).nom.uppercase()){
                saerchList.add(medicament)
            }
        }
        recyclerView.adapter = pharmacieAdapter
        pharmacieAdapter.setDataList(saerchList)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPharmacieBinding.inflate(inflater, container, false)
        val sharedPreference =  activity?.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
        role = sharedPreference?.getInt("role", Int.MAX_VALUE) as Int
        if(role == 1) binding.fabBtnAdd.visibility = View.VISIBLE

        val root: View = binding.root
        setData()
        binding.fabBtnAdd.setOnClickListener {
            view?.findNavController()?.navigate(R.id.nav_pharmacie_form)
        }
        fragmentCallback.navigationDetected(R.id.page_pharmacie)
        return root
    }

    private fun setData() {
        val database = Firebase.database
        val myRef = database.getReference("pharmacyApp/pharmacies")
        recyclerView = binding.recyclerViewPharmacie
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        pharmacieAdapter = ModelAdapter()
        recyclerView.adapter = pharmacieAdapter
        binding.loading.visibility = View.VISIBLE
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.loading.visibility = View.GONE
                dataSnapshot.children.forEach {
                    var pharmacie  = it.getValue( DataPharmacie::class.java) as DataPharmacie
                    pharmacie.key = it.key.toString()
                    dataList.add(pharmacie)
                }
                // set medicament
                recyclerView.adapter = pharmacieAdapter
                pharmacieAdapter.setDataList(dataList)

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
    }
}