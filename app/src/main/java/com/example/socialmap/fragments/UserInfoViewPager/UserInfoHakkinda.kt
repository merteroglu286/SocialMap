package com.example.socialmap.fragments.UserInfoViewPager

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmap.Adapter.IlgiAlanlariAdapter
import com.example.socialmap.Adapter.MeslekVeEgitimAdapter
import com.example.socialmap.ProfileIlgiAlanlariModel
import com.example.socialmap.ProfileIsModel
import com.example.socialmap.R
import com.example.socialmap.UserModel
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.activities.UserInfoActivity
import com.example.socialmap.databinding.FragmentHakkindaBinding
import com.example.socialmap.databinding.FragmentUserInfoHakkindaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class UserInfoHakkinda : Fragment() {
    private lateinit var fragmentUserInfoHakkindaBinding: FragmentUserInfoHakkindaBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var recyclerView : RecyclerView
    private lateinit var recyclerView2 : RecyclerView
    private lateinit var adapter : MeslekVeEgitimAdapter
    private lateinit var adapter2 : IlgiAlanlariAdapter

    private lateinit var meslekveIsArrayList: ArrayList<ProfileIsModel>
    private lateinit var ilgiAlanlariArrayList: ArrayList<ProfileIlgiAlanlariModel>
    private var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        fragmentUserInfoHakkindaBinding = DataBindingUtil.inflate(inflater,
            com.example.socialmap.R.layout.fragment_user_info_hakkinda,container,false)

        val activity: UserInfoActivity? = activity as UserInfoActivity?
        val myDataFromActivity: String? = activity!!.getMyData()
        userId = myDataFromActivity.toString()

        recyclerView = fragmentUserInfoHakkindaBinding.catItemRecyclerEgitim
        recyclerView2 = fragmentUserInfoHakkindaBinding.recyclerViewIlgiAlanlari

        meslekveIsArrayList = arrayListOf<ProfileIsModel>()
        ilgiAlanlariArrayList = arrayListOf<ProfileIlgiAlanlariModel>()

        firebaseAuth = FirebaseAuth.getInstance()

        adapter = MeslekVeEgitimAdapter(requireContext(),meslekveIsArrayList)
        adapter2 = IlgiAlanlariAdapter(requireContext(),ilgiAlanlariArrayList)

        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("eklenenIsler").child(userId)

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
            FirebaseDatabase.getInstance().getReference("ilgiAlanlari").child(userId)

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

        val databaseReference3 = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)
        databaseReference3.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userModel = snapshot.getValue(UserModel::class.java)
                    fragmentUserInfoHakkindaBinding.userModel = userModel
                    fragmentUserInfoHakkindaBinding.txtProfileStatus.text = userModel!!.status.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        return fragmentUserInfoHakkindaBinding.root

    }

    fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }

}