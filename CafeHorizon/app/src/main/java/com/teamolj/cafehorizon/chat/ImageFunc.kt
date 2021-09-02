package com.teamolj.cafehorizon.chat

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.ComponentActivity
import androidx.core.content.FileProvider
import com.teamolj.cafehorizon.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat

class ImageFunc {
    companion object {
        private lateinit var context: Context
        const val PERMISSION_REQUEST_CODE: Int = 1000

        var realUri: Uri? = null
        var mCurrentPhotoPath:String=""

        fun getInstance(context: Context) {
            this.context = context
        }

        fun getPickIntent(): Intent {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = MediaStore.Images.Media.CONTENT_TYPE

            val chooserIntent =
                Intent.createChooser(galleryIntent, context.getString(R.string.text_select_image))
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(captureWithUseCache()))

            return chooserIntent
        }

        fun requirePermissions() {
            val permissions = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

            ActivityCompat.requestPermissions(context as Activity, permissions, PERMISSION_REQUEST_CODE)
        }


        fun showDialogToGetPermission() {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(context.getString(R.string.text_permission_request))
                .setPositiveButton(context.getString(R.string.btn_positive_permission_request)) { _, _ ->
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
                .setNegativeButton(context.getString(R.string.btn_negative_permission_request)) { _, _ -> }

            builder.create().show()
        }

        // 이미지 캡쳐 후 저장하지 않음
        private fun captureWithUseCache():Intent{
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(cameraIntent.resolveActivity(context.packageManager)!=null) {
                var photoFile: File? = null

                try {
                    var storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    val tempImage = File.createTempFile("Capture_", ".jpg", storageDir)

                    mCurrentPhotoPath = tempImage.absolutePath
                    photoFile = tempImage
                } catch (e:IOException) {
                    Log.w("TAG", "파일 생성 에러! ${e}")
                }

                if(photoFile!=null) {
                    var photoURI:Uri = FileProvider.getUriForFile(context,
                    context.packageName+".fileprovider",
                    photoFile)

                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                }
            }
            return cameraIntent
        }

        // 이미지 캡쳐 후 파일을 저장함
        private fun captureWithSaveImage():Intent {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val uri = createImageUri(newFileName(), "image/jpg");
            realUri = uri
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, realUri)
            return cameraIntent
        }

        private fun createImageUri(filename: String, mimeType: String): Uri? {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            }
            return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        }

        private fun newFileName(): String = "${SimpleDateFormat("yyyyMMdd_HHmmss").format(System.currentTimeMillis())}.jpg"

    }

}