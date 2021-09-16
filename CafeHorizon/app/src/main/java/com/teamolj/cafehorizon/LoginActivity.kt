package com.teamolj.cafehorizon

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.teamolj.cafehorizon.databinding.ActivityLoginBinding
import com.teamolj.cafehorizon.operation.InternetConnection
import com.teamolj.cafehorizon.signUp.SignUpActivity
import com.teamolj.cafehorizon.signUp.SignUpSocialActivity
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private var doubleBackPressed: Boolean = false
    private lateinit var closeToast: Toast

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var facebookCallbackManager: CallbackManager

    private val GOOGLE_SIGN_IN_CODE = 951753

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        val currentUser = auth.currentUser
        if(currentUser != null){
            // 비정상 경로 접근 차단
            if (App.prefs.getString("userNick", "") != "") {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            else
                auth.signOut()
        }

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
            closeKeyboard()

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
                val userId = binding.txtUserID.text.toString().trim()
                val email = userId + getString(R.string.email_domain)
                val password = binding.txtUserPwd.text.toString().trim()

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            App.prefs.setString("LoginType", "phoneNumber")
                            fetchUserInformation()
                        } else {
                            Toast.makeText(this, getString(R.string.toast_failed_verification), Toast.LENGTH_SHORT).show()
                            binding.btnLogin.isClickable = true
                        }
                    }
            }
        }

        binding.btnFindUserInfo.setOnClickListener {
            val intent = Intent(this, FindInfoActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // 소셜 로그인: 구글, 페이스북
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.btnGoogleLogin.setOnClickListener(loginWithGoogle)

        facebookCallbackManager = CallbackManager.Factory.create()
        binding.btnFacebookLogin.setOnClickListener(loginWithFacebook)
    }

    private val loginWithGoogle = View.OnClickListener {
        binding.btnGoogleLogin.isClickable = false
        binding.progressBar.visibility = View.VISIBLE

        val googleIntent = googleSignInClient.signInIntent
        startActivityForResult(googleIntent, GOOGLE_SIGN_IN_CODE)
    }

    private fun handleGoogleIdToken(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 기존 유저인지 신규 유저인지 확인
                    if (!auth.currentUser!!.phoneNumber.isNullOrEmpty()) {
                        App.prefs.setString("LoginType", "Google")
                        fetchUserInformation()
                    }
                    else {
                        // 구글 단독 계정 삭제
                        auth.currentUser!!.delete()

                        // SignUpSocialActivity에 credential 전달
                        val intent = Intent(this, SignUpSocialActivity::class.java)
                        intent.putExtra("authProvider", "Google")
                        intent.putExtra("googleToken", idToken)
                        startActivity(intent)
                    }
                } else {googleSignInClient.signOut()
                    Toast.makeText(applicationContext, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                    binding.btnGoogleLogin.isClickable = true
                    binding.progressBar.visibility = View.GONE
                }
            }
    }

    private val loginWithFacebook = View.OnClickListener {
        binding.btnFacebookLogin.isClickable = false
        binding.progressBar.visibility = View.VISIBLE

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_birthday"))
        LoginManager.getInstance().registerCallback(facebookCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                // 회원 생일 확인
                var fbUserBday: String?
                val fbRequest = GraphRequest.newMeRequest(result.accessToken) { json, response ->
                    fbUserBday = if (response?.error != null)
                        null
                    else
                        json?.opt("birthday").toString()

                    handleFacebookAccessToken(result.accessToken, fbUserBday)
                }
                val parameters = Bundle()
                parameters.putString("fields", "birthday")
                fbRequest.parameters = parameters
                fbRequest.executeAsync()
            }

            override fun onCancel() {
                LoginManager.getInstance().logOut()
                binding.btnFacebookLogin.isClickable = true
                binding.progressBar.visibility = View.GONE
            }

            override fun onError(error: FacebookException?) {
                LoginManager.getInstance().logOut()
                Toast.makeText(applicationContext, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                binding.btnFacebookLogin.isClickable = true
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken, fbUserBday: String?) {
        val credential = FacebookAuthProvider.getCredential(token.token)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 기존 유저인지 신규(또는 페이스북 신규 연동) 유저인지 확인
                    if (!auth.currentUser!!.phoneNumber.isNullOrEmpty()) {
                        // 로그인 절차 수행
                        App.prefs.setString("LoginType", "Facebook")
                        fetchUserInformation()
                    }
                    else {
                        // 페이스북 단독 계정 삭제
                        auth.currentUser!!.delete()

                        // SignUpSocialActivity에 credential 전달: 전화번호 로그인 후 계정 링크 위함
                        val intent = Intent(this, SignUpSocialActivity::class.java)
                        intent.putExtra("authProvider", "Facebook")
                        intent.putExtra("facebookToken", token.token)
                        intent.putExtra("facebookUserBday", fbUserBday)
                        startActivity(intent)
                    }
                } else {
                    LoginManager.getInstance().logOut()
                    Toast.makeText(applicationContext, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                    binding.btnFacebookLogin.isClickable = true
                    binding.progressBar.visibility = View.GONE
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
                    LoginManager.getInstance().logOut()
                    Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                    binding.btnLogin.isClickable = true
                    binding.progressBar.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                Firebase.auth.signOut()
                LoginManager.getInstance().logOut()
                Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                binding.btnLogin.isClickable = true
                binding.progressBar.visibility = View.GONE
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result from Google
        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful
                val account = task.getResult(ApiException::class.java)!!
                handleGoogleIdToken(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed
                Toast.makeText(applicationContext, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                binding.btnGoogleLogin.isClickable = true
                binding.progressBar.visibility = View.GONE
            }
        }
        // Result from Facebook
        else {
            facebookCallbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }
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