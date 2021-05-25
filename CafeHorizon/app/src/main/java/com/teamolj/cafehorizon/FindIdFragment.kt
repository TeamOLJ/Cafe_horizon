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
import com.teamolj.cafehorizon.databinding.FragmentFindIdBinding
import com.teamolj.cafehorizon.operation.InternetConnection

class FindIdFragment : Fragment() {
    private lateinit var binding: FragmentFindIdBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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
                // 인증문자 보내기

                // if 보내는 데에 성공한 경우,
                binding.btnFindID.isEnabled = true

                binding.btnSendConfirm.isEnabled = false
                binding.btnSendConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.lightgray))
                binding.textFieldPhoneNum.isHelperTextEnabled = true

                mCountDown.start()

                // else 실패한 경우,
//                Toast.makeText(requireContext(), getString(R.string.toast_failed_to_send), Toast.LENGTH_SHORT).show()
//                binding.btnSendConfirm.isClickable = true
            }
        }

        binding.btnFindID.setOnClickListener {
            binding.btnFindID.isClickable = false

            if (!InternetConnection.isInternetConnected(requireContext())) {
                Toast.makeText(requireContext(), getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
                binding.btnFindID.isClickable = true
            }
            else if (binding.editConfirmCode.text.toString().trim().isEmpty()) {
                binding.textFieldConfirmCode.error = getString(R.string.warning_empty_confirms)
                binding.btnFindID.isClickable = true
            }
            else {
                // 인증번호 일치여부 확인

                // if 일치하는 경우,
                // 아이디 찾기 루틴 실행
                mCountDown.cancel()

                binding.layoutFindIdUserInput.visibility = View.GONE
                binding.layoutFoundUserID.visibility = View.VISIBLE
                binding.textFoundUserID.text = "찾아낸 아이디"
                binding.btnFindID.text = getText(R.string.btn_go_login)

                binding.btnFindID.setOnClickListener {
                    activity?.finish()
                }

                // else 일치하지 않는 경우,
//                binding.textFieldConfirmCode.error = getString(R.string.warning_wrong_confirms)
//                binding.btnFindID.isClickable = true
            }
        }

        return binding.root
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