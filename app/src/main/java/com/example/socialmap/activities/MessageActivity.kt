package com.example.socialmap.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.socialmap.*
import com.example.socialmap.Adapter.ChatAdapter
import com.example.socialmap.Constants.AppConstants
import com.example.socialmap.Models.Chat
import com.example.socialmap.R
import com.example.socialmap.Util.AppUtil
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.databinding.ActivityMessageBinding
import com.google.common.reflect.TypeToken
import com.google.firebase.database.*
import org.json.JSONObject
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson


class MessageActivity : AppCompatActivity() {

    private lateinit var activityMessageBinding: ActivityMessageBinding
    private lateinit var profileViewModels: ProfileViewModel
    private var hisId : String? =  null
    private var hisName : String? =  null
    private var hisImage : String? =  null
    private var chatId : String? =  null
    private lateinit var appUtil : AppUtil
    private lateinit var myId : String
    private lateinit var myName : String
    private lateinit var myImage : String
    private lateinit var sharedPreferences: SharedPreferences
    var chatList = ArrayList<Chat>()

    private var lastMessage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMessageBinding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(activityMessageBinding.root)

        appUtil = AppUtil()
        myId = appUtil.getUID()!!
        sharedPreferences = getSharedPreferences("Messages", MODE_PRIVATE)
        myImage = sharedPreferences.getString("myImage","").toString()


        activityMessageBinding.messageRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        //activityMessageBinding.activity = this

        hisId = intent.getStringExtra("hisId")
        hisImage = intent.getStringExtra("hisImage")
        hisName = intent.getStringExtra("hisName")




        profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(
            ProfileViewModel::class.java)

        profileViewModels.getUser().observe(this, androidx.lifecycle.Observer { userModel->
            myName = userModel.name
            myImage = userModel.image

        })

        getUserData(hisId)

        activityMessageBinding.btnSend.setOnClickListener {
            var message:String = activityMessageBinding.msgText.text.toString()
            if(message.isEmpty()){

            }else{
                var reference = FirebaseDatabase.getInstance().getReference("Chat").child(myId.toString()).child(hisId.toString())

                val map = mapOf(
                    "senderId" to myId.toString(),
                    "receiverId" to hisId!!.toString(),
                    "message" to message.toString(),
                    "date" to System.currentTimeMillis().toString(),
                )
                MessageModel(myId.toString(),hisId!!.toString(),message.toString(),System.currentTimeMillis().toString())
                reference.push().setValue(map).addOnSuccessListener {
                    FirebaseDatabase.getInstance().getReference("Chat").child(hisId!!.toString()).child(myId.toString()).push().setValue(map)
                }


/*
                var hashMap: HashMap<String,String> = HashMap()
                hashMap.put("senderId",myId.toString())
                hashMap.put("receiverId",hisId!!.toString())
                hashMap.put("message",message.toString())
                hashMap.put("date",System.currentTimeMillis().toString())
                reference!!.child("Chat").push().setValue(hashMap)
 */
                .addOnSuccessListener {


                    var referenceConversationMy = FirebaseDatabase.getInstance().getReference("Conversations")

                    val map = mapOf(
                        "lastMessage" to lastMessage.toString(),
                        "receiverId" to hisId.toString(),
                        "receiverImage" to hisImage.toString(),
                        "receiverName" to hisName.toString(),
                        "senderId" to myId.toString(),
                        "date" to System.currentTimeMillis().toString(),
                        "okunduMu" to true
                    )
                    ConversationsModel(lastMessage,hisId.toString(),hisImage.toString(),hisName.toString(),myId,System.currentTimeMillis().toString(),true)
                    referenceConversationMy.child(myId).child(hisId!!).updateChildren(map).addOnSuccessListener {
                        var referenceConversationHis = FirebaseDatabase.getInstance().getReference("Conversations")

                        val map = mapOf(
                            "lastMessage" to lastMessage.toString(),
                            "receiverId" to myId.toString(),
                            "receiverImage" to myImage.toString(),
                            "receiverName" to myName.toString(),
                            "senderId" to hisId.toString(),
                            "date" to System.currentTimeMillis().toString(),
                            "okunduMu" to false
                        )
                        ConversationsModel(lastMessage.toString(),myId.toString(),myImage.toString(),myName.toString(),hisId!!.toString(),System.currentTimeMillis().toString(),false)
                        referenceConversationHis.child(hisId!!).child(myId).updateChildren(map)
                    }
                }
                getToken(message)

                activityMessageBinding.msgText.setText(R.string.default_text)

            }
        }



        /*

        activityMessageBinding.btnSend.setOnClickListener {
            val message = activityMessageBinding.msgText.text.toString()
            if(message.isEmpty()){
                Toast.makeText(this,"Mesajınınızı giriniz",Toast.LENGTH_SHORT).show()
            }else{
                sendMessage(message)
            }
        }

        if(chatId != null){
            checkChat(hisId!!)
        }

         */

