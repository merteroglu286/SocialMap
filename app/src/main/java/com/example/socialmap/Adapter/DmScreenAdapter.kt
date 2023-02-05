package com.example.socialmap.Adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmap.ConversationsModel
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.activities.MessageActivity
import com.example.socialmap.databinding.DeleteItemDialogBinding
import com.example.socialmap.databinding.DmScreenRowBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.dm_screen_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class DmScreenAdapter(
    private var appContacts: MutableList<ConversationsModel>,
    context: Context,
):
    RecyclerView.Adapter<DmScreenAdapter.ViewHolder>(),Filterable {

    private var allContact: MutableList<ConversationsModel> = appContacts
    private lateinit var profileViewModels: ProfileViewModel
    private lateinit var myName:String
    private lateinit var myImage:String
    private var context = context
    private lateinit var dialog:  AlertDialog
    private var isEnable = false
    private val itemSelectedList = mutableListOf<Int>()
    private lateinit var deleteItemDialogBinding: DeleteItemDialogBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dmScreenLayoutBinding =
            DmScreenRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DmScreenAdapter.ViewHolder(dmScreenLayoutBinding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conversationModel = allContact[position]
        if(conversationModel.senderId == FirebaseAuth.getInstance().currentUser!!.uid || conversationModel.receiverId == FirebaseAuth.getInstance().currentUser!!.uid){
            //holder.binding.conversationModel = conversationModel


            holder.binding.dmRowName.text = conversationModel.receiverName
            holder.binding.dmRowLastMessage.text = conversationModel.lastMessage
            Glide.with(context).load(conversationModel.receiverImage).into(holder.binding.dmRowImage)

            var currentTime = System.currentTimeMillis()

            val simpleDateFormat = SimpleDateFormat("kk:mm",Locale.getDefault())
            val simpleDateFormat2 = SimpleDateFormat("dd.MM.yy",Locale.getDefault())


            val minute = SimpleDateFormat("mm",Locale.getDefault())
            val hours = SimpleDateFormat("kk",Locale.getDefault())
            val day = SimpleDateFormat("dd",Locale.getDefault())
            val month = SimpleDateFormat("MM",Locale.getDefault())
            val year = SimpleDateFormat("yy",Locale.getDefault())

            val messageMinute = minute.format(conversationModel.date.toLong())
            val messageHours = hours.format(conversationModel.date.toLong())
            val messageDay = day.format(conversationModel.date.toLong())
            val messageMonth = month.format(conversationModel.date.toLong())
            val messageYear = year.format(conversationModel.date.toLong())

            val currentMinute = minute.format(currentTime)
            val currentHours = hours.format(currentTime)
            val currentDay = day.format(currentTime)
            val currentMonth = month.format(currentTime)
            val currentYear = year.format(currentTime)

            val date = simpleDateFormat.format(conversationModel.date.toLong())
            val date2 = simpleDateFormat2.format(conversationModel.date.toLong())


            if(messageDay == currentDay && messageMonth == currentMonth && messageYear == currentYear){
                holder.itemView.dm_row_dateTime.text = date.toString()
            }
            else if((currentDay.toInt() - messageDay.toInt() ) == 1 && messageMonth == currentMonth && messageYear == currentYear){
                holder.itemView.dm_row_dateTime.text = "Dün"
            }
            else{
                holder.itemView.dm_row_dateTime.text = date2.toString()
            }

            Log.i("zxc",messageMinute.toString() + "," + messageHours.toString() + "," + messageDay.toString() + "," + messageMonth.toString() + "," + messageYear.toString())
            Log.i("zxc",currentMinute.toString() + "," + currentHours.toString() + "," + currentDay.toString() + "," + currentMonth.toString() + "," + currentYear.toString())




            if (conversationModel.okunduMu == false){
                holder.itemView.dm_row_lastMessage.setTextColor(Color.BLACK)
            }else{
                holder.itemView.dm_row_lastMessage.setTextColor(Color.GRAY)
            }
        }

/*
        FirebaseDatabase.getInstance().reference.child("Chat")
            .child(FirebaseAuth.getInstance().currentUser!!.uid + userModel.uid)
            .orderByChild("data")
            .limitToLast(1)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChildren()){
                        for(snapshot1 in snapshot.children) {
                            holder.binding.dmRowLastMessage.text = snapshot1.child("message").value.toString()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

 */




        holder.itemView.setOnClickListener {
            val intent = Intent(it.context,MessageActivity::class.java)
            intent.putExtra("hisId",conversationModel.receiverId)
            intent.putExtra("hisImage",conversationModel.receiverImage)
            intent.putExtra("hisName",conversationModel.receiverName)

            var referenceConversationMy = FirebaseDatabase.getInstance().getReference("Conversations")
            val map = mapOf(
                "okunduMu" to true
            )
            referenceConversationMy.child(FirebaseAuth.getInstance().currentUser!!.uid.toString()).child(conversationModel.receiverId.toString()).updateChildren(map)

            it.context.startActivity(intent)

        }

        holder.itemView.setOnLongClickListener {

            val alertDialog = AlertDialog.Builder(it.context)
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            deleteItemDialogBinding = DeleteItemDialogBinding.inflate(inflater)
            alertDialog.setView(deleteItemDialogBinding.root)

            deleteItemDialogBinding.btnYes.setOnClickListener {
                val ref = FirebaseDatabase.getInstance().getReference("Conversations").child(FirebaseAuth.getInstance().currentUser!!.uid.toString()).child(conversationModel.receiverId.toString())

                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (refSnapshot in dataSnapshot.children) {
                            refSnapshot.ref.removeValue()
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
                    }
                })

                val ref2 = FirebaseDatabase.getInstance().getReference("Chat").child(FirebaseAuth.getInstance().currentUser!!.uid.toString()).child(conversationModel.receiverId.toString())

                ref2.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (refSnapshot in dataSnapshot.children) {
                            refSnapshot.ref.removeValue()
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
                    }
                })


                dialog.dismiss()
            }
            deleteItemDialogBinding.btnNo.setOnClickListener {
                dialog.dismiss()
            }

            dialog = alertDialog.create()
            dialog.show()
            true
        }


    }

    override fun getItemCount(): Int {
        return allContact.size
    }

    class ViewHolder(val binding:DmScreenRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(index: Int){
        allContact.removeAt(index)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchContent = constraint.toString()
                if (searchContent.isEmpty())
                    allContact = appContacts
                else {

                    val filterContact = ArrayList<ConversationsModel>()
                    for (conversationModel in appContacts) {

                        if (conversationModel.receiverName.toLowerCase(Locale.ROOT).trim()
                                .contains(searchContent.toLowerCase(Locale.ROOT).trim())
                        )
                            filterContact.add(conversationModel)
                    }
                    allContact = filterContact
                }

                val filterResults = FilterResults()
                filterResults.values = allContact
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                allContact = results?.values as MutableList<ConversationsModel>
                notifyDataSetChanged()

            }
        }
    }
}