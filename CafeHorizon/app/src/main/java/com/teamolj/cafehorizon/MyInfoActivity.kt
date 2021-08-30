package com.teamolj.cafehorizon

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.firebase.FirebaseException
import com.google.firebase.Timestamp
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.teamolj.cafehorizon.databinding.ActivityMyinfoBinding
import com.teamolj.cafehorizon.operation.InternetConnection
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MyInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyinfoBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private lateinit var newPhoneWithFormat: String
    private val yearToday = Calendar.getInstance().get(Calendar.YEAR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyinfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

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
        binding.itemUserBday.setDescText(App.prefs.getDateAsString("userBday"))

        binding.itemTermMarketing.setSwitch(App.prefs.getBoolean("userAgreeMarketing"))
        binding.itemTermPushMsg.setSwitch(App.prefs.getBoolean("userAgreePush"))

        // nickname
        binding.itemUserNickname.setOnClickListener {
            binding.itemUserNickname.toggleSlider()
            if (binding.itemUserNickname.isSlideOpen()) {
                binding.layoutChangeNickname.visibility = View.VISIBLE
            }
            else {
                binding.layoutChangeNickname.visibility = View.GONE
            }
        }

        binding.editNewNick.doOnTextChanged { _, _, _, _ ->
            binding.textFieldNewNick.error = null
            binding.textFieldNewNick.isErrorEnabled = false
        }

        binding.btnChangeNick.setOnClickListener {
            binding.btnChangeNick.isClickable = false
            closeKeyboard()

            if (!InternetConnection.isInternetConnected(this)) {
                Toast.makeText(this, getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
                binding.btnChangeNick.isClickable = true
            }
            else if (binding.editNewNick.text.toString().trim().isEmpty()) {
                binding.textFieldNewNick.error = getString(R.string.warning_empty_usernick)
                binding.btnChangeNick.isClickable = true
            }
            else if (!binding.editNewNick.text.toString().trim().matches("[가-힣]{2,7}".toRegex())) {
                binding.textFieldNewNick.error = getString(R.string.warning_wrong_nick_format)
                binding.btnChangeNick.isClickable = true
            }
            else if (binding.editNewNick.text.toString().trim() == App.prefs.getString("userNick", "")) {
                binding.textFieldNewNick.error = getString(R.string.warning_same_usernick)
                binding.btnChangeNick.isClickable = true
            }
            else {
                binding.progressBar.visibility = View.VISIBLE

                val newNick = binding.editNewNick.text.toString().trim()

                db.collection("UserInformation").document(auth.currentUser!!.uid)
                    .update("userNick", newNick)
                    .addOnCompleteListener { task ->
                        binding.progressBar.visibility = View.GONE
                        binding.btnChangeNick.isClickable = true

                        if (task.isSuccessful) {
                            App.prefs.setString("userNick", newNick)
                            binding.itemUserNickname.setDescText(newNick)

                            binding.editNewNick.setText("")

                            if (binding.itemUserNickname.isSlideOpen()) {
                                binding.itemUserNickname.toggleSlider()
                                binding.layoutChangeNickname.visibility = View.GONE
                            }

                            // 메인에 변경 사항 발생 알림
                            val mIntent = Intent(this, MainActivity::class.java)
                            setResult(Activity.RESULT_OK, mIntent)

                            Toast.makeText(this, getString(R.string.toast_nickname_changed), Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

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
                val newPhone = binding.editPhoneNum.text.toString().trim()
                newPhoneWithFormat = "${newPhone.substring(0,3)}-${newPhone.substring(3,7)}-${newPhone.substring(7)}"

                if (App.prefs.getString("userPhone", "") == newPhoneWithFormat) {
                    binding.textFieldPhoneNum.error = getString(R.string.warning_same_phonenum)
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
                        .setCallbacks(callbacks)
                        .build()

                    // 인증문자 보내기
                    PhoneAuthProvider.verifyPhoneNumber(options)
                }
            }
        }

        binding.btnCheckConfirm.setOnClickListener {
            binding.btnCheckConfirm.isClickable = false
            closeKeyboard()

            if (!InternetConnection.isInternetConnected(this)) {
                Toast.makeText(this, getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
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

                auth.currentUser!!.updatePhoneNumber(credential)
                    .addOnCompleteListener { task ->
                        binding.progressBar.visibility = View.GONE
                        binding.btnCheckConfirm.isClickable = true

                        if (task.isSuccessful) {
                            mCountDown.cancel()
                            binding.textFieldPhoneNum.isHelperTextEnabled = false

                            binding.itemUserPhoneNum.setDescText(newPhoneWithFormat)
                            App.prefs.setString("userPhone", newPhoneWithFormat)

                            binding.editPhoneNum.setText("")
                            binding.editConfirmCode.setText("")

                            if (binding.itemUserPhoneNum.isSlideOpen()) {
                                binding.itemUserPhoneNum.toggleSlider()
                                binding.layoutChangePhoneNum.visibility = View.GONE
                            }

                            // 바코드 번호도 함께 변경
                            val newBarcode = createBarcode("0${newPhoneWithFormat.substring(3)}")
                            db.collection("UserInformation").document(auth.currentUser!!.uid).update("userBarcode", newBarcode)
                            App.prefs.setString("userBarcode", newBarcode)

                            Toast.makeText(this, R.string.toast_phone_changed, Toast.LENGTH_SHORT).show()

                        } else {
                            if (task.exception is FirebaseAuthInvalidCredentialsException) {
                                binding.textFieldConfirmCode.error = getString(R.string.warning_wrong_confirms)
                            }
                            else {
                                Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
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

        binding.btnChangePwd.setOnClickListener {
            binding.btnChangePwd.isClickable = false
            closeKeyboard()

            if (!InternetConnection.isInternetConnected(this)) {
                Toast.makeText(this, getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
                binding.btnChangePwd.isClickable = true
            }
            else if (binding.editCurrentPwd.text.toString().trim().isEmpty()) {
                binding.textFieldCurrentPwd.error = getString(R.string.warning_empty_userpwd)
                binding.btnChangePwd.isClickable = true
            }
            else if (binding.editNewPwd.text.toString().trim().isEmpty()) {
                binding.textFieldNewPwd.error = getString(R.string.warning_empty_userpwd)
                binding.btnChangePwd.isClickable = true
            }
            else if (binding.editNewPwd.text.toString().length < 8 || binding.editNewPwd.text.toString().length > 15) {
                binding.textFieldNewPwd.error = getString(R.string.warning_wrong_pwd_format)
                binding.btnChangePwd.isClickable = true
            }
            else if (binding.editConfirmPwd.text.toString().trim().isEmpty()) {
                binding.textFieldConfirmPwd.error = getString(R.string.warning_empty_pwd_confirm)
                binding.btnChangePwd.isClickable = true
            }
            else if (binding.editNewPwd.text.toString().trim() != binding.editConfirmPwd.text.toString().trim()) {
                binding.textFieldConfirmPwd.error = getString(R.string.warning_different_pwd)
                binding.btnChangePwd.isClickable = true
            }
            else {
                binding.progressBar.visibility = View.VISIBLE

                val email = App.prefs.getString("userID", "") + getString(R.string.email_domain)
                val newPwd = binding.editNewPwd.text.toString().trim()

                val user = auth.currentUser!!
                val credential = EmailAuthProvider.getCredential(email, binding.editCurrentPwd.text.toString().trim())

                user.reauthenticate(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {

                            user.updatePassword(newPwd)
                                .addOnCompleteListener { pwdTask ->
                                    binding.progressBar.visibility = View.GONE
                                    binding.btnChangePwd.isClickable = true

                                    if (pwdTask.isSuccessful) {
                                        binding.editCurrentPwd.setText("")
                                        binding.editNewPwd.setText("")
                                        binding.editConfirmPwd.setText("")

                                        if (binding.itemUserPwd.isSlideOpen()) {
                                            binding.itemUserPwd.toggleSlider()
                                            binding.layoutChangePassword.visibility = View.GONE
                                        }

                                        Toast.makeText(this, getString(R.string.toast_pwd_changed), Toast.LENGTH_SHORT).show()
                                    }
                                    else {
                                        Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                        else {
                            binding.textFieldCurrentPwd.error = getString(R.string.warning_wrong_pwd)

                            binding.progressBar.visibility = View.GONE
                            binding.btnChangePwd.isClickable = true
                        }
                    }
            }
        }

        // birthday
        binding.itemUserBday.setOnClickListener {
            binding.itemUserBday.toggleSlider()
            if (binding.itemUserBday.isSlideOpen()) {
                binding.layoutChangeBday.visibility = View.VISIBLE
            }
            else {
                binding.layoutChangeBday.visibility = View.GONE
            }
        }

        binding.editYear.doOnTextChanged { _, _, _, _ ->
            binding.textFieldYear.error = null
            binding.textFieldYear.isErrorEnabled = false
        }

        binding.editMonth.doOnTextChanged { _, _, _, _ ->
            binding.textFieldMonth.error = null
            binding.textFieldMonth.isErrorEnabled = false
        }

        binding.editDate.doOnTextChanged { _, _, _, _ ->
            binding.textFieldDate.error = null
            binding.textFieldDate.isErrorEnabled = false
        }

        binding.btnChangeBday.setOnClickListener {
            binding.btnChangeBday.isClickable = false
            closeKeyboard()

            if (!InternetConnection.isInternetConnected(this)) {
                Toast.makeText(this, getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
                binding.btnChangeBday.isClickable = true
            }
            else if(!checkBdayValidation()) {
                binding.btnChangeBday.isClickable = true
            }
            else {
                val year = binding.editYear.text.toString().trim()
                val month = binding.editMonth.text.toString().trim()
                val date = binding.editDate.text.toString().trim()

                val bdayBefore = App.prefs.getString("userBday", "")
                val bdayNew = "$year/${month.padStart(2, '0')}/${date.padStart(2, '0')}"

                if (bdayBefore == bdayNew) {
                    Toast.makeText(this, getString(R.string.warning_no_changes), Toast.LENGTH_SHORT).show()
                    binding.btnChangeBday.isClickable = true
                }
                else {
                    val userUID = auth.currentUser!!.uid

                    // 마지막 변경 날짜 확인
                    var lastModified: Date? = null
                    if (App.prefs.getDateAsString("lastBdayMod") != "")
                        lastModified = Date(SimpleDateFormat("yyyy/MM/dd").parse(App.prefs.getDateAsString("lastBdayMod")).time)

                    // 변경일로부터 1년 이상 지났으면 생일 변경
                    if (lastModified == null || Date().time - lastModified.time >= 60 * 60 * 24 * 365) {
                        binding.progressBar.visibility = View.VISIBLE

                        val newUserBday = Timestamp(Date(SimpleDateFormat("yyyy/MM/dd").parse(bdayNew).time))
                        db.collection("UserInformation").document(userUID)
                            .update("userBday", newUserBday, "lastBdayModified", Timestamp(Date()))
                            .addOnCompleteListener { task ->
                                binding.progressBar.visibility = View.GONE
                                binding.btnChangeBday.isClickable = true

                                if (task.isSuccessful) {
                                    App.prefs.setString("userBday", bdayNew)
                                    App.prefs.setDateAsString("lastBdayMod", Date())

                                    binding.editYear.setText("")
                                    binding.editMonth.setText("")
                                    binding.editDate.setText("")

                                    binding.itemUserBday.setDescText(bdayNew)

                                    if (binding.itemUserBday.isSlideOpen()) {
                                        binding.itemUserBday.toggleSlider()
                                        binding.layoutChangeBday.visibility = View.GONE
                                    }

                                    Toast.makeText(this, getString(R.string.toast_birthday_changed), Toast.LENGTH_SHORT).show()

                                } else {
                                    Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                    else {
                        Toast.makeText(this, getString(R.string.warning_year_not_past), Toast.LENGTH_SHORT).show()
                        binding.btnChangeBday.isClickable = true
                    }
                }
            }
        }

        binding.itemTermMarketing.setOnClickListener {
            if (binding.itemTermMarketing.isSwitchChecked()) {
                Toast.makeText(this, getString(R.string.toast_marketing_agreed), Toast.LENGTH_SHORT).show()
                db.collection("UserInformation").document(auth.currentUser!!.uid).update("agreeMarketing", true)
                App.prefs.setBoolean("userAgreeMarketing", true)
                FirebaseMessaging.getInstance().token
            }
            else {
                Toast.makeText(this, getString(R.string.toast_marketing_disagreed), Toast.LENGTH_SHORT).show()
                db.collection("UserInformation").document(auth.currentUser!!.uid).update("agreeMarketing", false)
                App.prefs.setBoolean("userAgreeMarketing", false)
            }
        }

        binding.itemTermPushMsg.setOnClickListener {
            if (binding.itemTermPushMsg.isSwitchChecked()) {
                Toast.makeText(this, getString(R.string.toast_push_agreed), Toast.LENGTH_SHORT).show()
                db.collection("UserInformation").document(auth.currentUser!!.uid).update("agreePush", true)
                App.prefs.setBoolean("userAgreePush", true)
                FirebaseMessaging.getInstance().token
            }
            else {
                Toast.makeText(this, getString(R.string.toast_push_disagreed), Toast.LENGTH_SHORT).show()
                db.collection("UserInformation").document(auth.currentUser!!.uid).update("agreePush", false)
                App.prefs.setBoolean("userAgreePush", false)
            }
        }

        binding.btnUnsubscribe.setOnClickListener {
            val intent = Intent(this, UnsubscribeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initCallbackObject() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) { }

            override fun onVerificationFailed(e: FirebaseException) {
                binding.progressBar.visibility = View.GONE

                if (e is FirebaseAuthInvalidCredentialsException) {
                    binding.textFieldPhoneNum.error = getString(R.string.warning_wrong_format)
                } else {
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
                binding.btnCheckConfirm.isEnabled = true

                storedVerificationId = verificationId
                resendToken = token
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
        }
    }

    private fun checkBdayValidation() : Boolean {

        val year = binding.editYear.text.toString().trim()
        val month = binding.editMonth.text.toString().trim()
        val date = binding.editDate.text.toString().trim()

        val dateArray = arrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        if (year.isNotEmpty() && year.toInt() % 4 == 0)
            dateArray[2] += 1

        // 생일 칸이 전부 비어있는 경우
        if (year.isEmpty() && month.isEmpty() && date.isEmpty()) {
            return true
        }
        // 모두 채운 경우
        else if (year.isNotEmpty() && month.isNotEmpty() && date.isNotEmpty()) {
            if (year.toInt() > yearToday || year.toInt() < yearToday - 150) {
                binding.textFieldYear.requestFocus()
                binding.textFieldYear.error = " "
                return false
            }
            else if (month.toInt() < 1 || month.toInt() > 12) {
                binding.textFieldMonth.requestFocus()
                binding.textFieldMonth.error = " "
                return false
            }
            else if (date.toInt() < 1 || date.toInt() > dateArray[month.toInt()]) {
                binding.textFieldDate.requestFocus()
                binding.textFieldDate.error = " "
                return false
            }
            return true
        }
        // 채우다 만 경우
        else {
            if (year.isEmpty())
                binding.textFieldYear.error = " "
            if (month.isEmpty())
                binding.textFieldMonth.error = " "
            if (date.isEmpty())
                binding.textFieldDate.error = " "
            return false
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

    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }
    }
}