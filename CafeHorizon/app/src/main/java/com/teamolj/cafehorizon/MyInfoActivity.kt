package com.teamolj.cafehorizon

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.databinding.ActivityMyinfoBinding
import com.teamolj.cafehorizon.operation.InternetConnection
import java.util.concurrent.TimeUnit

class MyInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyinfoBinding

    private lateinit var auth: FirebaseAuth

    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private lateinit var newPhoneWithFormat: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyinfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        // 사용자 기본 설정 가져와서 세팅하기
        binding.itemUserID.setDescText(App.prefs.getString("userID", ""))
        binding.itemUserNickname.setDescText(App.prefs.getString("userNick", ""))
        binding.itemUserPhoneNum.setDescText(App.prefs.getString("userPhone", ""))

        binding.itemTermMarketing.setSwitch(App.prefs.getBoolean("userAgreeMarketing"))
        binding.itemTermPushMsg.setSwitch(App.prefs.getBoolean("userAgreePush"))

        // phone number
        binding.itemUserPhoneNum.setOnClickListener {
            binding.itemUserPhoneNum.toggleSlider()
            if (binding.itemUserPhoneNum.isSlideOpen()) {
                binding.layoutChangePhoneNum.visibility = View.VISIBLE
            }
            else {
                binding.layoutChangePhoneNum.visibility = View.GONE
            }
        }

        binding.editPhoneNum.doOnTextChanged { _, _, _, _ ->
            binding.textFieldPhoneNum.error = null
            binding.textFieldPhoneNum.isErrorEnabled = false
        }

        binding.editConfirmCode.doOnTextChanged { _, _, _, _ ->
            binding.textFieldConfirmCode.error = null
            binding.textFieldConfirmCode.isErrorEnabled = false
        }

        binding.btnSendConfirm.setOnClickListener {
            binding.btnSendConfirm.isClickable = false

            if (!InternetConnection.isInternetConnected(this)) {
                Toast.makeText(this, getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
                binding.btnSendConfirm.isClickable = true
            }
            else if (binding.editPhoneNum.text.toString().trim().isEmpty()) {
                binding.textFieldPhoneNum.error = getString(R.string.warning_empty_phonenum)
                binding.btnSendConfirm.isClickable = true
            }
            else {
                val newPhone = binding.editPhoneNum.text.toString().trim()
                newPhoneWithFormat = "${newPhone.substring(0,3)}-${newPhone.substring(3,7)}-${newPhone.substring(7)}"

                if (App.prefs.getString("userPhone", "") == newPhoneWithFormat) {
                    binding.textFieldPhoneNum.error = getString(R.string.warning_same_phonenum)
                    binding.btnSendConfirm.isClickable = true
                }
                else {
                    binding.textFieldConfirmCode.error = null
                    binding.textFieldConfirmCode.isErrorEnabled = false

                    binding.progressBar.visibility = View.VISIBLE

                    initCallbackObject()

                    val phoneNum = binding.editPhoneNum.text.toString().trim()

                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber("+82${phoneNum}")
                        .setTimeout(120L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(callbacks)
                        .build()

                    // 인증문자 보내기
                    PhoneAuthProvider.verifyPhoneNumber(options)
                }
            }
        }

        binding.btnCheckConfirm.setOnClickListener {
            binding.btnCheckConfirm.isClickable = false

            if (!InternetConnection.isInternetConnected(this)) {
                Toast.makeText(this, getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
                binding.btnCheckConfirm.isClickable = true
            }
            else if (binding.editConfirmCode.text.toString().trim().isEmpty()) {
                binding.textFieldConfirmCode.error = getString(R.string.warning_empty_confirms)
                binding.btnCheckConfirm.isClickable = true
            }
            else {
                binding.progressBar.visibility = View.VISIBLE

                val confirmCode = binding.editConfirmCode.text.toString().trim()
                val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, confirmCode)

                auth.currentUser!!.updatePhoneNumber(credential)
                    .addOnCompleteListener { task ->
                        binding.progressBar.visibility = View.GONE
                        binding.btnCheckConfirm.isClickable = true

                        if (task.isSuccessful) {
                            mCountDown.cancel()
                            binding.textFieldPhoneNum.isHelperTextEnabled = false

                            binding.itemUserPhoneNum.setDescText(newPhoneWithFormat)
                            App.prefs.setString("userPhone", newPhoneWithFormat)

                            binding.editPhoneNum.setText("")
                            binding.editConfirmCode.setText("")

                            if (binding.itemUserPhoneNum.isSlideOpen()) {
                                binding.itemUserPhoneNum.toggleSlider()
                                binding.layoutChangePhoneNum.visibility = View.GONE
                            }

                            Toast.makeText(this, R.string.toast_phone_changed, Toast.LENGTH_SHORT).show()

                        } else {
                            if (task.exception is FirebaseAuthInvalidCredentialsException) {
                                binding.textFieldConfirmCode.error = getString(R.string.warning_wrong_confirms)
                            }
                            else {
                                Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            }
        }

        // password
        binding.itemUserPwd.setOnClickListener {
            binding.itemUserPwd.toggleSlider()
            if (binding.itemUserPwd.isSlideOpen()) {
                binding.layoutChangePassword.visibility = View.VISIBLE
            }
            else {
                binding.layoutChangePassword.visibility = View.GONE
            }
        }

        binding.editCurrentPwd.doOnTextChanged { _, _, _, _ ->
            binding.textFieldCurrentPwd.error = null
            binding.textFieldCurrentPwd.isErrorEnabled = false
        }

        binding.editNewPwd.doOnTextChanged { _, _, _, _ ->
            binding.textFieldNewPwd.error = null
            binding.textFieldNewPwd.isErrorEnabled = false
        }

        binding.editConfirmPwd.doOnTextChanged { _, _, _, _ ->
            binding.textFieldConfirmPwd.error = null
            binding.textFieldConfirmPwd.isErrorEnabled = false
        }

        binding.btnChangePwd.setOnClickListener {
            binding.btnChangePwd.isClickable = false

            if (!InternetConnection.isInternetConnected(this)) {
                Toast.makeText(this, getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
                binding.btnChangePwd.isClickable = true
            }
            else if (binding.editCurrentPwd.text.toString().trim().isEmpty()) {
                binding.textFieldCurrentPwd.error = getString(R.string.warning_empty_userpwd)
                binding.btnChangePwd.isClickable = true
            }
            else if (binding.editNewPwd.text.toString().trim().isEmpty()) {
                binding.textFieldNewPwd.error = getString(R.string.warning_empty_userpwd)
                binding.btnChangePwd.isClickable = true
            }
            else if (binding.editNewPwd.text.toString().length < 8 || binding.editNewPwd.text.toString().length > 15) {
                binding.textFieldNewPwd.error = getString(R.string.warning_wrong_pwd_format)
                binding.btnChangePwd.isClickable = true
            }
            else if (binding.editConfirmPwd.text.toString().trim().isEmpty()) {
                binding.textFieldConfirmPwd.error = getString(R.string.warning_empty_pwd_confirm)
                binding.btnChangePwd.isClickable = true
            }
            else if (binding.editNewPwd.text.toString().trim() != binding.editConfirmPwd.text.toString().trim()) {
                binding.textFieldConfirmPwd.error = getString(R.string.warning_different_pwd)
                binding.btnChangePwd.isClickable = true
            }
            else {
                binding.progressBar.visibility = View.VISIBLE

                val email = App.prefs.getString("userID", "") + getString(R.string.email_domain)
                val newPwd = binding.editNewPwd.text.toString().trim()

                val user = auth.currentUser!!
                val credential = EmailAuthProvider.getCredential(email, binding.editCurrentPwd.text.toString().trim())

                user.reauthenticate(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {

                            user.updatePassword(newPwd)
                                .addOnCompleteListener { pwdTask ->
                                    binding.progressBar.visibility = View.GONE
                                    binding.btnChangePwd.isClickable = true

                                    if (pwdTask.isSuccessful) {
                                        binding.editCurrentPwd.setText("")
                                        binding.editNewPwd.setText("")
                                        binding.editConfirmPwd.setText("")

                                        if (binding.itemUserPwd.isSlideOpen()) {
                                            binding.itemUserPwd.toggleSlider()
                                            binding.layoutChangePassword.visibility = View.GONE
                                        }

                                        Toast.makeText(this, getString(R.string.toast_pwd_changed), Toast.LENGTH_SHORT).show()
                                    }
                                    else {
                                        Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                        else {
                            binding.textFieldCurrentPwd.error = getString(R.string.warning_wrong_pwd)

                            binding.progressBar.visibility = View.GONE
                            binding.btnChangePwd.isClickable = true
                        }
                    }
            }
        }

        binding.itemTermMarketing.setOnClickListener {
            if (binding.itemTermMarketing.isSwitchChecked()) {
                Toast.makeText(this, "마케팅 정보 수신 설정", Toast.LENGTH_SHORT).show()
                // 마케팅 정보 수신 설정
            }
            else {
                Toast.makeText(this, "마케팅 정보 수신 거부", Toast.LENGTH_SHORT).show()
                // 마케팅 정보 수신 거부
            }
        }

        binding.itemTermPushMsg.setOnClickListener {
            if (binding.itemTermPushMsg.isSwitchChecked()) {
                Toast.makeText(this, "푸시메시지 허용", Toast.LENGTH_SHORT).show()
                // 푸시메시지 허용
            }
            else {
                Toast.makeText(this, "푸시메시지 거부", Toast.LENGTH_SHORT).show()
                // 푸시메시지 거부
            }
        }

        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, UnsubscribeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initCallbackObject() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) { }

            override fun onVerificationFailed(e: FirebaseException) {
                binding.progressBar.visibility = View.GONE

                if (e is FirebaseAuthInvalidCredentialsException) {
                    binding.textFieldPhoneNum.error = getString(R.string.warning_wrong_format)
                } else {
                    Toast.makeText(applicationContext, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                }

                binding.btnSendConfirm.isClickable = true
                binding.btnSendConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.gray))
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                binding.progressBar.visibility = View.GONE
                mCountDown.start()

                binding.btnSendConfirm.isEnabled = false
                binding.btnSendConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.lightgray))
                binding.btnCheckConfirm.isEnabled = true

                storedVerificationId = verificationId
                resendToken = token
            }
        }
    }

    private val mCountDown: CountDownTimer = object : CountDownTimer(120000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val secs = millisUntilFinished / 1000
            binding.textFieldPhoneNum.helperText ="재발송 대기 ${secs / 60}:${String.format("%02d", secs % 60)}"
        }

        override fun onFinish() {
            binding.textFieldPhoneNum.isHelperTextEnabled = false

            binding.btnSendConfirm.isEnabled = true
            binding.btnSendConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.gray))
        }
    }
}