package com.example.socialmap.fragments.Profile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.example.socialmap.BecerilerModel
import com.example.socialmap.ProfileIsModel
import com.example.socialmap.ProfileStatusActivity
import com.example.socialmap.R
import com.example.socialmap.activities.ProfileDuzenle
import com.example.socialmap.activities.SeekbarBeceriActivity
import com.example.socialmap.databinding.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BeceriFragment : Fragment() {

    private lateinit var binding: FragmentBeceriBinding

    private lateinit var seekbarDialogLayoutBinding: SeekbarDialogLayoutBinding
    private lateinit var dialog: AlertDialog

    private lateinit var firebaseAuth :FirebaseAuth

    var startPoint= 0
    var endPoint= 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBeceriBinding.inflate(layoutInflater)


        firebaseAuth = FirebaseAuth.getInstance()

        binding.fabBeceriDuzenle.setOnClickListener{

            val intent = Intent(it.context, SeekbarBeceriActivity::class.java)
            it.context.startActivity(intent)


            //getIsDialog(it.context)

        }



        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Beceriler").child(firebaseAuth.uid.toString())

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val eklenen = snapshot.getValue(BecerilerModel::class.java)

                binding.progressViewSatranc.labelText = eklenen!!.satrancValue
                binding.progressViewBasketbol.labelText = eklenen!!.basketbolValue
                binding.progressViewFutbol.labelText = eklenen!!.futbolValue
                binding.progressViewBilardo.labelText = eklenen!!.bilardoValue
                binding.progressViewBowling.labelText = eklenen!!.bowlingValue
                binding.progressViewTenis.labelText = eklenen!!.tenisValue
                binding.progressViewMasaTenisi.labelText = eklenen!!.masaTenisiValue
                binding.progressViewOkey.labelText = eklenen!!.okeyValue
                binding.progressViewPaintball.labelText = eklenen!!.paintballValue
                binding.progressViewGokart.labelText = eklenen!!.gokartValue
                binding.progressViewFifa.labelText = eklenen!!.fifaValue
                binding.progressViewPes.labelText = eklenen!!.pesValue
                binding.progressViewCs16.labelText = eklenen!!.cs16Value
                binding.progressViewCsGo.labelText = eklenen!!.csgoValue
                binding.progressViewValorant.labelText = eklenen!!.valorantValue
                binding.progressViewLol.labelText = eklenen!!.lolValue

                binding.progressViewSatranc.progress = eklenen!!.satrancValue.toFloat()
                binding.progressViewBasketbol.progress = eklenen!!.basketbolValue.toFloat()
                binding.progressViewFutbol.progress = eklenen!!.futbolValue.toFloat()
                binding.progressViewBilardo.progress = eklenen!!.bilardoValue.toFloat()
                binding.progressViewBowling.progress = eklenen!!.bowlingValue.toFloat()
                binding.progressViewTenis.progress = eklenen!!.tenisValue.toFloat()
                binding.progressViewMasaTenisi.progress = eklenen!!.masaTenisiValue.toFloat()
                binding.progressViewOkey.progress = eklenen!!.okeyValue.toFloat()
                binding.progressViewPaintball.progress = eklenen!!.paintballValue.toFloat()
                binding.progressViewGokart.progress = eklenen!!.gokartValue.toFloat()
                binding.progressViewFifa.progress = eklenen!!.fifaValue.toFloat()
                binding.progressViewPes.progress = eklenen!!.pesValue.toFloat()
                binding.progressViewCs16.progress = eklenen!!.cs16Value.toFloat()
                binding.progressViewCsGo.progress = eklenen!!.csgoValue.toFloat()
                binding.progressViewValorant.progress = eklenen!!.valorantValue.toFloat()
                binding.progressViewLol.progress = eklenen!!.lolValue.toFloat()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        return binding.root
    }

    private fun getIsDialog(context:Context) {

        val alertDialog = AlertDialog.Builder(context)
        seekbarDialogLayoutBinding = SeekbarDialogLayoutBinding.inflate(layoutInflater)
        alertDialog.setView(seekbarDialogLayoutBinding.root)
        seekbarDialogLayoutBinding.seekbarSatranc.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekbarDialogLayoutBinding.satrancValue.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    startPoint = seekBar.progress
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    endPoint = seekBar.progress
                }
            }

        })

        dialog = alertDialog.create()
        dialog.show()


    }

}