package com.teamolj.cafehorizon.signUp

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.facebook.Profile
import com.facebook.login.LoginManager
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.Timestamp
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.teamolj.cafehorizon.App
import com.teamolj.cafehorizon.MainActivity
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivitySignupSocialBinding
import com.teamolj.cafehorizon.operation.InternetConnection
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SignUpSocialActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupSocialBinding

    private var authProvider: String? = null
    private var facebookToken: String? = null
    private var facebookUserBday: String? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupSocialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authProvider = intent.getStringExtra("authProvider")
        facebookToken = intent.getStringExtra("facebookToken")
        facebookUserBday = intent.getStringExtra("facebookUserBday")

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            showExitMsg()
        }

        auth = Firebase.auth
        db = Firebase.firestore

        db.collection("Operational").document("SignUpTerms")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val termService = document.data?.get("termService").toString().replace("\\\\n", "\n")
                    val termPersonalInfo = document.data?.get("termPersonalInfo").toString().replace("\\\\n", "\n")
                    val termMarketingMessage = document.data?.get("termMarketingMessage").toString().replace("\\\\n", "\n")
                    val termPushMessage = document.data?.get("termPushMessage").toString().replace("\\\\n", "\n")

                    binding.textTermService.text = termService
                    binding.textTermPersonal.text = termPersonalInfo
                    binding.textTermMarketing.text = termMarketingMessage
                    binding.textPushMsg.text = termPushMessage

                } else {
                    binding.textTermService.text = getString(R.string.toast_error_occurred)
                    binding.textTermPersonal.text = getString(R.string.toast_error_occurred)
                    binding.textTermMarketing.text = getString(R.string.toast_error_occurred)
                    binding.textPushMsg.text = getString(R.string.toast_error_occurred)
                }
            }
            .addOnFailureListener {
                binding.textTermService.text = getString(R.string.toast_error_occurred)
                binding.textTermPersonal.text = getString(R.string.toast_error_occurred)
                binding.textTermMarketing.text = getString(R.string.toast_error_occurred)
                binding.textPushMsg.text = getString(R.string.toast_error_occurred)
            }

        binding.checkAllTerm.setOnClickListener {
            if (binding.checkAllTerm.isChecked) {
                binding.checkTermService.isChecked = true
                binding.checkTermPersonal.isChecked = true
                binding.checkTermMarketing.isChecked = true
                binding.checkPushMsg.isChecked = true

                binding.tvConfirmUser.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
                binding.layoutConfirmUser.visibility = View.VISIBLE
            }
            else {
                binding.checkTermService.isChecked = false
                binding.checkTermPersonal.isChecked = false
                binding.checkTermMarketing.isChecked = false
                binding.checkPushMsg.isChecked = false

                binding.tvConfirmUser.setBackgroundColor(ContextCompat.getColor(this, R.color.semigray))
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
                binding.tvConfirmUser.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
                binding.layoutConfirmUser.visibility = View.VISIBLE
            }
            else {
                binding.tvConfirmUser.setBackgroundColor(ContextCompat.getColor(this, R.color.semigray))
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
                binding.tvConfirmUser.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
                binding.layoutConfirmUser.visibility = View.VISIBLE
            }
            else {
                binding.tvConfirmUser.setBackgroundColor(ContextCompat.getColor(this, R.color.semigray))
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
            closeKeyboard()

            if (!InternetConnection.isInternetConnected(this)) {
                Toast.makeText(this, getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
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
                    .setActivity(this)
                    .setCallbacks(phoneCallbacks)
                    .build()

                // 인증문자 보내기
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }

        binding.btnSignUp.setOnClickListener {
            binding.btnSignUp.isClickable = false
            closeKeyboard()

            if (!InternetConnection.isInternetConnected(this)) {
                Toast.makeText(this, getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
                binding.btnSignUp.isClickable = true
            }
            else if (binding.editConfirmCode.text.toString().trim().isEmpty()) {
                binding.textFieldConfirmCode.error = getString(R.string.warning_empty_confirms)
                binding.btnSignUp.isClickable = true
            }
            else {
                binding.progressBar.visibility = View.VISIBLE

                val confirmCode = binding.editConfirmCode.text.toString().trim()
                val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, confirmCode)

                // 인증번호 일치여부 확인
                checkCodeValidation(credential)
            }
        }
    }

    private fun initCallbackObject() {
        phoneCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) { }

            override fun onVerificationFailed(e: FirebaseException) {
                binding.progressBar.visibility = View.GONE

                if (e is FirebaseAuthInvalidCredentialsException) {
                    binding.textFieldPhoneNum.error = getString(R.string.warning_wrong_format)
                } else if (e is FirebaseTooManyRequestsException) {
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
                binding.btnSignUp.isEnabled = true

                storedVerificationId = verificationId
                resendToken = token
            }
        }
    }

    private fun checkCodeValidation(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                // 인증번호가 일치하는 경우
                if (task.isSuccessful) {
                    mCountDown.cancel()

                    val user = auth.currentUser!!

                    // 1. 현재 계정(전화번호 로그인)과 페이스북 계정 연결
                    val facebookCredential = FacebookAuthProvider.getCredential(facebookToken!!)
                    user.linkWithCredential(facebookCredential)
                        .addOnCompleteListener { _task ->
                            // 연결에 성공한 경우
                            if (_task.isSuccessful) {
                                // 2. 기존 유저의 신규 연동인지 최초 유저 가입인지 확인
                                if (!user.displayName.isNullOrEmpty()) {
                                    // 2-1. 기존 유저면 로그인 절차만 수행
                                    fetchUserInformation()
                                }
                                else {
                                    // 2-2. 최초 가입 유저면 사용자 정보 DB 업로드 후 로그인 수행
                                    user.updateProfile(userProfileChangeRequest { displayName = "SocialMember" })

                                    // 사용자 정보를 DB에 문서화
                                    val userBarcode = createBarcode("0${user.phoneNumber!!.substring(3)}")
                                    val agreeMarketing = binding.checkTermMarketing.isChecked
                                    val agreePush = binding.checkPushMsg.isChecked

                                    val userNick = Profile.getCurrentProfile().firstName

                                    val userBdayString : String
                                    val userBday : Timestamp?

                                    if (facebookUserBday != null) {
                                        val year = facebookUserBday!!.split("/")[2]
                                        var month = facebookUserBday!!.split("/")[0]
                                        if (month.length == 1) month = "0$month"
                                        var date = facebookUserBday!!.split("/")[1]
                                        if (date.length == 1) date = "0$date"
                                        userBdayString = "$year/$month/$date"
                                        userBday = Timestamp(Date(SimpleDateFormat("yyyy/MM/dd").parse(userBdayString).time))
                                    } else {
                                        userBdayString = ""
                                        userBday = null
                                    }

                                    val userData = hashMapOf(
                                        "userID" to " ",
                                        "userNick" to userNick,
                                        "userBarcode" to userBarcode,
                                        "userBday" to userBday,
                                        "lastBdayModified" to null,
                                        "agreeMarketing" to agreeMarketing,
                                        "agreePush" to agreePush,
                                        "deviceToken" to null
                                    )

                                    db.collection("UserInformation").document(user.uid)
                                        .set(userData)
                                        .addOnSuccessListener {
                                            // 로그인 처리 (sharedPreference 설정 등)
                                            App.prefs.setString("userID", " ")
                                            App.prefs.setString("userNick", userNick)
                                            App.prefs.setString("userBarcode", userBarcode)
                                            App.prefs.setString("userBday", userBdayString)

                                            App.prefs.setString("userPhone", "0${user.phoneNumber!!.substring(3,5)}-${user.phoneNumber!!.substring(5,9)}-${user.phoneNumber!!.substring(9)}")

                                            App.prefs.setBoolean("userAgreeMarketing", agreeMarketing)
                                            App.prefs.setBoolean("userAgreePush", agreePush)

                                            // 푸시메시지에 동의한 경우 토큰 생성
                                            if (agreeMarketing || agreePush)
                                                FirebaseMessaging.getInstance().token

                                            // 로그인 완료
                                            val intent = Intent(this, MainActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                        }
                                        .addOnFailureListener {
                                            // 연결 해제
                                            Firebase.auth.currentUser!!.unlink(FacebookAuthProvider.PROVIDER_ID)

                                            binding.progressBar.visibility = View.GONE
                                            Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                                            binding.btnSignUp.isClickable = true
                                        }
                                }
                            }
                            else {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(applicationContext, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                                binding.btnSignUp.isClickable = true
                            }
                        }
                } else {
                    binding.progressBar.visibility = View.GONE

                    binding.textFieldConfirmCode.error = getString(R.string.warning_wrong_confirms)
                    binding.btnSignUp.isClickable = true
                }
            }
    }

    private fun fetchUserInformation() {
        val userUID = auth.currentUser!!.uid
        val userPhone = auth.currentUser!!.phoneNumber

        val docRef = db.collection("UserInformation").document(userUID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    App.prefs.setString("userID", document.data?.get("userID").toString())
                    App.prefs.setString("userNick", document.data?.get("userNick").toString())
                    App.prefs.setString("userBarcode", document.data?.get("userBarcode").toString())

                    if (document.getTimestamp("userBday") != null)
                        App.prefs.setDateAsString("userBday", document.getTimestamp("userBday")!!.toDate())
                    else
                        App.prefs.setDateAsString("userBday", null)

                    if (document.getTimestamp("lastBdayModified") != null)
                        App.prefs.setDateAsString("lastBdayMod", document.getTimestamp("lastBdayModified")!!.toDate())
                    else
                        App.prefs.setDateAsString("lastBdayMod", null)

                    App.prefs.setString("userPhone", "0${userPhone!!.substring(3,5)}-${userPhone.substring(5,9)}-${userPhone.substring(9)}")

                    App.prefs.setBoolean("userAgreeMarketing", document.getBoolean("agreeMarketing")!!)
                    App.prefs.setBoolean("userAgreePush", document.getBoolean("agreePush")!!)

                    if (document.getBoolean("agreeMarketing")!! || document.getBoolean("agreePush")!!)
                        FirebaseMessaging.getInstance().token

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Firebase.auth.signOut()
                    Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                    binding.btnSignUp.isClickable = true
                }
            }
            .addOnFailureListener { exception ->
                Firebase.auth.signOut()
                Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                binding.btnSignUp.isClickable = true
            }
    }

    private fun createBarcode(phoneNum : String) : String {
        val result = StringBuilder()
        val key = getString(R.string.barcode_key)

        var temp = 0

        for (idx in 0..10) {
            temp = phoneNum[idx].digitToInt() + key[idx].digitToInt()
            if (temp >= 10)
                temp -= 10
            result.append(temp)
        }

        return result.toString()
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
            binding.btnSendConfirm.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.gray))

            binding.btnSignUp.isClickable = true
            binding.btnSignUp.isEnabled = false
        }
    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }
    }
    private fun showExitMsg() {
        val builder = AlertDialog.Builder(this)

        builder.setMessage(getString(R.string.cancel_signup))
            .setPositiveButton(getString(R.string.btn_goback)) { _, _ ->
                // 인증 정보 삭제
                if (authProvider == "Facebook")
                    LoginManager.getInstance().logOut()

                finish()
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .setCancelable(true)

        builder.create().show()
    }

    override fun onBackPressed() {
        showExitMsg()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCountDown.cancel()
    }
}