        activityMessageBinding.btnBack.setOnClickListener {
            finish()
        }

        activityMessageBinding.imgProfile.setOnClickListener {
            val intent = Intent(it.context,UserInfoActivity::class.java)
            intent.putExtra("hisId",hisId.toString())
            intent.putExtra("hisImage",hisImage.toString())
            intent.putExtra("hisName",hisName.toString())
            it.context.startActivity(intent)
        }

        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat").child(myId.toString()).child(hisId.toString())

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)

                    if (chat!!.senderId.equals(myId.toString()) && chat!!.receiverId.equals(hisId.toString()) ||
                        chat!!.senderId.equals(hisId.toString()) && chat!!.receiverId.equals(myId.toString())
                    ) {
                        chatList.add(chat)

                    }
                    lastMessage = chat.message
                }
                val chatAdapter = ChatAdapter(
                    this@MessageActivity,
                    chatList,
                    activityMessageBinding.messageRecyclerView
                )

                activityMessageBinding.messageRecyclerView.adapter = chatAdapter
                activityMessageBinding.messageRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)


            }
        })
        (activityMessageBinding.messageRecyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true


/*
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)

                    if (chat!!.senderId.equals(myId) && chat!!.receiverId.equals(hisId) ||
                        chat!!.senderId.equals(hisId) && chat!!.receiverId.equals(myId)
                    ) {
                        chatList.add(chat)
                    }
                    lastMessage = chat.message
                }

                val chatAdapter = ChatAdapter(this@MessageActivity, chatList)

                activityMessageBinding.messageRecyclerView.adapter = chatAdapter
                activityMessageBinding.messageRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
            }
        })

        (activityMessageBinding.messageRecyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true


 */
    }


    /*

  private fun checkChat(hisId:String){
      val databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(myId)
      val query = databaseReference.orderByChild("member").equalTo(hisId)
      query.addValueEventListener(object : ValueEventListener{
          override fun onDataChange(snapshot: DataSnapshot) {
              if (snapshot.exists()){
                  for (ds in snapshot.children){
                      val member = ds.child("member").value.toString()
                      if (hisId == member){
                          chatId = ds.key
                          readMessages(chatId!!)
                          break
                      }
                  }
              }
          }

          override fun onCancelled(error: DatabaseError) {
              TODO("Not yet implemented")
          }
      })
  }

  private fun createChat(message: String){
      var databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(myId)
      chatId = databaseReference.push().key
      val chatListModel = ChatListModel(chatId!!,message,System.currentTimeMillis().toString(),hisId!!)

      databaseReference.child(chatId!!).setValue(chatListModel)
      databaseReference= FirebaseDatabase.getInstance().getReference("ChatList").child(hisId!!)

      val chatList = ChatListModel(chatId!!,message,System.currentTimeMillis().toString(),myId)

      databaseReference.child(chatId!!).setValue(chatList)

      databaseReference = FirebaseDatabase.getInstance().getReference("Chat").child(chatId!!)

      val messageModel = MessageModel(myId,hisId!!,message, type = "text")
      databaseReference.push().setValue(messageModel)

  }


  private fun sendMessage(message: String){
      if(chatId == null){
          createChat(message)
      }else{
          var databaseReference = FirebaseDatabase.getInstance().getReference("Chat").child(chatId!!)
          val messageModel = MessageModel(myId,hisId!!,message, type = "text")
          databaseReference.push().setValue(messageModel)

          val map: MutableMap<String,Any> = HashMap()
          map["lastMessage"] = message
          map["date"] = System.currentTimeMillis().toString()

          databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(myId).child(chatId!!)

          databaseReference.updateChildren(map)

          databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(hisId!!).child(chatId!!)

          databaseReference.updateChildren(map)
      }
  }

  private fun readMessages(chatId: String) {

      val query = FirebaseDatabase.getInstance().getReference("Chat").child(chatId)

      val firebaseRecyclerOptions = FirebaseRecyclerOptions.Builder<MessageModel>()
          .setLifecycleOwner(this)
          .setQuery(query,MessageModel::class.java)
          .build()
      query.keepSynced(true)

      firebaseRecyclerAdapter =
          object : FirebaseRecyclerAdapter<MessageModel, ViewHolder>(firebaseRecyclerOptions) {
              override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

                  var viewDataBinding: ViewDataBinding? = null

                  if (viewType == 0)
                      viewDataBinding = RightItemLayoutBinding.inflate(
                          LayoutInflater.from(parent.context),
                          parent,
                          false
                      )

                  if (viewType == 1)

                      viewDataBinding = LeftItemLayoutBinding.inflate(
                          LayoutInflater.from(parent.context),
                          parent,
                          false
                      )

                  return ViewHolder(viewDataBinding!!)

              }

              override fun onBindViewHolder(
                  holder: ViewHolder,
                  position: Int,
                  messageModel: MessageModel
              ) {
                  if (getItemViewType(position) == 0) {
                      holder.viewDataBinding.setVariable(BR.message, messageModel)
                      holder.viewDataBinding.setVariable(BR.messageImage, myImage)
                  }

                  if (getItemViewType(position) == 1) {

                      holder.viewDataBinding.setVariable(BR.message, messageModel)
                      holder.viewDataBinding.setVariable(BR.messageImage, hisImage)
                  }
              }

              override fun getItemViewType(position: Int): Int {

                  val messageModel = getItem(position)
                  return if (messageModel.senderId == myId)
                      0
                  else
                      1
              }
          }

      activityMessageBinding.messageRecyclerView.layoutManager = LinearLayoutManager(this)
      activityMessageBinding.messageRecyclerView.adapter = firebaseRecyclerAdapter
      firebaseRecyclerAdapter!!.startListening()

  }

  class ViewHolder(var viewDataBinding: ViewDataBinding) :
      RecyclerView.ViewHolder(viewDataBinding.root)

  override fun onPause() {
      super.onPause()
      if (firebaseRecyclerAdapter != null){
          firebaseRecyclerAdapter!!.stopListening()
      }
  }
*/


  fun userInfo() {
      val intent = Intent(this, UserInfoActivity::class.java)
      intent.putExtra("userId", hisId)
      startActivity(intent)
  }
