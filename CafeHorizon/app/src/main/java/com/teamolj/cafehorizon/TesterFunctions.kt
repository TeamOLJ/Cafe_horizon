package com.teamolj.cafehorizon

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.databinding.TesterFunctionsBinding
import java.util.concurrent.TimeUnit

class TesterFunctions : AppCompatActivity() {
    private val TAG = "TestFunc"

    private lateinit var binding: TesterFunctionsBinding

    private var onVerification = false
    private var phoneVerified = false

    private lateinit var phoneNum: String
    private lateinit var confirmCode: String

    private lateinit var auth: FirebaseAuth

    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TesterFunctionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            // 전화번호 인증을 마친 상태에서 회원가입을 포기할 경우, 회원정보 삭제(탈퇴)
            if (phoneVerified) {
                // 회원가입을 취소하겠는지를 먼저 묻고...
                    
                auth.currentUser!!.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "User account deleted.")
                            // finish()
                        }
                    }
            }
        }

        binding.editConfirmCode.doOnTextChanged { _, _, _, _ ->
            binding.textFieldConfirmCode.error = null
            binding.textFieldConfirmCode.isErrorEnabled = false
        }

        binding.btnSendConfirm.setOnClickListener {
            phoneNum = binding.editPhoneNum.text.toString().trim()

            binding.textFieldConfirmCode.error = null
            binding.textFieldConfirmCode.isErrorEnabled = false

            binding.progressBar.visibility = View.VISIBLE

            callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d(TAG, "onVerificationCompleted:$credential")
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.w(TAG, "onVerificationFailed", e)

                    if (e is FirebaseAuthInvalidCredentialsException) {
                        binding.textFieldConfirmCode.error = "잘못된 형식입니다. 다시 확인하세요."
                    } else if (e is FirebaseTooManyRequestsException) {
                        Toast.makeText(baseContext, "오류가 발생했습니다. 나중에 다시 시도하세요.", Toast.LENGTH_SHORT).show()
                    }

                    binding.progressBar.visibility = View.GONE

                    binding.btnSendConfirm.isEnabled = true
                    binding.btnSendConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.gray))

                    binding.btnCheckConfirm.isEnabled = false
                    binding.btnCheckConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.lightgray))
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    binding.progressBar.visibility = View.GONE
                    mCountDown.start()

                    binding.btnSendConfirm.isEnabled = false
                    binding.btnSendConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.lightgray))

                    binding.btnCheckConfirm.isEnabled = true
                    binding.btnCheckConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.gray))

                    onVerification = true
                    storedVerificationId = verificationId
                    resendToken = token
                }
            }

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+82${phoneNum}")
                .setTimeout(120L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }

        binding.btnCheckConfirm.setOnClickListener {
            confirmCode = binding.editConfirmCode.text.toString().trim()

            val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, confirmCode)
            checkCodeValidation(credential)
        }

        binding.btnSignUp.setOnClickListener {
            val editEmail = binding.editEmail.text.toString().trim()
            val editPwd = binding.editPwd.text.toString().trim()

            val credential = EmailAuthProvider.getCredential(editEmail, editPwd)

            auth.currentUser!!.linkWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "linkWithCredential:success")
                        Toast.makeText(baseContext, "회원가입 성공", Toast.LENGTH_SHORT).show()

                        Log.d(TAG, "finalSignUp: ${task.result?.user!!.uid}")
                        Log.d(TAG, "finalSignUp: ${task.result?.user!!.email}")

                    } else {
                        Log.w(TAG, "linkWithCredential:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

    private fun checkCodeValidation(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    mCountDown.cancel()
                    mCountDown.onFinish()

                    Log.d(TAG, "signInWithCredential(confirmCode):success")

                    onVerification = false
                    phoneVerified = true

                    binding.textFieldPhoneNum.isHelperTextEnabled = true
                    binding.textFieldPhoneNum.helperText = "인증되었습니다!"
                    binding.textFieldPhoneNum.setHelperTextColor(ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.textGreen)))

                    binding.btnSignUp.isEnabled = true
                    binding.btnSignUp.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.gray))

                    binding.btnCheckConfirm.isEnabled = false
                    binding.btnCheckConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.lightgray))

                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        binding.textFieldConfirmCode.error = "잘못된 인증번호입니다."
                    }
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

            binding.btnCheckConfirm.isEnabled = false
            binding.btnCheckConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.lightgray))

            onVerification = false
        }
    }
}