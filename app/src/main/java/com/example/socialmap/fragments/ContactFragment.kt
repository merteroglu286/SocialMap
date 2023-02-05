package com.example.socialmap.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmap.Adapter.ContactAdapter
import com.example.socialmap.UserModel
import com.example.socialmap.databinding.FragmentContactBinding
import com.google.firebase.database.*


class ContactFragment : Fragment() {
    private lateinit var binding: FragmentContactBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<UserModel>
    private var contactAdapter: ContactAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentContactBinding.inflate(inflater,container,false)
        userRecyclerView = binding.recyclerViewContact
        userRecyclerView.layoutManager = LinearLayoutManager(context)
        userRecyclerView.setHasFixedSize(true)
        userArrayList = arrayListOf<UserModel>()
        getUserData()

        //binding.contactSearchView.setOnQueryTextFocusChangeListener(filterTextWatcher)

        binding.contactSearchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (contactAdapter != null) {
                    contactAdapter!!.filter.filter(newText)
                    binding.recyclerViewContact.isVisible = newText != ""
                    if (newText == ""){
                        binding.imgSocialMap.isVisible = true
                        binding.recyclerViewContact.isVisible = false
                    }else{
                        binding.imgSocialMap.isVisible = false
                        binding.recyclerViewContact.isVisible = true
                    }

                }
                else {
                    Toast.makeText(context, "calısmıyor", Toast.LENGTH_SHORT).show()
                }
                return false
            }

        })
        return binding.root
    }

    private fun getUserData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener(object : ValueEventListener{


            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    for(userSnapShot in snapshot.children){

                        val user = userSnapShot.getValue(UserModel::class.java)
                        userArrayList.add(user!!)

                    }
                    contactAdapter = ContactAdapter(userArrayList)
                    userRecyclerView.adapter = contactAdapter
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}