package com.example.socialmap.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.socialmap.BecerilerModel
import com.example.socialmap.ConversationsModel
import com.example.socialmap.Permission.AppPermission
import com.example.socialmap.R
import com.example.socialmap.UserModel
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.activities.DashBoard
import com.example.socialmap.activities.KullaniciSozlesmesi
import com.example.socialmap.activities.SplashScreen
import com.example.socialmap.databinding.FragmentVerifyNumberBinding
import com.example.socialmap.fragments.Profile.Profile
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import com.google.gson.Gson


class VerifyNumber : Fragment() {
/*
    private var _binding: FragmentVerifyNumberBinding? = null
    private val binding get() = _binding!!
*/

    private lateinit var binding : FragmentVerifyNumberBinding
    private lateinit var profileViewModels: ProfileViewModel

    private var firebaseAuth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null

    private lateinit var appPermission: AppPermission
    private lateinit var userArrayList: ArrayList<UserModel>

    private lateinit var sharedPreferences : SharedPreferences

    private lateinit var conversationsArrayList: ArrayList<ConversationsModel>

    private lateinit var sharedPreferences2: SharedPreferences

    private var code : String? = null
    private lateinit var pin: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            code = it.getString("Code")
        }
        print("oncreate calıstı")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        // _binding = FragmentVerifyNumberBinding.inflate(inflater,container,false)


        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_verify_number,container,false)

        conversationsArrayList = arrayListOf<ConversationsModel>()
        conversationsArrayList.add(ConversationsModel("","","","","","",false))

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        appPermission = AppPermission()
        firebaseAuth = FirebaseAuth.getInstance()

        userArrayList = arrayListOf<UserModel>()


        FirebaseApp.initializeApp(/*context=*/this.requireContext())
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )



        binding.btnVerify.setOnClickListener {
            if(checkPin()){
                val credential = PhoneAuthProvider.getCredential(code!!,pin)
                signInUser(credential)
            }
            print("button tıklandı")
        }

        return binding.root
    }

    private fun signInUser(credential: PhoneAuthCredential) {

        firebaseAuth!!.signInWithCredential(credential).addOnCompleteListener {
            if(it!!.isSuccessful){
                val map = mapOf(
                    "number" to firebaseAuth!!.currentUser!!.phoneNumber,
                    "uid" to firebaseAuth!!.currentUser!!.uid,
                )
                databaseReference!!.child(firebaseAuth?.uid!!).updateChildren(map)

                beceriKaydet()

                conversationsArrayList = arrayListOf<ConversationsModel>()

                conversationsArrayList.add(ConversationsModel("","","","","","",true))


                sharedPreferences = requireContext().getSharedPreferences("Conversations", Context.MODE_PRIVATE)
                var editor = sharedPreferences.edit()
                var gson = Gson()
                var json : String = gson.toJson(conversationsArrayList)
                editor.putString("user",json)
                editor.apply()

                val ref: DatabaseReference =
                    FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth!!.uid.toString())

                ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(UserModel::class.java)

                        checkIfFragmentAttached {
                            if (user!!.kayitliMi == true){
                                sharedPreferences2 = requireContext()!!.getSharedPreferences("MY_INFO", Context.MODE_PRIVATE)
                                var editor2 = sharedPreferences2.edit()

                                editor2.putString("name",user.name)
                                editor2.putString("image",user.image)
                                editor2.putString("uid",user.uid)
                                editor2.putString("status",user.status)
                                editor2.putString("isletmeAdi",user.isletmeAdi)
                                editor2.putString("isletmeTuru",user.isletmeTuru)
                                editor2.putInt("isletmeTipi",user.hesapTipi)
                                editor2.putBoolean("kayitliMi",true)
                                editor2.apply()
                                startActivity(Intent(this, SplashScreen::class.java))
                                requireActivity().finish()
                            }else{

                                activity!!.supportFragmentManager
                                    .beginTransaction()
                                    .replace(R.id.main_container, com.example.socialmap.fragments.GetUserData())
                                    .commit();
                            }
                        }
                        }


                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })



            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(code: String,) =
            VerifyNumber().apply {
                arguments = Bundle().apply {
                    putString("Code", code)
                }
                print("80.satır")
            }
    }

    private fun checkPin(): Boolean{
        pin= binding.otpTextView.text.toString()
        if(pin.isEmpty()) {
            binding.otpTextView.error = "Alan gereklidir."
            return false
        } else if (pin.length<6){
            binding.otpTextView.error = " Geçerli pini giriniz."
            return false
        }else return true
    }

    private fun beceriKaydet(){
        var reference = FirebaseDatabase.getInstance().getReference("Beceriler").child(firebaseAuth!!.uid.toString())

        val map = mapOf(
            "satrancValue" to "0",
            "basketbolValue" to "0",
            "futbolValue" to "0",
            "bilardoValue" to "0",
            "uid" to firebaseAuth!!.uid!!.toString()
        )
        BecerilerModel("0","0","0","0",firebaseAuth!!.uid.toString())
        reference.setValue(map)
    }

    fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }
}