/*
    private fun sendMessage(senderId:String , receiverId: String, message:String,time:String){
        val receiverRoom = receiverId + senderId
        val senderRoom = senderId + receiverId

        var reference = FirebaseDatabase.getInstance().getReference("Chat").child(senderRoom)
/*
        var hashMap: HashMap<String,String> = HashMap()
        hashMap.put("senderId",senderId)
        hashMap.put("receiverId",receiverId)
        hashMap.put("message",message)
        reference!!.child("Chat").push().setValue(hashMap)
 */
        val map = mapOf(
            "senderId" to senderId,
            "receiverId" to receiverId,
            "message" to message,
            "date" to time,
            "type" to ""
        )
        MessageModel(senderId,receiverId,message,time,"")
        reference.push().setValue(map).addOnSuccessListener {
            FirebaseDatabase.getInstance().getReference("Chat").child(receiverRoom).push().setValue(map)
        }
    }


    fun readMessage(senderId: String, receiverId: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat").child(senderId+receiverId)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)

                    if (chat!!.senderId.equals(senderId) && chat!!.receiverId.equals(receiverId) ||
                        chat!!.senderId.equals(receiverId) && chat!!.receiverId.equals(senderId)
                    ) {
                        chatList.add(chat)
                    }
                }

                val chatAdapter = ChatAdapter(this@MessageActivity, chatList)

                activityMessageBinding.messageRecyclerView.adapter = chatAdapter
            }
        })
    }


 */
    private fun sendMessage(senderId:String , receiverId: String, message:String){


    }

    fun readMessage(senderId: String, receiverId: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat").child(senderId+receiverId)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)

                    if (chat!!.senderId.equals(senderId) && chat!!.receiverId.equals(receiverId) ||
                        chat!!.senderId.equals(receiverId) && chat!!.receiverId.equals(senderId)
                    ) {
                        chatList.add(chat)
                    }
                }

                val chatAdapter = ChatAdapter(this@MessageActivity, chatList,activityMessageBinding.messageRecyclerView)

                activityMessageBinding.messageRecyclerView.adapter = chatAdapter
            }
        })
    }

    private fun addConversation(lastMessage:String,senderId:String , receiverId: String,receiverImage:String,receiverName:String){


    }

    private fun getUserData(userId: String?) {

        val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userModel = snapshot.getValue(UserModel::class.java)
                    activityMessageBinding.userModel = userModel
                    //Picasso.get().load(userModel!!.image).into(activityMessageBinding.imgProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun getToken(message: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(hisId!!)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val token = snapshot.child("token").value.toString()

                    val to = JSONObject()
                    val data = JSONObject()

                    data.put("hisId", myId.toString())
                    data.put("hisImage", myImage.toString())
                    data.put("hisName", myName.toString())
                    data.put("message", message.toString())
                    data.put("chatId", chatId.toString())

                    to.put("to", token)
                    to.put("data", data)
                    sendNotification(to)


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun sendNotification(to: JSONObject) {

        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            AppConstants.NOTIFICATION_URL,
            to,
            Response.Listener { response: JSONObject ->

                Log.d("TAG", "onResponse: $response")
            },
            Response.ErrorListener {

                Log.d("TAG", "onError: $it")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map: MutableMap<String, String> = HashMap()

                map["Authorization"] = "key=" + AppConstants.SERVER_KEY
                map["Content-type"] = "application/json"
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }

        val requestQueue = Volley.newRequestQueue(this)
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        requestQueue.add(request)

    }
}
