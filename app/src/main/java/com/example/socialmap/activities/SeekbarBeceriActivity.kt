package com.example.socialmap.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.socialmap.BecerilerModel
import com.example.socialmap.ProfileIsModel
import com.example.socialmap.databinding.ActivitySeekbarBeceriBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_seekbar_beceri.*
import java.util.concurrent.TimeUnit

class SeekbarBeceriActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySeekbarBeceriBinding

    private lateinit var firebaseAuth : FirebaseAuth

    var startPoint= 0
    var endPoint= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeekbarBeceriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        seekbarListener(binding.seekbarSatranc,binding.satrancValue)
        seekbarListener(binding.seekbarBasketbol,binding.basketbolValue)
        seekbarListener(binding.seekbarFutbol,binding.futbolValue)
        seekbarListener(binding.seekbarBilardo,binding.bilardoValue)
        seekbarListener(binding.seekbarBowling,binding.bowlingValue)
        seekbarListener(binding.seekbarTenis,binding.tenisValue)
        seekbarListener(binding.seekbarMasaTenisi,binding.masaTenisiValue)
        seekbarListener(binding.seekbarOkey,binding.okeyValue)
        seekbarListener(binding.seekbarPaintball,binding.paintballValue)
        seekbarListener(binding.seekbarGokart,binding.gokartValue)
        seekbarListener(binding.seekbarFifa,binding.fifaValue)
        seekbarListener(binding.seekbarPes,binding.pesValue)
        seekbarListener(binding.seekbarCs16,binding.cs16Value)
        seekbarListener(binding.seekbarCsgo,binding.csgoValue)
        seekbarListener(binding.seekbarValorant,binding.valorantValue)
        seekbarListener(binding.seekbarLol,binding.lolValue)


        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Beceriler").child(firebaseAuth.uid.toString())

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val eklenen = snapshot.getValue(BecerilerModel::class.java)

                binding.satrancValue.text = eklenen!!.satrancValue
                binding.basketbolValue.text = eklenen!!.basketbolValue
                binding.futbolValue.text = eklenen!!.futbolValue
                binding.bilardoValue.text = eklenen!!.bilardoValue
                binding.bowlingValue.text = eklenen!!.bowlingValue
                binding.tenisValue.text = eklenen!!.tenisValue
                binding.masaTenisiValue.text = eklenen!!.masaTenisiValue
                binding.okeyValue.text = eklenen!!.okeyValue
                binding.paintballValue.text = eklenen!!.paintballValue
                binding.gokartValue.text = eklenen!!.gokartValue
                binding.fifaValue.text = eklenen!!.fifaValue
                binding.pesValue.text = eklenen!!.pesValue
                binding.cs16Value.text = eklenen!!.cs16Value
                binding.csgoValue.text = eklenen!!.csgoValue
                binding.valorantValue.text = eklenen!!.valorantValue
                binding.lolValue.text = eklenen!!.lolValue

                binding.seekbarSatranc.progress = eklenen.satrancValue.toInt()
                binding.seekbarBasketbol.progress = eklenen.basketbolValue.toInt()
                binding.seekbarFutbol.progress = eklenen.futbolValue.toInt()
                binding.seekbarBilardo.progress = eklenen.bilardoValue.toInt()
                binding.seekbarBowling.progress = eklenen!!.bowlingValue.toInt()
                binding.seekbarTenis.progress = eklenen!!.tenisValue.toInt()
                binding.seekbarMasaTenisi.progress = eklenen!!.masaTenisiValue.toInt()
                binding.seekbarOkey.progress = eklenen!!.okeyValue.toInt()
                binding.seekbarPaintball.progress = eklenen!!.paintballValue.toInt()
                binding.seekbarGokart.progress = eklenen!!.gokartValue.toInt()
                binding.seekbarFifa.progress = eklenen!!.fifaValue.toInt()
                binding.seekbarPes.progress = eklenen!!.pesValue.toInt()
                binding.seekbarCs16.progress = eklenen!!.cs16Value.toInt()
                binding.seekbarCsgo.progress = eklenen!!.csgoValue.toInt()
                binding.seekbarValorant.progress = eklenen!!.valorantValue.toInt()
                binding.seekbarLol.progress = eklenen!!.lolValue.toInt()


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

        binding.btnKaydet.setOnClickListener {
            var satrancValue = binding.satrancValue.text
            var basketbolValue = binding.basketbolValue.text
            var futbolValue = binding.futbolValue.text
            var bilardoValue = binding.bilardoValue.text
            var bowlingValue = binding.bowlingValue.text
            var tenisValue = binding.tenisValue.text
            var masaTenisiValue = binding.masaTenisiValue.text
            var okeyValue = binding.okeyValue.text
            var paintballValue = binding.paintballValue.text
            var gokartValue = binding.gokartValue.text
            var fifaValue = binding.fifaValue.text
            var pesValue = binding.pesValue.text
            var cs16Value = binding.cs16Value.text
            var csgoValue = binding.csgoValue.text
            var valorantValue = binding.valorantValue.text
            var lolValue = binding.lolValue.text

            var reference = FirebaseDatabase.getInstance().getReference("Beceriler").child(firebaseAuth.currentUser!!.uid)

            if (satrancValue.isNotEmpty() && basketbolValue.isNotEmpty() && futbolValue.isNotEmpty() && bilardoValue.isNotEmpty()
                && bowlingValue.isNotEmpty() && tenisValue.isNotEmpty() && masaTenisiValue.isNotEmpty() && okeyValue.isNotEmpty()
                && paintballValue.isNotEmpty() && gokartValue.isNotEmpty() && fifaValue.isNotEmpty() && pesValue.isNotEmpty()
                && cs16Value.isNotEmpty() && csgoValue.isNotEmpty() && valorantValue.isNotEmpty() && lolValue.isNotEmpty()) {
                val map = mapOf(
                    "satrancValue" to satrancValue,
                    "basketbolValue" to basketbolValue,
                    "futbolValue" to futbolValue,
                    "bilardoValue" to bilardoValue,
                    "bowlingValue" to bowlingValue,
                    "tenisValue" to tenisValue,
                    "masaTenisiValue" to masaTenisiValue,
                    "okeyValue" to okeyValue,
                    "paintballValue" to paintballValue,
                    "gokartValue" to gokartValue,
                    "fifaValue" to fifaValue,
                    "pesValue" to pesValue,
                    "cs16Value" to cs16Value,
                    "csgoValue" to csgoValue,
                    "valorantValue" to valorantValue,
                    "lolValue" to lolValue,
                    "uid" to firebaseAuth!!.uid!!.toString(),
                )
                BecerilerModel(satrancValue.toString(),basketbolValue.toString(),futbolValue.toString(),bilardoValue.toString(),firebaseAuth.uid.toString())
                reference.setValue(map)
            }
            finish()
        }

    }


    private fun seekbarListener(seekbar:SeekBar,value:TextView){
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                value.text = progress.toString()
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
    }

}

