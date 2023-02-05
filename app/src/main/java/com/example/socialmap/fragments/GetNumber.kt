package com.example.socialmap.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.socialmap.R
import com.example.socialmap.UserModel
import com.example.socialmap.activities.DashBoard
import com.example.socialmap.activities.KullaniciSozlesmesi
import com.example.socialmap.activities.MainActivity
import com.example.socialmap.databinding.FragmentGetNumberBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit


class GetNumber : Fragment() {
  /*
    private var _binding: FragmentGetNumberBinding? = null
    private val binding get() = _binding!!

    private var firebaseAuth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null

    private var number: String? = null
    private var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var code : String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGetNumberBinding.inflate(inflater, container,false)
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        firebaseAuth!!.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true)

        FirebaseApp.initializeApp(/*context=*/this.requireContext())
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )

        binding.btnGenerateOTP.setOnClickListener {
            if(checkNumber()){
                val phoneNumber = binding.countryCodePicker.selectedCountryCodeWithPlus + number
                sendCode(phoneNumber)
            }
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                firebaseAuth!!.signInWithCredential(credential).addOnCompleteListener {
                    if(it.isSuccessful){
                        val userModel =
                            UserModel(
                                "", "", "",
                                firebaseAuth!!.currentUser!!.phoneNumber!!
                            )

                        Log.d("test","getnumber button tıklandı")

                        databaseReference!!.child(firebaseAuth!!.uid!!).setValue(userModel)
                        activity?.supportFragmentManager
                            ?.beginTransaction()
                            ?.replace(R.id.main_container, GetNumber())
                            ?.commit()


                    }
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if(e is FirebaseAuthInvalidCredentialsException)
                    Toast.makeText(context,""+ e.message , Toast.LENGTH_SHORT).show()
                else if(e is FirebaseTooManyRequestsException)
                    Toast.makeText(context,""+e.message,Toast.LENGTH_SHORT).show()
                else Toast.makeText(context,""+e.message,Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationCode: String, p1: PhoneAuthProvider.ForceResendingToken) {
                code = verificationCode
                activity!!.supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.main_container, VerifyNumber.newInstance(
                            code!!
                        )
                    )
                    .commit()
            }

        }


        return binding.root
    }

    private fun sendCode(phoneNumber: String) {
        val options = activity?.let {
            PhoneAuthOptions.newBuilder(firebaseAuth!!)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(it)                 // Activity (for callback binding)
                .setCallbacks(callbacks!!)          // OnVerificationStateChangedCallbacks
                .build()
        }
        PhoneAuthProvider.verifyPhoneNumber(options!!)
    }

    private fun checkNumber(): Boolean{
        number = binding.edtNumber.text.toString().trim()
        if(number!!.isEmpty()){
            binding.edtNumber.error = "Alan gereklidir."
            return false
        }else if(number!!.length<10){
            binding.edtNumber.error = "Sayının uzunluğu 10 olmalıdır"
            return false
        }else return true
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
*/
    private var _binding: FragmentGetNumberBinding? = null
    private val binding get() = _binding!!

    private var number: String? = null
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var code: String? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGetNumberBinding.inflate(inflater, container,false)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")


        binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // perform logic
                Toast.makeText(requireContext(), "seçildi", Toast.LENGTH_SHORT).show()
                binding.btnGenerateOTP.setOnClickListener {

                    if (checkNumber()) {
                        val phoneNumber = binding.countryCodePicker.selectedCountryCodeWithPlus + number
                        sendCode(phoneNumber)
                    }
                }
            }else{
                binding.btnGenerateOTP.isClickable = false

            }
        }


        binding.kullaniciSozlesmesi.setOnClickListener {
            startActivity(Intent(context, KullaniciSozlesmesi::class.java))
        }






        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                firebaseAuth!!.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val map = mapOf(
                            "number" to firebaseAuth!!.currentUser!!.phoneNumber,
                            "uid" to "",
                            "hesapTipi" to 2
                        )
                        databaseReference!!.child(firebaseAuth?.uid!!).updateChildren(map)
                        activity?.supportFragmentManager
                            ?.beginTransaction()
                            ?.replace(R.id.main_container, GetUserData())
                            ?.commit()
                    }
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException)
                    Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
                else if (e is FirebaseTooManyRequestsException)
                    Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
                else Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verificationCode: String,
                p1: PhoneAuthProvider.ForceResendingToken
            ) {
                code = verificationCode
                activity!!.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_container, VerifyNumber.newInstance(code!!))
                    .commit()


            }
        }

        return binding.root
    }

    private fun sendCode(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            requireActivity(),
            callbacks
        )

    }

    private fun checkNumber(): Boolean {
        number = binding.edtNumber.text.toString().trim()
        if (number!!.isEmpty()) {
            binding.edtNumber.error = "Field is required"
            return false
        } else if (number!!.length < 10) {
            binding.edtNumber.error = "Number should be 10 in length"
            return false
        } else return true
    }
}