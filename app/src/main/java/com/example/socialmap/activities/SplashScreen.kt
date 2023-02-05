package com.example.socialmap.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.socialmap.AdminPaneli
import com.example.socialmap.ConversationsModel
import com.example.socialmap.R
import com.example.socialmap.UserModel
import com.example.socialmap.Util.AppUtil
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.databinding.ActivityProfileDuzenleBinding
import com.example.socialmap.databinding.ActivitySplashScreenBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson

class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private var firebaseAuth: FirebaseAuth? = null
    private lateinit var appUtil: AppUtil
    private lateinit var sharedPreferences:SharedPreferences
    private var kayitliMi :Boolean = false
    private var hesapTipi : Int = 1
    private lateinit var profileViewModels: ProfileViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        appUtil = AppUtil()

        FirebaseApp.initializeApp(/*context=*/this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )

        sharedPreferences = getSharedPreferences("MY_INFO", Context.MODE_PRIVATE)

        kayitliMi = sharedPreferences.getBoolean("kayitliMi", true)

        if (firebaseAuth?.currentUser?.uid != null){
            profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(
                ProfileViewModel::class.java)

            profileViewModels.getUser().observe(this, androidx.lifecycle.Observer { userModel->
                binding.userModel = userModel

                hesapTipi = userModel.hesapTipi
                Toast.makeText(applicationContext,"hesapTipi $hesapTipi",Toast.LENGTH_SHORT).show()
                //Picasso.get().load(userModel.image).into(binding.imgProfile)
            })
        }
        Handler().postDelayed({

            if (firebaseAuth!!.currentUser == null) {
                startActivity(Intent(this, BaslangicActivity::class.java))
                finish()
            } else {
                if ( kayitliMi == true){
                    FirebaseMessaging.getInstance().token
                        .addOnCompleteListener(OnCompleteListener {
                            if (it.isSuccessful) {
                                val token = it.result!!
                                val databaseReference =
                                    FirebaseDatabase.getInstance().getReference("Users")
                                        .child(appUtil.getUID()!!)

                                val map: MutableMap<String, Any> = HashMap()
                                map["token"] = token
                                databaseReference.updateChildren(map)
                            }
                        })

                    val intent = Intent(this, DashBoard::class.java)
                    intent.putExtra("hesapTipi", hesapTipi)
                    startActivity(intent)
                    finish()
                }else{
                    startActivity(Intent(this, BaslangicActivity::class.java))
                    finish()
                }
            }

        }, 2000)

    }

    private fun getUserData(userId: String?) {

        val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userModel = snapshot.getValue(UserModel::class.java)
                    binding.userModel = userModel
                    hesapTipi = userModel!!.hesapTipi
                    //Picasso.get().load(userModel!!.image).into(activityMessageBinding.imgProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}