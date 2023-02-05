package com.example.socialmap.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmap.Adapter.DmScreenAdapter
import com.example.socialmap.ConversationsModel
import com.example.socialmap.Permission.AppPermission
import com.example.socialmap.R
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.databinding.FragmentChatBinding
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson


class ChatFragment : Fragment() {

    private lateinit var chatBinding: FragmentChatBinding
    private lateinit var profileViewModels: ProfileViewModel

    private lateinit var appPermission: AppPermission

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : DmScreenAdapter

    //private lateinit var messageArrayList: MutableList<ConversationsModel>
    private lateinit var conversationsArrayList: ArrayList<ConversationsModel>

    private var mMenu: Menu? = null




    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chatBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_chat,container,false)

        appPermission = AppPermission()
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireContext()!!.getSharedPreferences("Conversations", Context.MODE_PRIVATE)




        profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity()!!.application).create(
            ProfileViewModel::class.java)

        profileViewModels.getUser().observe(viewLifecycleOwner, Observer {  userModel ->
            chatBinding.userModel = userModel


        })

        recyclerView = chatBinding.dmRecyclerView

        //messageArrayList = arrayListOf<ConversationsModel>()
        conversationsArrayList = arrayListOf<ConversationsModel>()


        var editor = sharedPreferences.edit()
        var gson = Gson()
        var json : String

        loadData()

        adapter = DmScreenAdapter(conversationsArrayList.asReversed(),requireContext())
        checkIfFragmentAttached {
            recyclerView.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        }


        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        //recyclerView.addItemDecoration(DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL))

/*
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                messageArrayList.clear()
                if(snapshot.exists()){
                    for(userSnapShot in snapshot.children){
                        val user = userSnapShot.getValue(UserModel::class.java)
                        chatBinding.userModel = user
                        if (user != null) {
                            if(!user.uid.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                                messageArrayList.add(user)
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
 */

        val databaseReference: Query =
            FirebaseDatabase.getInstance().getReference("Conversations").child(firebaseAuth.uid.toString()).orderByChild("date")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                conversationsArrayList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val eklenen = dataSnapShot.getValue(ConversationsModel::class.java)
                    conversationsArrayList.add(eklenen!!)
                    json = gson.toJson(conversationsArrayList)
                    editor.putString("user",json)
                    editor.apply()
                }

                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

        chatBinding.chatSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (adapter != null) {
                    adapter!!.filter.filter(newText)
                }
                else {
                    Toast.makeText(context, "calısmıyor", Toast.LENGTH_SHORT).show()
                }
                return false
            }

        })


        return chatBinding.root

    }

    fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }
    private fun loadData() {
        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences("Conversations", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("user", null)
        val type = object : TypeToken<ArrayList<ConversationsModel?>?>() {}.type
        conversationsArrayList = (gson.fromJson<Any>(json, type) as ArrayList<ConversationsModel>?)!!
        if (conversationsArrayList == null) {
            conversationsArrayList = ArrayList()
        }
    }

}