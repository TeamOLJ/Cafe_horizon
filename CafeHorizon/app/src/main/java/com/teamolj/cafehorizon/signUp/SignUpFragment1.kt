package com.teamolj.cafehorizon.signUp

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.FragmentSignup1Binding
import com.teamolj.cafehorizon.operation.InternetConnection
import java.util.concurrent.TimeUnit

class SignUpFragment1 : Fragment() {
    private lateinit var binding: FragmentSignup1Binding

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

        binding = FragmentSignup1Binding.inflate(inflater, container, false)

        binding.checkAllTerm.setOnClickListener {
            if (binding.checkAllTerm.isChecked) {
                binding.checkTermService.isChecked = true
                binding.checkTermPersonal.isChecked = true
                binding.checkTermMarketing.isChecked = true
                binding.checkPushMsg.isChecked = true

                binding.tvConfirmUser.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                binding.layoutConfirmUser.visibility = View.VISIBLE
            }
            else {
                binding.checkTermService.isChecked = false
                binding.checkTermPersonal.isChecked = false
                binding.checkTermMarketing.isChecked = false
                binding.checkPushMsg.isChecked = false

                binding.tvConfirmUser.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.semigray))
                binding.layoutConfirmUser.visibility = View.GONE
            }
        }

        binding.checkTermService.setOnClickListener {
            binding.checkAllTerm.isChecked =
                binding.checkTermService.isChecked
                    && binding.checkTermPersonal.isChecked
                    && binding.checkTermMarketing.isChecked
                    && binding.checkPushMsg.isChecked

            if (binding.checkTermService.isChecked && binding.checkTermPersonal.isChecked) {
                binding.tvConfirmUser.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                binding.layoutConfirmUser.visibility = View.VISIBLE
            }
            else {
                binding.tvConfirmUser.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.semigray))
                binding.layoutConfirmUser.visibility = View.GONE
            }
        }

        binding.checkTermPersonal.setOnClickListener {
            binding.checkAllTerm.isChecked =
                binding.checkTermService.isChecked
                        && binding.checkTermPersonal.isChecked
                        && binding.checkTermMarketing.isChecked
                        && binding.checkPushMsg.isChecked

            if (binding.checkTermService.isChecked && binding.checkTermPersonal.isChecked) {
                binding.tvConfirmUser.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                binding.layoutConfirmUser.visibility = View.VISIBLE
            }
            else {
                binding.tvConfirmUser.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.semigray))
                binding.layoutConfirmUser.visibility = View.GONE
            }
        }

        binding.checkTermMarketing.setOnClickListener {
            binding.checkAllTerm.isChecked =
                binding.checkTermService.isChecked
                        && binding.checkTermPersonal.isChecked
                        && binding.checkTermMarketing.isChecked
                        && binding.checkPushMsg.isChecked
        }

        binding.checkPushMsg.setOnClickListener {
            binding.checkAllTerm.isChecked =
                binding.checkTermService.isChecked
                        && binding.checkTermPersonal.isChecked
                        && binding.checkTermMarketing.isChecked
                        && binding.checkPushMsg.isChecked
        }

        binding.btnTermService.setOnClickListener {
            if (binding.btnTermService.tag == "not_open") {
                binding.btnTermService.tag = "open"
                binding.btnTermService.setImageResource(R.drawable.ic_slideup)
                binding.textTermService.visibility = View.VISIBLE
            }
            else {
                binding.btnTermService.tag = "not_open"
                binding.btnTermService.setImageResource(R.drawable.ic_slidedown)
                binding.textTermService.visibility = View.GONE
            }
        }

        binding.btnTermPersonal.setOnClickListener {
            if (binding.btnTermPersonal.tag == "not_open") {
                binding.btnTermPersonal.tag = "open"
                binding.btnTermPersonal.setImageResource(R.drawable.ic_slideup)
                binding.textTermPersonal.visibility = View.VISIBLE
            }
            else {
                binding.btnTermPersonal.tag = "not_open"
                binding.btnTermPersonal.setImageResource(R.drawable.ic_slidedown)
                binding.textTermPersonal.visibility = View.GONE
            }
        }

        binding.btnTermMarketing.setOnClickListener {
            if (binding.btnTermMarketing.tag == "not_open") {
                binding.btnTermMarketing.tag = "open"
                binding.btnTermMarketing.setImageResource(R.drawable.ic_slideup)
                binding.textTermMarketing.visibility = View.VISIBLE
            }
            else {
                binding.btnTermMarketing.tag = "not_open"
                binding.btnTermMarketing.setImageResource(R.drawable.ic_slidedown)
                binding.textTermMarketing.visibility = View.GONE
            }
        }

        binding.btnPushMsg.setOnClickListener {
            if (binding.btnPushMsg.tag == "not_open") {
                binding.btnPushMsg.tag = "open"
                binding.btnPushMsg.setImageResource(R.drawable.ic_slideup)
                binding.textPushMsg.visibility = View.VISIBLE
            }
            else {
                binding.btnPushMsg.tag = "not_open"
                binding.btnPushMsg.setImageResource(R.drawable.ic_slidedown)
                binding.textPushMsg.visibility = View.GONE
            }
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

        binding.btnCheckConfirm.setOnClickListener {
            binding.btnCheckConfirm.isClickable = false
            closeKeyboard()

            if (!InternetConnection.isInternetConnected(requireContext())) {
                Toast.makeText(requireContext(), getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
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
                binding.btnCheckConfirm.isEnabled = true

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

                    // 이미 가입되어 있는 전화번호인지 먼저 확인
                    if (!auth.currentUser!!.email.isNullOrEmpty()) {
                        userExistMsg()
                    }
                    else
                        // 아닐 경우, 다음 프래그먼트로 전환
                        (activity as SignUpActivity).switchFragment()

                } else {
                    binding.progressBar.visibility = View.GONE

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        binding.textFieldConfirmCode.error = getString(R.string.warning_wrong_confirms)
                        binding.btnCheckConfirm.isClickable = true
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
            binding.btnSendConfirm.isClickable = true
            binding.btnSendConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray))

            binding.btnCheckConfirm.isClickable = true
            binding.btnCheckConfirm.isEnabled = false
        }
    }

    private fun userExistMsg() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setMessage(getString(R.string.dialog_user_exist))
            .setPositiveButton(getString(R.string.btn_go_login)) { _, _ ->
                auth.signOut()
                (activity as SignUpActivity).closeActivity()
            }
            .setNegativeButton(getString(R.string.btn_cancel)) { _, _ ->
                auth.signOut()
                binding.editPhoneNum.setText("")
                binding.editConfirmCode.setText("")

                binding.textFieldPhoneNum.isHelperTextEnabled = false

                binding.btnSendConfirm.isEnabled = true
                binding.btnSendConfirm.isClickable = true
                binding.btnSendConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray))

                binding.btnCheckConfirm.isClickable = true
                binding.btnCheckConfirm.isEnabled = false
            }
            .setCancelable(true)

        builder.create().show()
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