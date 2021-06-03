package com.teamolj.cafehorizon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.teamolj.cafehorizon.databinding.ActivityLoginBinding
import com.teamolj.cafehorizon.operation.InternetConnection
import com.teamolj.cafehorizon.sign.SigninActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private var doubleBackPressed: Boolean = false
    private lateinit var closeToast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
//                if (실패한 경우) {
//                    Toast.makeText(this, getString(R.string.toast_failed_verification), Toast.LENGTH_SHORT).show()
//                    binding.btnLogin.isClickable = true
//                }
//                else {
//                    // 사용자 닉네임 & 로그인 타입(일반계정) 저장: sharedPreferences
//                    // 로그인 상태정보(액세스 키 등) 저장, 자동로그인 기본 허용
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
//                }
            }
        }

        binding.btnFindUserInfo.setOnClickListener {
            val intent = Intent(this, FindInfoActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
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