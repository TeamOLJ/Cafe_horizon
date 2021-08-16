package com.teamolj.cafehorizon.signUp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.FragmentSignup2Binding
import com.teamolj.cafehorizon.operation.InternetConnection

class SignUpFragment2 : Fragment() {
    private lateinit var binding: FragmentSignup2Binding

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        auth = Firebase.auth
        var checkedNick = ""

        binding = FragmentSignup2Binding.inflate(inflater, container, false)

        binding.btnCheckDuple.setOnClickListener {
            binding.textFieldUserID.error = null
            binding.textFieldUserID.isErrorEnabled = false

            // 아이디 중복여부 확인

            // if 중복이 아니면
            checkedNick = binding.editUserID.text.toString().trim()
        }

        binding.editUserID.doOnTextChanged { _, _, _, _ ->
            binding.textFieldUserID.error = null
            binding.textFieldUserID.isErrorEnabled = false
        }

        binding.editUserNick.doOnTextChanged { _, _, _, _ ->
            binding.textFieldUserNick.error = null
            binding.textFieldUserNick.isErrorEnabled = false
        }

        binding.editUserPwd.doOnTextChanged { _, _, _, _ ->
            binding.textFieldUserPwd.error = null
            binding.textFieldUserPwd.isErrorEnabled = false
        }

        binding.editConfirmPwd.doOnTextChanged { _, _, _, _ ->
            binding.textFieldConfirmPwd.error = null
            binding.textFieldConfirmPwd.isErrorEnabled = false
        }

        binding.textFieldConfirmPwd.setOnFocusChangeListener { view, state ->
            if (!state) {
                if (binding.editUserPwd.text.toString().trim() != binding.editConfirmPwd.text.toString().trim())
                    binding.textFieldConfirmPwd.error = getString(R.string.warning_different_pwd)
            }
        }

        binding.btnSignUp.setOnClickListener {
            binding.btnSignUp.isClickable = false

            if (!InternetConnection.isInternetConnected(requireContext())) {
                Toast.makeText(requireContext(), getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
                binding.btnSignUp.isClickable = true
            }
            else if (binding.editUserID.text.toString().trim().isEmpty()) {
                binding.textFieldUserID.error = getString(R.string.warning_empty_userid)
                binding.btnSignUp.isClickable = true
            }
            else if (binding.editUserID.text.toString().trim() != checkedNick) {
                binding.textFieldUserID.error = getString(R.string.warning_check_userid)
                binding.btnSignUp.isClickable = true
            }
            else if (binding.editUserNick.text.toString().trim().isEmpty()) {
                binding.textFieldUserNick.error = getString(R.string.warning_empty_usernick)
                binding.btnSignUp.isClickable = true
            }
            else if (binding.editUserPwd.text.toString().trim().isEmpty()) {
                binding.textFieldUserPwd.error = getString(R.string.warning_empty_userpwd)
                binding.btnSignUp.isClickable = true
            }
            else if (binding.editUserPwd.text.toString().length < 8 || binding.editUserPwd.text.toString().length > 15) {
                binding.textFieldUserPwd.error = getString(R.string.warning_wrong_pwd_format)
                binding.btnSignUp.isClickable = true
            }
            else if (binding.editConfirmPwd.text.toString().trim().isEmpty()) {
                binding.textFieldConfirmPwd.error = getString(R.string.warning_empty_pwd_confirm)
                binding.btnSignUp.isClickable = true
            }
            else if (binding.editUserPwd.text.toString().trim() != binding.editConfirmPwd.text.toString().trim()) {
                binding.textFieldConfirmPwd.error = getString(R.string.warning_different_pwd)
                binding.btnSignUp.isClickable = true
            }
            else {
                // 회원가입 처리

                // 로그인 처리 (sharedPreference 설정 등)

                // if 모두 성공한 경우
                (activity as SignUpActivity).callSignupSuccess()
            }
        }

        return binding.root
    }

}