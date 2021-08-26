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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    //Firebase Auth
    private lateinit var auth: FirebaseAuth
    private lateinit var userName: String

    //Firebase Database
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private var childEventListener: ChildEventListener? = null

    //Firebase Storage
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private lateinit var chatAdapter: ChatRecyclerAdapter
    private val messageList = mutableListOf<Message>()

    private var enable: Boolean = true;

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri: Uri? = result.data?.data ?: GetImageFunc.realUri
            val message = Message(userName, System.currentTimeMillis(), null, uri.toString(), false)

            storageReference.child(auth.uid.toString())
                .putFile(uri!!)
                .addOnSuccessListener { taskSnapshot -> {

                } }

            chatAdapter.addNewData(message)
            databaseReference.push().setValue(message)
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
        userName = auth.currentUser!!.displayName.toString()

        database =
            FirebaseDatabase.getInstance("https://cafehorizon-cd14d-default-rtdb.asia-southeast1.firebasedatabase.app/")
        databaseReference = database.reference.child(auth.currentUser!!.uid)
        attachDatabaseReadListener()

        storage = FirebaseStorage.getInstance()
        storageReference = storage.getReference().child("chat_photos")


        GetImageFunc.getInstance(this)

        // Initialize recycler Adapter
        chatAdapter = ChatRecyclerAdapter(userName)
        chatAdapter.messageList = messageList
        binding.recyclerViewChat.adapter = chatAdapter
        binding.recyclerViewChat.layoutManager = LinearLayoutManager(this)


        if (enable) {
            //영업시간 X
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
            // 영업시간 O
            binding.btnPhotoPicker.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    GetImageFunc.requirePermissions()
                } else {
                    // 버전이 낮은 경우 권한 확인하지 않음.
                    pickImageLauncher.launch(GetImageFunc.getPickIntent())
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
                    chatAdapter.notifyDataSetChanged()
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

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            GetImageFunc.PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty()) {
                    throw RuntimeException("Empty permission result")
                }
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    pickImageLauncher.launch(GetImageFunc.getPickIntent())
                } else {
                    // 권한 거부
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        showToast("권한이 거부되었습니다. 권한을 허용해야 사용할 수 있습니다.")
                    } else {
                        // +다신 물어보지 않기
                        GetImageFunc.showDialogToGetPermission()
                    }
                }
            }
        }
    }
}