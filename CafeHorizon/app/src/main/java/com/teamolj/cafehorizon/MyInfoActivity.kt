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
import com.teamolj.cafehorizon.databinding.ActivityMyinfoBinding
import com.teamolj.cafehorizon.operation.InternetConnection

class MyInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyinfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyinfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                // 인증문자 보내기

                // if 보내는 데에 성공한 경우,
                binding.btnCheckConfirm.isEnabled = true

                binding.btnSendConfirm.isEnabled = false
                binding.btnSendConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightgray))
                binding.textFieldPhoneNum.isHelperTextEnabled = true

                mCountDown.start()

                // else 실패한 경우,
//                Toast.makeText(this, getString(R.string.toast_failed_to_send), Toast.LENGTH_SHORT).show()
//                binding.btnSendConfirm.isClickable = true
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
                // 인증번호 일치여부 확인

                // if 일치하는 경우,
                // 휴대폰번호 변경 루틴 실행
                mCountDown.cancel()

                binding.itemUserPhoneNum.setDescText("010-new-phone")

                if (binding.itemUserPhoneNum.isSlideOpen()) {
                    binding.itemUserPhoneNum.toggleSlider()
                    binding.layoutChangePhoneNum.visibility = View.GONE
                }

                Toast.makeText(this, R.string.toast_phone_changed, Toast.LENGTH_SHORT).show()

                // else 일치하지 않는 경우,
                // binding.textFieldConfirmCode.error = getString(R.string.warning_wrong_confirms)

                binding.btnCheckConfirm.isClickable = true
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

        binding.btnChagedPwd.setOnClickListener {
            binding.btnChagedPwd.isClickable = false

            if (!InternetConnection.isInternetConnected(this)) {
                Toast.makeText(this, getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
                binding.btnChagedPwd.isClickable = true
            }
            else if (binding.editCurrentPwd.text.toString().trim().isEmpty()) {
                binding.textFieldCurrentPwd.error = getString(R.string.warning_empty_userpwd)
                binding.btnChagedPwd.isClickable = true
            }
            else if (binding.editNewPwd.text.toString().trim().isEmpty()) {
                binding.textFieldNewPwd.error = getString(R.string.warning_empty_userpwd)
                binding.btnChagedPwd.isClickable = true
            }
            else if (binding.editConfirmPwd.text.toString().trim().isEmpty()) {
                binding.textFieldConfirmPwd.error = getString(R.string.warning_empty_pwd_confirm)
                binding.btnChagedPwd.isClickable = true
            }
            else if (binding.editNewPwd.text.toString().trim() != binding.editConfirmPwd.text.toString().trim()) {
                binding.textFieldConfirmPwd.error = getString(R.string.warning_different_pwd)
                binding.btnChagedPwd.isClickable = true
            }
            else {
                val newPwd = binding.editNewPwd.text.toString().trim()

                // 입력한 현재 비밀번호와 기존 비밀번호 일치여부 확인
                // if 일치할 경우,
                // 비밀번호 변경 루틴 진행

                // if 성공한 경우
                if (binding.itemUserPwd.isSlideOpen()) {
                    binding.itemUserPwd.toggleSlider()
                    binding.layoutChangePassword.visibility = View.GONE
                }

                Toast.makeText(this, getString(R.string.toast_pwd_changed), Toast.LENGTH_SHORT).show()

                // else 실패한 경우
                // Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()

                // else 일치하지 않는 경우,
                // binding.textFieldCurrentPwd.error = "잘못된 비밀번호입니다."

                binding.btnChagedPwd.isClickable = true
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

    private val mCountDown: CountDownTimer = object : CountDownTimer(180000, 1000) {
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