package com.teamolj.cafehorizon

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.databinding.FragmentFindIdBinding
import com.teamolj.cafehorizon.operation.InternetConnection
import java.util.concurrent.TimeUnit


class FindIdFragment : Fragment() {
    private lateinit var binding: FragmentFindIdBinding

    private lateinit var auth: FirebaseAuth

    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        auth = Firebase.auth

        binding = FragmentFindIdBinding.inflate(inflater, container, false)

        binding.editPhoneNum.doOnTextChanged { _, _, _, _ ->
            // remove error message
            binding.textFieldPhoneNum.error = null
            binding.textFieldPhoneNum.isErrorEnabled = false
        }

        binding.editConfirmCode.doOnTextChanged { _, _, _, _ ->
            binding.textFieldConfirmCode.error = null
            binding.textFieldConfirmCode.isErrorEnabled = false
        }

        binding.btnSendConfirm.setOnClickListener {
            binding.btnSendConfirm.isClickable = false

            if (!InternetConnection.isInternetConnected(requireContext())) {
                Toast.makeText(requireContext(), getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
                binding.btnSendConfirm.isClickable = true
            }
            else if (binding.editPhoneNum.text.toString().trim().isEmpty()) {
                binding.textFieldPhoneNum.error = getString(R.string.warning_empty_phonenum)
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
                    .setActivity(requireActivity())
                    .setCallbacks(callbacks)
                    .build()

                // 인증문자 보내기
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }

        binding.btnFindID.setOnClickListener {
            binding.btnFindID.isClickable = false
            closeKeyboard()

            if (!InternetConnection.isInternetConnected(requireContext())) {
                Toast.makeText(requireContext(), getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
                binding.btnFindID.isClickable = true
            }
            else if (binding.editConfirmCode.text.toString().trim().isEmpty()) {
                binding.textFieldConfirmCode.error = getString(R.string.warning_empty_confirms)
                binding.btnFindID.isClickable = true
            }
            else {
                binding.progressBar.visibility = View.VISIBLE

                val confirmCode = binding.editConfirmCode.text.toString().trim()
                val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, confirmCode)

                // 인증번호 일치여부 확인
                checkCodeValidation(credential)
            }
        }

        return binding.root
    }

    private fun initCallbackObject() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) { }

            override fun onVerificationFailed(e: FirebaseException) {
                binding.progressBar.visibility = View.GONE

                if (e is FirebaseAuthInvalidCredentialsException) {
                    binding.textFieldPhoneNum.error = getString(R.string.warning_wrong_format)
                } else if (e is FirebaseTooManyRequestsException) {
                    Toast.makeText(requireContext(), getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                }

                binding.btnSendConfirm.isClickable = true
                binding.btnSendConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray))
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                binding.progressBar.visibility = View.GONE
                mCountDown.start()

                binding.btnSendConfirm.isEnabled = false
                binding.btnSendConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.lightgray))
                binding.btnFindID.isEnabled = true

                storedVerificationId = verificationId
                resendToken = token
            }
        }
    }

    private fun checkCodeValidation(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {    // 인증번호가 일치하는 경우
                    mCountDown.cancel()
                    binding.progressBar.visibility = View.GONE

                    // 1. 아이디 확인... 아이디가 존재하는 경우, 다음 단계로
                    if (!auth.currentUser!!.email.isNullOrEmpty()) {
                        binding.layoutFindIdUserInput.visibility = View.GONE
                        binding.layoutFoundUserID.visibility = View.VISIBLE
                        binding.textFoundUserID.text = auth.currentUser!!.email!!.split("@")[0]
                        binding.btnFindID.text = getText(R.string.btn_go_login)

                        binding.btnFindID.setOnClickListener {
                            activity?.finish()
                        }

                        auth.signOut()
                    }
                    // 2. 존재하지 않는 경우, 다이얼로그 띄우고 입력값 모두 초기화시키기
                    else {
                        noUserInfoMsg()

                        auth.currentUser!!.delete()

                        binding.editPhoneNum.setText("")
                        binding.editConfirmCode.setText("")

                        mCountDown.onFinish()
                    }
                } else {
                    binding.progressBar.visibility = View.GONE

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        binding.textFieldConfirmCode.error = getString(R.string.warning_wrong_confirms)
                        binding.btnFindID.isClickable = true
                    }
                }
            }
    }

    private fun noUserInfoMsg() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setMessage(getString(R.string.dialog_no_user))
            .setNeutralButton("확인", null)
            .setCancelable(true)

        builder.create().show()
    }

    private val mCountDown: CountDownTimer = object : CountDownTimer(120000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val secs = millisUntilFinished / 1000
            binding.textFieldPhoneNum.helperText ="재발송 대기 ${secs / 60}:${String.format("%02d", secs % 60)}"
        }

        override fun onFinish() {
            binding.textFieldPhoneNum.isHelperTextEnabled = false

            binding.btnSendConfirm.isEnabled = true
            binding.btnSendConfirm.isClickable = true
            binding.btnSendConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray))

            binding.btnFindID.isClickable = true
            binding.btnFindID.isEnabled = false
        }
    }

    private fun closeKeyboard() {
        val view = requireActivity().currentFocus
        if (view != null) {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mCountDown.cancel()
    }
}