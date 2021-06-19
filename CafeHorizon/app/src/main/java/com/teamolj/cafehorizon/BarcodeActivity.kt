package com.teamolj.cafehorizon

import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer
import com.teamolj.cafehorizon.databinding.ActivityBarcodeBinding

class BarcodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBarcodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.closeBarcode -> {
                    finish()
                    true
                }
                else -> false
            }
        }

        // SharedPreferences에서 사용자 닉네임 및 바코드 번호 가져오기
        binding.textUserNickname.text = App.prefs.getString("userNick", "")
        val userBarcode = App.prefs.getString("userBarcode", "")

        // 바코드 세팅
        displayUserBarcode(userBarcode)
    }

    private fun displayUserBarcode(userBarcodeValue: String) {
        // get device dimensions
        val displayMetrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display?.getRealMetrics(displayMetrics)
        }
        else {
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        }
 
        val width = displayMetrics.widthPixels - resources.getDimensionPixelSize(R.dimen.barcode_margin)*2
        val height = resources.getDimensionPixelSize(R.dimen.barcode_height)

        binding.imageBarcode.setImageBitmap(
            createBarcodeBitmap(
                barcodeValue = userBarcodeValue,
                barcodeColor = ContextCompat.getColor(this, R.color.black),
                backgroundColor = ContextCompat.getColor(this, android.R.color.transparent),
                widthPixels = width,
                heightPixels = height
            )
        )
        
        binding.textBarcodeNum.text = userBarcodeValue
    }

    private fun createBarcodeBitmap(
        barcodeValue: String,
        @ColorInt barcodeColor: Int,
        @ColorInt backgroundColor: Int,
        widthPixels: Int,
        heightPixels: Int
    ): Bitmap {
        val bitMatrix = Code128Writer().encode(barcodeValue, BarcodeFormat.CODE_128, widthPixels, heightPixels)

        val pixels = IntArray(bitMatrix.width * bitMatrix.height)
        for (y in 0 until bitMatrix.height) {
            val offset = y * bitMatrix.width
            for (x in 0 until bitMatrix.width) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) barcodeColor else backgroundColor
            }
        }

        val bitmap = Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.ARGB_8888)

        bitmap.setPixels(pixels, 0, bitMatrix.width, 0, 0, bitMatrix.width, bitMatrix.height)

        return bitmap
    }
}