package com.teamolj.cafehorizon.chat

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.chat.ImageFunc.Companion.PERMISSION_REQUEST_CODE
import com.teamolj.cafehorizon.databinding.ActivityPhotoDetailBinding
import java.text.SimpleDateFormat

class PhotoDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photoUrl = intent.getStringExtra("photoUrl")!!

        val topAppBar = binding.toolbar
        topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_photo_download -> {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    ) {
                        // 다운로드를 위해 WRITE_EXTERNAL_STORAGE 권한 얻기
                        ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            PERMISSION_REQUEST_CODE)

                    } else {
                        // 파이어베이스로부터 Byte Array 형태로 이미지 불러오기
                        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(photoUrl)
                        storageRef.getBytes(10 * 1024 * 1024).addOnSuccessListener { byteArray ->

                            //MediaStore에 저장할 uri 생성
                            val values = ContentValues().apply {
                                put(MediaStore.Images.Media.DISPLAY_NAME, "CafeHorizon_${
                                    SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())
                                }.png")
                                put(MediaStore.Images.Media.MIME_TYPE, "image/*")

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    put(MediaStore.Images.Media.IS_PENDING,
                                        1)  //파일 업데이트 전까지 외부 접근 제한
                                }
                            }

                            val uri =
                                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    values)!!

                            val stream = contentResolver.openOutputStream(uri)!!
                            stream.write(byteArray)
                            stream.flush()
                            stream.close()

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                values.clear()
                                values.put(MediaStore.Images.Media.IS_PENDING,
                                    0)   //업데이트 후 외부의 파일 접근 허용
                                contentResolver.update(uri, values, null, null)
                            }

                            Toast.makeText(this, "이미지가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                            .addOnFailureListener { exception ->
                                Log.w("firebase", "Error getting documents.", exception)
                                Toast.makeText(binding.root.context,
                                    getString(R.string.toast_error_occurred),
                                    Toast.LENGTH_SHORT).show()
                            }
                    }

                    true
                }
                else -> false
            }
        }

        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        Glide.with(this)
            .load(photoUrl)
            .into(binding.photoDetail)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_image_download, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.toolbar.menu.performIdentifierAction(R.id.action_photo_download, 0)
            } else {
                Toast.makeText(this,
                    getString(R.string.text_permission_request),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}