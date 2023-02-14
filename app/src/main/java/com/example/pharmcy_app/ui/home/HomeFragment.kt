package com.example.pharmcy_app.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pharmcy_app.R
import com.example.pharmcy_app.databinding.FragmentHomeBinding
import com.example.pharmcy_app.receiclerView.*
import com.example.pharmcy_app.ui.FragmentCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {
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
        var item: MenuItem = menu.getItem(0)
        item.setVisible(false)
    }

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val root: View = binding.root


        binding.showAllPharmacie.setOnClickListener {
            view?.findNavController()?.navigate(R.id.nav_pharmacie)
        }

        binding.showAllMedicament.setOnClickListener {
            view?.findNavController()?.navigate(R.id.nav_medicament)
        }
        setData()
        fragmentCallback.navigationDetected(R.id.page_home)
        return root
    }



    private lateinit var modelAdapterMedicament: ModelAdapter
    private lateinit var modelAdapterPharmacie: ModelAdapter
    private var dataListMedicament: MutableList<DataModel> = ArrayList<DataModel>()
    private var dataListPharmacie: MutableList<DataModel> = ArrayList<DataModel>()

    private fun setData() {
        val database = Firebase.database
        val myRef = database.getReference("pharmacyApp")
        binding.recyclerViewMedicament.layoutManager = GridLayoutManager(context, 2)
        modelAdapterMedicament = ModelAdapter()
        binding.recyclerViewPharmacie.layoutManager = GridLayoutManager(context, 2)
        modelAdapterPharmacie = ModelAdapter()

        binding.loading.visibility = View.VISIBLE
        binding.titleMedicament.visibility = View.GONE
        binding.titlePharmacie.visibility = View.GONE

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding?.loading?.visibility = View.GONE
                dataSnapshot.child("medicaments").children.forEach {
                    var medicament  = it.getValue( DataMedicament::class.java) as DataMedicament
                    medicament.key = it.key.toString()
                    if(dataListMedicament.size < 2) dataListMedicament.add(medicament)
                }

                dataSnapshot.child("pharmacies").children.forEach {
                    var pharmacie  = it.getValue( DataPharmacie::class.java)  as DataPharmacie
                    pharmacie.key = it.key.toString()
                    if(dataListPharmacie.size < 2) dataListPharmacie.add(pharmacie)
                }
                binding.titleMedicament.visibility = View.VISIBLE
                binding.titlePharmacie.visibility = View.VISIBLE

                if(dataListMedicament.size == 2){
                    binding.showAllMedicament.visibility = View.VISIBLE
                }
                if(dataListPharmacie.size == 2){
                    binding.showAllPharmacie.visibility = View.VISIBLE
                }
                // set medicament
                binding.recyclerViewMedicament.adapter = modelAdapterMedicament
                modelAdapterMedicament.setDataList(dataListMedicament)
                // set pharmacie
                if(dataListPharmacie.size > 2){
                    binding.showAllPharmacie.visibility = View.VISIBLE
                }
                binding.recyclerViewPharmacie.adapter = modelAdapterPharmacie
                modelAdapterPharmacie.setDataList(dataListPharmacie)

            }

            override fun onCancelled(error: DatabaseError) {
                binding.loading.visibility = View.GONE
                // Failed to read value
                Log.w("Tag error", "Failed to read value.", error.toException())
            }
        })



    }

    override fun onDestroyView() {
        dataListPharmacie.clear()
        dataListMedicament.clear()
        super.onDestroyView()
//        _binding = null
    }
}