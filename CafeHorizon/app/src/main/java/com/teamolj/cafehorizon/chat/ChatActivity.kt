package com.teamolj.cafehorizon.chat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private val PERMISSION_REQUEST_CODE: Int = 1000

    private var enable: Boolean = true;
    private lateinit var userName: String

    private lateinit var chatAdapter: ChatRecyclerAdapter
    private val messageList = mutableListOf<Message>()

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private var childEventListener: ChildEventListener? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode== RESULT_OK) {
                val uri = result.data!!.data

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        auth = FirebaseAuth.getInstance()
        database =
            FirebaseDatabase.getInstance("https://cafehorizon-cd14d-default-rtdb.asia-southeast1.firebasedatabase.app/")
        databaseReference = database.reference.child(auth.currentUser!!.uid)
        attachDatabaseReadListener()

        userName = auth.currentUser!!.displayName.toString()
        Log.d("TAG", "user nickname : $userName")

        // Initialize recycler Adapter
        chatAdapter = ChatRecyclerAdapter(userName)
        chatAdapter.messageList = messageList
        binding.recyclerViewChat.adapter = chatAdapter
        binding.recyclerViewChat.layoutManager = LinearLayoutManager(this)


        if (enable) {
            binding.editTextChat.keyListener = null
            binding.editTextChat.setText(R.string.text_chatting_not_enable)
            binding.editTextChat.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.editTextChat.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.gray
                )
            )
            binding.btnSendChat.isEnabled = false

        } else {
            binding.btnPhotoPicker.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissions()
                }
            }

            binding.editTextChat.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.btnSendChat.isEnabled = p0.toString().trim().isNotEmpty()
                }

                override fun afterTextChanged(p0: Editable?) {}
            })

            binding.btnSendChat.setOnClickListener {
                val message = Message(
                    userName,
                    System.currentTimeMillis(),
                    binding.editTextChat.text.toString(),
                    null,
                    false
                )
                databaseReference.push().setValue(message)

                binding.editTextChat.setText("")
            }
        }
    }

    private fun attachDatabaseReadListener() {
        if (childEventListener == null) {
            childEventListener = object : ChildEventListener {
                override fun onChildAdded(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    val newMessage: Message = snapshot.getValue(Message::class.java)!!
                    chatAdapter.addNewData(newMessage)
//                    chatAdapter.notifyDataSetChanged()
                }

                override fun onChildChanged(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                }

                override fun onCancelled(error: DatabaseError) {}

            }
            databaseReference.addChildEventListener(childEventListener!!)
        }
    }

    private fun detachDatabaseReadListener() {
        if (childEventListener != null) {
            databaseReference.removeEventListener(childEventListener!!)
            childEventListener = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {

        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty()) {
                    throw RuntimeException("Empty permission result")
                }
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    // 권한 허락됨
                } else {
                    // 권한 허락되지 않음
                    if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // 권한 거절. 대신 다시 물어봐도 됨.
                        Toast.makeText(this, "권한이 거절되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        //다신 물어보지 않기
//                        showDialogToGetPermission()
                    }
                }
            }
        }
    }
}