package com.teamolj.cafehorizon.notice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityNoticeBinding
import java.util.*

class NoticeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoticeBinding

    private lateinit var noticeAdapter: NoticeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        val db = Firebase.firestore
        val userUid = Firebase.auth.currentUser!!.uid
        val docRef = db.collection("UserInformation").document(userUid).collection("Notices")

        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -30)
        val monthAgo: Date = cal.time

        docRef
            .whereGreaterThan("noticeTime", monthAgo)
            .orderBy("noticeTime", Query.Direction.DESCENDING)
            .get().addOnSuccessListener { snapshot ->
                val noticeList: MutableList<Notice> = mutableListOf()

                for (document in snapshot.documents) {
                    noticeList.add(Notice(document["noticeContext"].toString(),
                        (document["noticeTime"] as Timestamp).seconds * 1000))
                }

                val adapter = NoticeAdapter()
                adapter.noticeList = noticeList
                binding.recyclerViewNotice.adapter = adapter
                binding.recyclerViewNotice.layoutManager = LinearLayoutManager(this)

                binding.progressBar.visibility = View.GONE

            }.addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Log.w("firebase", "Error getting documents.", it)
                Toast.makeText(binding.root.context,
                    getString(R.string.toast_error_occurred),
                    Toast.LENGTH_SHORT).show()
            }
    }
}