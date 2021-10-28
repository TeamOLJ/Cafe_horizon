package com.teamolj.cafehorizon.chat

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityChatBinding
import java.io.File
import java.util.*

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

    private var isEnable: Boolean = true;

    private var newestItemTime: Long = 0

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri: Uri = result.data?.data ?: File(ImageFunc.mCurrentPhotoPath).toUri()

            cropImageLauncher.launch(
                options(uri = uri) {
                    setGuidelines(CropImageView.Guidelines.ON)
                    setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                }
            )
        }

    }

    private val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uriContent = result.uriContent

            storageReference.child("${auth.uid.toString()}/${uriContent?.lastPathSegment}")
                .putFile(uriContent!!)
                .addOnSuccessListener { taskSnapshot ->
                    val downloadUri: Task<Uri> = taskSnapshot.storage.downloadUrl

                    downloadUri.addOnSuccessListener { uri ->
                        val message = Message(userName,
                            System.currentTimeMillis(),
                            null,
                            uri.toString(),
                            false)
                        databaseReference.push().setValue(message)
                    }
                }
                .addOnFailureListener { e ->
                    showToast("image upload failed.\n${e}")
                }
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
        userName = auth.currentUser!!.uid

        database =
            FirebaseDatabase.getInstance("https://cafehorizon-cd14d-default-rtdb.asia-southeast1.firebasedatabase.app/")
        databaseReference = database.reference.child(auth.currentUser!!.uid)
        attachDatabaseReadListener()

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference.child("ChatImages")


        // Initialize recycler Adapter
        chatAdapter = ChatRecyclerAdapter(userName)
        binding.recyclerViewChat.adapter = chatAdapter
        binding.recyclerViewChat.layoutManager = LinearLayoutManager(this)

        binding.btnSendChat.isEnabled = false

        if (!isEnable) {
            //영업시간 X
            binding.btnPhotoPicker.isEnabled = false
            binding.editTextChat.keyListener = null
            binding.editTextChat.setText(R.string.text_chatting_not_enable)
            binding.editTextChat.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.editTextChat.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.gray
                )
            )

        } else {
            // 영업시간 O
            binding.btnPhotoPicker.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(this, arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), ImageFunc.PERMISSION_REQUEST_CODE)

                } else {
                    // 버전이 낮은 경우 권한 확인하지 않음.
                    pickImageLauncher.launch(ImageFunc.getPickIntent(this))
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

        binding.recyclerViewChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val firstVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()

                if (firstVisibleItemPosition == 5) {
                    val query =
                        databaseReference.orderByChild("time")
                            .endBefore(chatAdapter.getOldestMessageTime().toDouble())
                            .limitToLast(15)

                    query.get().addOnFailureListener {
                        Log.w("firebase", "Error getting chat data.", it)
                        Toast.makeText(applicationContext,
                            getString(R.string.toast_error_occurred),
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        detachDatabaseReadListener()
        super.onDestroy()
    }

    private fun attachDatabaseReadListener() {
        if (childEventListener == null) {
            childEventListener = object : ChildEventListener {
                override fun onChildAdded(
                    snapshot: DataSnapshot,
                    previousChildName: String?,
                ) {
                    val newMessage: Message = snapshot.getValue(Message::class.java)!!
                    if (newMessage.user != userName && !newMessage.readState) {
                        newMessage.readState = true;
                        databaseReference.updateChildren(hashMapOf<String, Any>("/${snapshot.key}/readState" to true))
                    }

                    if (newestItemTime < newMessage.time) {
                        chatAdapter.addAfterMessage(newMessage)
                        newestItemTime = newMessage.time
                        binding.recyclerViewChat.scrollToPosition(chatAdapter.itemCount - 1)
                    } else {
                        chatAdapter.addBeforeMessage(newMessage)
                    }
                }

                override fun onChildChanged(
                    snapshot: DataSnapshot,
                    previousChildName: String?,
                ) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(
                    snapshot: DataSnapshot,
                    previousChildName: String?,
                ) {
                }

                override fun onCancelled(error: DatabaseError) {}

            }
            databaseReference.limitToLast(15).addChildEventListener(childEventListener!!)
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
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            ImageFunc.PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty()) {
                }
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    pickImageLauncher.launch(ImageFunc.getPickIntent(this))
                } else {
                    // 권한 거부
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        showToast(getString(R.string.text_permission_request))
                    } else {
                        // +다신 물어보지 않기
                        ImageFunc.showDialogToGetPermission(this)
                    }
                }
            }
        }
    }
}