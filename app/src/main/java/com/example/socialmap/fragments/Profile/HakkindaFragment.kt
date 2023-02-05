package com.example.socialmap.fragments.Profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmap.Adapter.IlgiAlanlariAdapter
import com.example.socialmap.Adapter.MeslekVeEgitimAdapter
import com.example.socialmap.ProfileIlgiAlanlariModel
import com.example.socialmap.ProfileIsModel
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.databinding.FragmentHakkindaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class HakkindaFragment : Fragment() {


    private lateinit var fragmentHakkindaBinding : FragmentHakkindaBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var profileViewModels: ProfileViewModel
    private lateinit var recyclerView : RecyclerView
    private lateinit var recyclerView2 : RecyclerView
    private lateinit var adapter : MeslekVeEgitimAdapter
    private lateinit var adapter2 : IlgiAlanlariAdapter

    private lateinit var meslekveIsArrayList: ArrayList<ProfileIsModel>
    private lateinit var ilgiAlanlariArrayList: ArrayList<ProfileIlgiAlanlariModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentHakkindaBinding = DataBindingUtil.inflate(inflater,
            com.example.socialmap.R.layout.fragment_hakkinda,container,false)


        recyclerView = fragmentHakkindaBinding.catItemRecyclerEgitim
        recyclerView2 = fragmentHakkindaBinding.recyclerViewIlgiAlanlari

        meslekveIsArrayList = arrayListOf<ProfileIsModel>()
        ilgiAlanlariArrayList = arrayListOf<ProfileIlgiAlanlariModel>()

       firebaseAuth = FirebaseAuth.getInstance()

        profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity()!!.application).create(
            ProfileViewModel::class.java)

        profileViewModels.getUser().observe(viewLifecycleOwner, Observer {  userModel ->
            fragmentHakkindaBinding.userModel = userModel


        })

        adapter = MeslekVeEgitimAdapter(requireContext(),meslekveIsArrayList)
        adapter2 = IlgiAlanlariAdapter(requireContext(),ilgiAlanlariArrayList)


        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("eklenenIsler").child(firebaseAuth.uid.toString())

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                meslekveIsArrayList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val eklenen = dataSnapShot.getValue(ProfileIsModel::class.java)
                    meslekveIsArrayList.add(eklenen!!)
                }

                if (meslekveIsArrayList.size>0){
                    checkIfFragmentAttached {
                        recyclerView.layoutManager = GridLayoutManager(requireContext(),1)
                    }
                    recyclerView.setHasFixedSize(true)
                    recyclerView.adapter = adapter
                    recyclerView.isNestedScrollingEnabled = false

                }
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })


            val databaseReference2: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("ilgiAlanlari").child(firebaseAuth.uid.toString())

        databaseReference2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ilgiAlanlariArrayList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val eklenen = dataSnapShot.getValue(ProfileIlgiAlanlariModel::class.java)
                    ilgiAlanlariArrayList.add(eklenen!!)
                }

                if (ilgiAlanlariArrayList.size>0){
                    checkIfFragmentAttached {
                        recyclerView2.layoutManager = GridLayoutManager(requireContext(),4)
                    }
                    recyclerView2.setHasFixedSize(true)
                    recyclerView2.adapter = adapter2
                    recyclerView2.isNestedScrollingEnabled = false

                }
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })



        return fragmentHakkindaBinding.root
    }

    fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }

}