package com.teamolj.cafehorizon

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.teamolj.cafehorizon.databinding.FragmentFindPwdBinding
import com.teamolj.cafehorizon.operation.InternetConnection

class FindPwdFragment : Fragment() {
    private lateinit var binding: FragmentFindPwdBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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
            binding.textFieldConfirmCode.isErrorEnabled = false
        }

        binding.editConfirmPwd.doOnTextChanged { _, _, _, _ ->
            binding.textFieldConfirmPwd.error = null
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
                // 인증문자 보내기

                // if 보내는 데에 성공한 경우,
                binding.btnFindPwd.isEnabled = true

                binding.btnSendConfirm.isEnabled = false
                binding.btnSendConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.lightgray))
                binding.textFieldPhoneNum.isHelperTextEnabled = true

                mCountDown.start()

                // else 실패한 경우,
//                Toast.makeText(requireContext(), getString(R.string.toast_failed_to_send), Toast.LENGTH_SHORT).show()
//                binding.btnSendConfirm.isClickable = true
            }
        }

        binding.btnFindPwd.setOnClickListener {
            binding.btnFindPwd.isClickable = false

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
            else {
                // 인증번호 일치여부 확인

                // if 일치하는 경우,
                mCountDown.cancel()

                binding.layoutFindPwdUserInput.visibility = View.GONE
                binding.layoutFoundUserPwd.visibility = View.VISIBLE

                binding.btnFindPwd.text = getText(R.string.chance_pwd)

                binding.btnFindPwd.setOnClickListener(changePwdListener)

                // else 일치하지 않는 경우,
//                binding.textFieldConfirmCode.error = getString(R.string.warning_wrong_confirms)
//                binding.btnFindPwd.isClickable = true
            }
        }

        return binding.root
    }

    private val changePwdListener = View.OnClickListener {
        binding.btnFindPwd.isClickable = false

        if (!InternetConnection.isInternetConnected(requireContext())) {
            Toast.makeText(requireContext(), getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
            binding.btnFindPwd.isClickable = true
        }
        else if (binding.editNewPwd.text.toString().trim().isEmpty()) {
            binding.textFieldNewPwd.error = getString(R.string.warning_empty_userpwd)
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

            // if 성공한 경우
            Toast.makeText(requireContext(), getString(R.string.toast_pwd_changed), Toast.LENGTH_SHORT).show()
            activity?.finish()

            // else 실패한 경우
//            Toast.makeText(requireContext(), getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
//            binding.btnFindPwd.isClickable = true
        }
    }

    private val mCountDown: CountDownTimer = object : CountDownTimer(180000, 1000) {
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
}