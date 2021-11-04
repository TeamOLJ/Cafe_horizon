package com.teamolj.cafehorizon.chat

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.teamolj.cafehorizon.R
import java.io.File
import java.io.IOException

class ImageFunc {
    companion object {
        const val PERMISSION_REQUEST_CODE: Int = 1000

        var mCurrentPhotoPath: String = ""

        fun getPickIntent(context: Context): Intent {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = MediaStore.Images.Media.CONTENT_TYPE

            val chooserIntent =
                Intent.createChooser(galleryIntent, context.getString(R.string.text_select_image))
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                arrayOf(captureWithUseCache(context)))

            return chooserIntent
        }

        fun showDialogToGetPermission(context: Context) {
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
        private fun captureWithUseCache(context: Context): Intent {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (cameraIntent.resolveActivity(context.packageManager) != null) {
                var photoFile: File? = null

                try {
                    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    val tempImage = File.createTempFile("Capture_", ".jpg", storageDir)

                    mCurrentPhotoPath = tempImage.absolutePath
                    photoFile = tempImage
                } catch (e: IOException) {
                    Log.w("ImageFunc", "파일 생성 에러! $e")
                }

                if (photoFile != null) {
                    val photoURI: Uri = FileProvider.getUriForFile(context,
                        context.packageName + ".fileprovider",
                        photoFile)

                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                }
            }
            return cameraIntent
        }
    }
}