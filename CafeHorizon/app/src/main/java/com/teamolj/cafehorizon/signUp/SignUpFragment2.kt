package com.teamolj.cafehorizon.signUp

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.firebase.Timestamp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.teamolj.cafehorizon.App
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.FragmentSignup2Binding
import com.teamolj.cafehorizon.operation.InternetConnection
import java.text.SimpleDateFormat
import java.util.*

class SignUpFragment2 : Fragment() {
    private lateinit var binding: FragmentSignup2Binding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val yearToday = Calendar.getInstance().get(Calendar.YEAR)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        auth = Firebase.auth
        db = Firebase.firestore

        binding = FragmentSignup2Binding.inflate(inflater, container, false)

        var checkedNick = ""
        val regID = "[a-zA-Z0-9]{4,10}".toRegex()
        val regNick = "[가-힣]{2,7}".toRegex()

        binding.btnCheckDuple.setOnClickListener {
            val userID = binding.editUserID.text.toString().trim()

            if (userID.isEmpty()) {
                binding.editUserID.requestFocus()
                binding.textFieldUserID.error = getString(R.string.warning_empty_userid)
                binding.btnSignUp.isClickable = true
            }
            else if (!userID.matches(regID)) {
                binding.editUserID.requestFocus()
                binding.textFieldUserID.error = getString(R.string.id_requirement)
                binding.btnSignUp.isClickable = true
            }
            else {
                // 아이디 중복여부 확인
                db.collection("UserInformation").whereEqualTo("userID", userID)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            checkedNick = userID
                            binding.textFieldUserID.helperText = getString(R.string.warning_usable_userid)
                            binding.textFieldUserID.setHelperTextColor(
                                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.textGreen)))
                        }
                        else {
                            checkedNick = ""
                            binding.textFieldUserID.error = getString(R.string.warning_duplicated_userid)
                        }
                    }
            }
        }

        binding.editUserID.doOnTextChanged { _, _, _, _ ->
            binding.textFieldUserID.error = null
            binding.textFieldUserID.isErrorEnabled = false
            binding.textFieldUserID.helperText = getString(R.string.id_requirement)
            binding.textFieldUserID.setHelperTextColor(ColorStateList.valueOf(binding.textFieldUserNick.helperTextCurrentTextColor))
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

        binding.textFieldConfirmPwd.setOnFocusChangeListener { _, state ->
            if (!state) {
                if (binding.editUserPwd.text.toString().trim() != binding.editConfirmPwd.text.toString().trim())
                    binding.textFieldConfirmPwd.error = getString(R.string.warning_different_pwd)
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

        binding.btnSignUp.setOnClickListener {
            binding.btnSignUp.isClickable = false
            closeKeyboard()

            if (!InternetConnection.isInternetConnected(requireContext())) {
                Toast.makeText(requireContext(), getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()
                binding.btnSignUp.isClickable = true
            }
            else if (binding.editUserID.text.toString().trim().isEmpty()) {
                binding.editUserID.requestFocus()
                binding.textFieldUserID.error = getString(R.string.warning_empty_userid)
                binding.btnSignUp.isClickable = true
            }
            else if (binding.editUserID.text.toString().trim() != checkedNick) {
                binding.editUserID.requestFocus()
                binding.textFieldUserID.error = getString(R.string.warning_check_userid)
                binding.btnSignUp.isClickable = true
            }
            else if (binding.editUserNick.text.toString().trim().isEmpty()) {
                binding.editUserNick.requestFocus()
                binding.textFieldUserNick.error = getString(R.string.warning_empty_usernick)
                binding.btnSignUp.isClickable = true
            }
            else if (!binding.editUserNick.text.toString().trim().matches(regNick)) {
                binding.editUserNick.requestFocus()
                binding.textFieldUserNick.error = getString(R.string.warning_wrong_nick_format)
                binding.btnSignUp.isClickable = true
            }
            else if (binding.editUserPwd.text.toString().trim().isEmpty()) {
                binding.editUserPwd.requestFocus()
                binding.textFieldUserPwd.error = getString(R.string.warning_empty_userpwd)
                binding.btnSignUp.isClickable = true
            }
            else if (binding.editUserPwd.text.toString().length < 8 || binding.editUserPwd.text.toString().length > 15) {
                binding.editUserPwd.requestFocus()
                binding.textFieldUserPwd.error = getString(R.string.warning_wrong_pwd_format)
                binding.btnSignUp.isClickable = true
            }
            else if (binding.editConfirmPwd.text.toString().trim().isEmpty()) {
                binding.editConfirmPwd.requestFocus()
                binding.textFieldConfirmPwd.error = getString(R.string.warning_empty_pwd_confirm)
                binding.btnSignUp.isClickable = true
            }
            else if (binding.editUserPwd.text.toString().trim() != binding.editConfirmPwd.text.toString().trim()) {
                binding.editConfirmPwd.requestFocus()
                binding.textFieldConfirmPwd.error = getString(R.string.warning_different_pwd)
                binding.btnSignUp.isClickable = true
            }
            else if(!checkBdayValidation()) {
                binding.btnSignUp.isClickable = true
            }
            else {
                binding.progressBar.visibility = View.VISIBLE

                val userUID = auth.currentUser!!.uid
                val userPhone = auth.currentUser!!.phoneNumber

                val ID = binding.editUserID.text.toString().trim()
                val userNick = binding.editUserNick.text.toString().trim()
                val userBarcode = createBarcode("0${userPhone!!.substring(3)}")

                val email = binding.editUserID.text.toString().trim() + getString(R.string.email_domain)
                val password = binding.editUserPwd.text.toString().trim()

                val termPair = (activity as SignUpActivity).termAgreedStates()

                val year = binding.editYear.text.toString().trim()
                val month = binding.editMonth.text.toString().trim()
                val date = binding.editDate.text.toString().trim()

                val userBdayString : String
                val userBday : Timestamp?

                if (year.isNotEmpty() && month.isNotEmpty() && date.isNotEmpty()) {
                    userBdayString = "$year-$month-$date"
                    userBday = Timestamp(Date(SimpleDateFormat("yyyy/MM/dd").parse(userBdayString).time))
                } else {
                    userBdayString = ""
                    userBday = null
                }

                // 회원가입 처리
                val credential = EmailAuthProvider.getCredential(email, password)

                auth.currentUser!!.linkWithCredential(credential)
                    .addOnCompleteListener { task ->
                        // 가입 처리에 성공한 경우
                        if (task.isSuccessful) {
                            // 사용자 정보를 DB에 문서화
                            val userData = hashMapOf(
                                "userID" to ID,
                                "userNick" to userNick,
                                "userBarcode" to userBarcode,
                                "userBday" to userBday,
                                "lastBdayModified" to null,
                                "agreeMarketing" to termPair.first,
                                "agreePush" to termPair.second,
                                "deviceToken" to null
                            )

                            db.collection("UserInformation").document(userUID)
                                .set(userData)
                                .addOnSuccessListener {
                                    // 로그인 처리 (sharedPreference 설정 등)
                                    App.prefs.setString("userID", ID)
                                    App.prefs.setString("userNick", userNick)
                                    App.prefs.setString("userBarcode", userBarcode)
                                    App.prefs.setString("userBday", userBdayString)

                                    App.prefs.setString("userPhone", "0${userPhone.substring(3,5)}-${userPhone.substring(5,9)}-${userPhone.substring(9)}")

                                    App.prefs.setBoolean("userAgreeMarketing", termPair.first)
                                    App.prefs.setBoolean("userAgreePush", termPair.second)

                                    // 푸시메시지에 동의한 경우 토큰 생성
                                    if (termPair.second)
                                        FirebaseMessaging.getInstance().token

                                    // 다음 화면으로 전환
                                    binding.progressBar.visibility = View.GONE
                                    (activity as SignUpActivity).callSignupSuccess()

                                }
                                .addOnFailureListener {
                                    // 이메일 연결 해제
                                    Firebase.auth.currentUser!!.unlink(EmailAuthProvider.PROVIDER_ID)

                                    binding.progressBar.visibility = View.GONE
                                    Toast.makeText(context, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                                    binding.btnSignUp.isClickable = true
                                }

                        }
                        else {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(context, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                            binding.btnSignUp.isClickable = true
                        }
                    }
            }
        }

        return binding.root
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
        val view = requireActivity().currentFocus
        if (view != null) {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }
    }
}