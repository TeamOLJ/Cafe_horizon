package com.teamolj.cafehorizon

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.databinding.ActivityLoginBinding
import com.teamolj.cafehorizon.operation.InternetConnection
import com.teamolj.cafehorizon.signUp.SignUpActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private var doubleBackPressed: Boolean = false
    private lateinit var closeToast: Toast

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        val currentUser = auth.currentUser
        if(currentUser != null){
            // 비정상 경로 접근 차단
            if (App.prefs.getString("userNick", "") != "") {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            else
                auth.signOut()
        }

        binding.txtUserID.doOnTextChanged { _, _, _, _ ->
            // remove error message
            binding.textFieldUserID.error = null
            binding.textFieldUserID.isErrorEnabled = false
        }

        binding.txtUserPwd.doOnTextChanged { _, _, _, _ ->
            binding.textFieldUserPwd.error = null
            binding.textFieldUserPwd.isErrorEnabled = false
        }

        binding.btnLogin.setOnClickListener {
            binding.btnLogin.isClickable = false
            closeKeyboard()

            if (!InternetConnection.isInternetConnected(this)) {
                Toast.makeText(this, getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
                binding.btnLogin.isClickable = true
            }
            else if (binding.txtUserID.text.toString().trim().isEmpty()) {
                binding.textFieldUserID.error = getString(R.string.warning_empty_userid)
                binding.btnLogin.isClickable = true
            }
            else if (binding.txtUserPwd.text.toString().trim().isEmpty()) {
                binding.textFieldUserPwd.error = getString(R.string.warning_empty_userpwd)
                binding.btnLogin.isClickable = true
            }
            else {
                // 로그인 시도
                val userId = binding.txtUserID.text.toString().trim()
                val email = userId + getString(R.string.email_domain)
                val password = binding.txtUserPwd.text.toString().trim()

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userUID = auth.currentUser!!.uid
                            val userPhone = auth.currentUser!!.phoneNumber

                            val docRef = db.collection("UserInformation").document(userUID)
                            docRef.get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        App.prefs.setString("userID", userId)
                                        App.prefs.setString("userNick", document.data?.get("userNick").toString())
                                        App.prefs.setString("userBarcode", document.data?.get("userBarcode").toString())
                                        App.prefs.setString("userBday", document.data?.get("userBday").toString())

                                        App.prefs.setString("userPhone", "0${userPhone!!.substring(3,5)}-${userPhone.substring(5,9)}-${userPhone.substring(9)}")

                                        App.prefs.setBoolean("userAgreeMarketing", document.getBoolean("agreeMarketing")!!)
                                        App.prefs.setBoolean("userAgreePush", document.getBoolean("agreePush")!!)

                                        val intent = Intent(this, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                    } else {
                                        Firebase.auth.signOut()
                                        Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                                        binding.btnLogin.isClickable = true
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Firebase.auth.signOut()
                                    Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                                    binding.btnLogin.isClickable = true
                                }
                        } else {
                            Toast.makeText(this, getString(R.string.toast_failed_verification), Toast.LENGTH_SHORT).show()
                            binding.btnLogin.isClickable = true
                        }
                    }
            }
        }

        binding.btnFindUserInfo.setOnClickListener {
            val intent = Intent(this, FindInfoActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnKakaoLogin.setOnClickListener(loginWithKakao)
        binding.btnNaverLogin.setOnClickListener(loginWithNaver)
        binding.btnFacebookLogin.setOnClickListener(loginWithFacebook)
    }

    // 각 로그인 성공 시 sharedPreferences에 로그인 타입 저장 필요: 추후 로그아웃 처리를 위함
    private val loginWithKakao = View.OnClickListener {
        // TODO
    }

    private val loginWithNaver = View.OnClickListener {
        // TODO
    }

    private val loginWithFacebook = View.OnClickListener {
        // TODO
    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }
    }

    // BackPress twice to close the app
    override fun onBackPressed() {
        if (doubleBackPressed) {
            closeToast.cancel()
            super.onBackPressed()
            return
        }
        this.doubleBackPressed = true
        closeToast = Toast.makeText(this, getString(R.string.toast_backpress_close), Toast.LENGTH_SHORT)
        closeToast.show()
        Handler(Looper.getMainLooper()).postDelayed({ doubleBackPressed = false }, 2000)
    }
}