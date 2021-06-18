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
import com.teamolj.cafehorizon.databinding.FragmentFindPwdBinding
import com.teamolj.cafehorizon.operation.InternetConnection
import java.util.concurrent.TimeUnit

class FindPwdFragment : Fragment() {
    private lateinit var binding: FragmentFindPwdBinding

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

        binding = FragmentFindPwdBinding.inflate(inflater, container, false)

        binding.editUserID.doOnTextChanged { _, _, _, _ ->
            // remove error message
            binding.textFieldUserID.error = null
            binding.textFieldUserID.isErrorEnabled = false
        }

        binding.editPhoneNum.doOnTextChanged { _, _, _, _ ->
            binding.textFieldPhoneNum.error = null
            binding.textFieldPhoneNum.isErrorEnabled = false
        }

        binding.editConfirmCode.doOnTextChanged { _, _, _, _ ->
            binding.textFieldConfirmCode.error = null
            binding.textFieldConfirmCode.isErrorEnabled = false
        }

        binding.editNewPwd.doOnTextChanged { _, _, _, _ ->
            binding.textFieldNewPwd.error = null
            binding.textFieldNewPwd.isErrorEnabled = false
        }

        binding.editConfirmPwd.doOnTextChanged { _, _, _, _ ->
            binding.textFieldConfirmPwd.error = null
            binding.textFieldConfirmPwd.isErrorEnabled = false
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

                binding.progressBar1.visibility = View.VISIBLE

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

        binding.btnFindPwd.setOnClickListener {
            binding.btnFindPwd.isClickable = false
            closeKeyboard()

            if (!InternetConnection.isInternetConnected(requireContext())) {
                Toast.makeText(requireContext(), getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
                binding.btnFindPwd.isClickable = true
            }
            else if (binding.editUserID.text.toString().trim().isEmpty()) {
                binding.textFieldUserID.error = getString(R.string.warning_empty_userid)
                binding.btnFindPwd.isClickable = true
            }
            else if (binding.editConfirmCode.text.toString().trim().isEmpty()) {
                binding.textFieldConfirmCode.error = getString(R.string.warning_empty_confirms)
                binding.btnFindPwd.isClickable = true
            }
            else {binding.progressBar1.visibility = View.VISIBLE

                val confirmCode = binding.editConfirmCode.text.toString().trim()
                val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, confirmCode)

                // 인증번호 일치여부 확인
                checkCodeValidation(credential, binding.editUserID.text.toString().trim())
            }
        }

        return binding.root
    }

    private val changePwdListener = View.OnClickListener {
        binding.btnFindPwd.isClickable = false
        closeKeyboard()

        if (!InternetConnection.isInternetConnected(requireContext())) {
            Toast.makeText(requireContext(), getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
            binding.btnFindPwd.isClickable = true
        }
        else if (binding.editNewPwd.text.toString().trim().isEmpty()) {
            binding.textFieldNewPwd.error = getString(R.string.warning_empty_userpwd)
            binding.btnFindPwd.isClickable = true
        }
        else if (binding.editNewPwd.text.toString().length < 8 || binding.editNewPwd.text.toString().length > 15) {
            binding.textFieldNewPwd.error = getString(R.string.warning_wrong_pwd_format)
            binding.btnFindPwd.isClickable = true
        }
        else if (binding.editConfirmPwd.text.toString().trim().isEmpty()) {
            binding.textFieldConfirmPwd.error = getString(R.string.warning_empty_pwd_confirm)
            binding.btnFindPwd.isClickable = true
        }
        else if (binding.editNewPwd.text.toString().trim() != binding.editConfirmPwd.text.toString().trim()) {
            binding.textFieldConfirmPwd.error = getString(R.string.warning_different_pwd)
            binding.btnFindPwd.isClickable = true
        }
        else {
            // 비밀번호 변경 루틴 진행
            binding.progressBar2.visibility = View.VISIBLE

            auth.currentUser!!.updatePassword(binding.editNewPwd.text.toString().trim())
                .addOnCompleteListener { task ->
                    binding.progressBar2.visibility = View.GONE
                    if (task.isSuccessful) {
                        auth.signOut()
                        Toast.makeText(requireContext(), getString(R.string.toast_pwd_changed_relogin), Toast.LENGTH_SHORT).show()
                        activity?.finish()
                    }
                    else {
                        Toast.makeText(requireContext(), getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                        binding.btnFindPwd.isClickable = true
                    }
                }
        }
    }

    private fun initCallbackObject() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) { }

            override fun onVerificationFailed(e: FirebaseException) {
                binding.progressBar1.visibility = View.GONE

                if (e is FirebaseAuthInvalidCredentialsException) {
                    binding.textFieldPhoneNum.error = getString(R.string.warning_wrong_format)
                } else if (e is FirebaseTooManyRequestsException) {
                    Toast.makeText(requireContext(), getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                }

                binding.btnSendConfirm.isClickable = true
                binding.btnSendConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray))
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                binding.progressBar1.visibility = View.GONE
                mCountDown.start()

                binding.btnSendConfirm.isEnabled = false
                binding.btnSendConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.lightgray))
                binding.btnFindPwd.isEnabled = true

                storedVerificationId = verificationId
                resendToken = token
            }
        }
    }

    private fun checkCodeValidation(credential: PhoneAuthCredential, givenID: String) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {    // 인증번호가 일치하는 경우
                    mCountDown.cancel()
                    binding.progressBar1.visibility = View.GONE

                    // 1. 계정 확인... 계정이 존재하는 경우,
                    if (!auth.currentUser!!.email.isNullOrEmpty()) {
                        // 계정 아이디와 입력한 아이디가 동일한 경우
                        if (auth.currentUser!!.email!!.split("@")[0] == givenID) {
                            binding.layoutFindPwdUserInput.visibility = View.GONE
                            binding.layoutFoundUserPwd.visibility = View.VISIBLE

                            binding.btnFindPwd.text = getText(R.string.change_pwd)

                            binding.btnFindPwd.setOnClickListener(changePwdListener)
                        }
                        // 아이디가 일치하지 않는 경우
                        else {
                            noUserInfoMsg()

                            auth.signOut()

                            binding.editUserID.setText("")
                            binding.editPhoneNum.setText("")
                            binding.editConfirmCode.setText("")

                            binding.btnFindPwd.isEnabled = false
                            binding.btnFindPwd.isClickable = true

                            binding.btnSendConfirm.isEnabled = true
                            binding.btnSendConfirm.isClickable = true
                            binding.btnSendConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray))

                            mCountDown.onFinish()
                        }
                    }
                    // 2. 계정이 존재하지 않는 경우
                    else {
                        noUserInfoMsg()

                        auth.currentUser!!.delete()

                        binding.editUserID.setText("")
                        binding.editPhoneNum.setText("")
                        binding.editConfirmCode.setText("")

                        binding.btnFindPwd.isEnabled = false
                        binding.btnFindPwd.isClickable = true

                        binding.btnSendConfirm.isEnabled = true
                        binding.btnSendConfirm.isClickable = true
                        binding.btnSendConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray))

                        mCountDown.onFinish()
                    }
                } else {
                    binding.progressBar1.visibility = View.GONE

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        binding.textFieldConfirmCode.error = getString(R.string.warning_wrong_confirms)
                        binding.btnFindPwd.isClickable = true
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
            binding.btnSendConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray))
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
        if (auth.currentUser != null) {
            auth.signOut()
        }
    }
}