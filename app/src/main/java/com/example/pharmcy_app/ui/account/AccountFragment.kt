package com.example.pharmcy_app.ui.account

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pharmcy_app.R
import com.example.pharmcy_app.Users
import com.example.pharmcy_app.databinding.FragmentAccountBinding
import com.example.pharmcy_app.receiclerView.AccountAdapter
import com.example.pharmcy_app.ui.FragmentCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class AccountFragment : Fragment() {
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

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setData()
        fragmentCallback.navigationDetected(R.id.page_account)
        return root
    }

    private fun setData(){
        val  firebaseAut = FirebaseAuth.getInstance()

        binding.recyclerViewAccount.layoutManager = LinearLayoutManager(context)
        var arrayAdapter = AccountAdapter()
        var listTest = ArrayList<String>()
        listTest.add("email: ${firebaseAut.currentUser?.email}")
        val database = Firebase.database
        val myRef = database.getReference("Users")
        myRef.get().addOnSuccessListener {
                binding.loading.visibility = View.GONE
                binding.linearLayoutParent.visibility = View.VISIBLE

                it.children.forEach {
                    var user  = it.getValue( Users::class.java)  as Users
                    Log.i("userEmail4242", user.Email + " - " + firebaseAut.currentUser?.email)

                    if(user.Email.toString().equals(firebaseAut.currentUser?.email)){
                        listTest.add("Last Name: ${user.lastName}")
                        listTest.add("First Name: ${user.fistName}")
                        listTest.add("Addresse: ${user.adresse}")
                        val sharedPreference =  activity?.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
                        var editor = sharedPreference?.edit()
                        if(user.role.equals("Admin")){
                            editor?.putInt("role", 1)
                            binding.imvCircular.setImageResource(R.drawable.profile_admin)
                        }else{
                            binding.imvCircular.setImageResource(R.drawable.profile_client)
                            editor?.putInt("role", 2)
                        }
                        editor?.putString("key", it.key)
                        editor?.commit()
                        binding.recyclerViewAccount.adapter = arrayAdapter
                        arrayAdapter.setDataList(listTest)
                    }
             }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}