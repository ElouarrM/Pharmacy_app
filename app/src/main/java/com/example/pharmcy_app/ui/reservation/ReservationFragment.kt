package com.example.pharmcy_app.ui.reservation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmcy_app.R
import com.example.pharmcy_app.databinding.FragmentMedicamentBinding
import com.example.pharmcy_app.databinding.FragmentReservationBinding
import com.example.pharmcy_app.receiclerView.DataMedicament
import com.example.pharmcy_app.receiclerView.DataModel
import com.example.pharmcy_app.receiclerView.ModelAdapter
import com.example.pharmcy_app.ui.FragmentCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class ReservationFragment : Fragment() {

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
    private var _binding: FragmentReservationBinding? = null
    private val binding get() = _binding!!
    private var role: Int = 2
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReservationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val sharedPreference =  activity?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        role = sharedPreference?.getInt("role", Int.MAX_VALUE) as Int
        var userKey = sharedPreference?.getString("key", "") as String
        setData(userKey)
        if(role == 1){
            binding.fabBtnAdd.visibility = View.VISIBLE
        }
        binding.fabBtnAdd.setOnClickListener {
            view?.findNavController()?.navigate(R.id.nav_medicament_form)
        }

        return root
    }







    private fun setData(userKey: String) {
        val database = Firebase.database
        var gson = Gson()
        val myRef = database.getReference("Users/${userKey}/reservation")
        recyclerView = binding.recyclerViewReservation
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        medicamentAdapter = ModelAdapter()
        recyclerView.adapter = medicamentAdapter
        binding.loading.visibility = View.VISIBLE
        myRef.get().addOnSuccessListener {
            binding.loading.visibility = View.GONE
            if(it.value.toString() != "null"){
                var arr  = gson.fromJson(it.value.toString(), ArrayList::class.java) as ArrayList<String>
                for(elm in arr){
                    val myRef2 = database.getReference("pharmacyApp/medicaments/${elm}")

                    myRef2.get().addOnSuccessListener {
                        var medicament = it.getValue( DataMedicament::class.java)  as DataMedicament
                        dataList.add(medicament)
                        Log.i("kdljsh", medicament.toString())
                        recyclerView.adapter = medicamentAdapter
                        medicamentAdapter.setDataList(dataList)
                    }

                }
            }
//            it.children.forEach {
//                var medicament  = it.getValue( DataMedicament::class.java)  as DataMedicament
//                medicament.key = it.key.toString()
//                dataList.add(medicament)
//            }
            // set medicament

        }
    }


    override fun onDestroyView() {
        Log.i("afsgdhfjgk127", "medicament.toString()")
        super.onDestroyView()
        dataList.clear()
//        _binding = null
    }
}