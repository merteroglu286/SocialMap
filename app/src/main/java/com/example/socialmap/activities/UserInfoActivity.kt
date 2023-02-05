package com.example.socialmap.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.socialmap.Adapter.ViewPagerAdapterUserInfo
import com.example.socialmap.UserModel
import com.example.socialmap.databinding.ActivityUserInfoBinding
import com.example.socialmap.fragments.UserInfoViewPager.UserInfoHakkinda
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class UserInfoActivity : AppCompatActivity() {
    private lateinit var activityUserInfoBinding: ActivityUserInfoBinding
    private var hisId: String? = null
    private var hisImage: String? = null
    private var hisName: String? = null
    private var myString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUserInfoBinding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(activityUserInfoBinding.root)


        val tabLayout= activityUserInfoBinding.tabLayout2
        val viewPager2 = activityUserInfoBinding.viewPager3

        val adapter= ViewPagerAdapterUserInfo(supportFragmentManager,lifecycle)


        viewPager2.adapter=adapter

        TabLayoutMediator(tabLayout,viewPager2){tab,position->
            when(position){
                0->{
                    tab.text="Hakkında"
                }
                1->{
                    tab.text="Sosyal"
                }
                2->{
                    tab.text="Blog"
                }
            }
        }.attach()

        hisId = intent.getStringExtra("hisId")
        hisImage = intent.getStringExtra("hisImage")
        hisName = intent.getStringExtra("hisName")

        Log.i("zxc",hisId.toString())
        Log.i("zxc",hisImage.toString())
        Log.i("zxc",hisName.toString())
        getUserData(hisId)

        //getMyData()

        Glide.with(this).load(hisImage).into(activityUserInfoBinding.imgProfile)
        activityUserInfoBinding.userName.text = hisName.toString()
        myString = hisId.toString()

        activityUserInfoBinding.btnMessage.setOnClickListener {
            val intent = Intent(it.context,MessageActivity::class.java)
            intent.putExtra("hisId",hisId.toString())
            intent.putExtra("hisImage",hisImage.toString())
            intent.putExtra("hisName",hisName.toString())
            it.context.startActivity(intent)
        }

    }

    private fun getUserData(userId: String?) {

        val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userModel = snapshot.getValue(UserModel::class.java)
                    activityUserInfoBinding.userModel = userModel
                    activityUserInfoBinding.userName.text = userModel!!.name

                    //Picasso.get().load(userModel!!.image).into(activityUserInfoBinding.imgProfile)
                    Glide.with(applicationContext).load(userModel!!.image).into(activityUserInfoBinding.imgProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getMyData(): String? {
        return myString
    }

}