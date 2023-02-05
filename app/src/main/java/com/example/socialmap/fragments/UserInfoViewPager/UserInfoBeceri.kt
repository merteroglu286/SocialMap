package com.example.socialmap.fragments.UserInfoViewPager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.socialmap.BecerilerModel
import com.example.socialmap.R
import com.example.socialmap.activities.UserInfoActivity
import com.example.socialmap.databinding.FragmentBeceriBinding
import com.example.socialmap.databinding.FragmentUserInfoBeceriBinding
import com.google.firebase.database.*

class UserInfoBeceri : Fragment() {

    private lateinit var binding : FragmentUserInfoBeceriBinding
    private var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserInfoBeceriBinding.inflate(layoutInflater)

        val activity: UserInfoActivity? = activity as UserInfoActivity?
        val myDataFromActivity: String? = activity!!.getMyData()
        userId = myDataFromActivity.toString()

        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Beceriler").child(userId)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val eklenen = snapshot.getValue(BecerilerModel::class.java)

                binding.progressViewSatranc.labelText = eklenen!!.satrancValue
                binding.progressViewBasketbol.labelText = eklenen!!.basketbolValue
                binding.progressViewFutbol.labelText = eklenen!!.futbolValue
                binding.progressViewBilardo.labelText = eklenen!!.bilardoValue

                binding.progressViewSatranc.progress = eklenen!!.satrancValue.toFloat()
                binding.progressViewBasketbol.progress = eklenen!!.basketbolValue.toFloat()
                binding.progressViewFutbol.progress = eklenen!!.futbolValue.toFloat()
                binding.progressViewBilardo.progress = eklenen!!.bilardoValue.toFloat()


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        return binding.root


    }
}