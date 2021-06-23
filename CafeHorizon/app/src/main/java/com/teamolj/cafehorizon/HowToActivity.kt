package com.teamolj.cafehorizon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.teamolj.cafehorizon.databinding.ActivityHowToBinding

class HowToActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHowToBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHowToBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        val applicationVersion = this.packageManager.getPackageInfo(this.packageName, 0).versionName
        binding.textHowToVersion.setText(("어플리케이션 버전 v$applicationVersion"))
    }

    fun onHowToClick(view: View) {
        val tag = view.tag.toString()
        val intent = Intent(this, HowToDetailActivity::class.java)
        intent.putExtra("category", tag)
        startActivity(intent)
    }
}