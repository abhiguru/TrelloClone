package `in`.tutorial.trelloclone.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import `in`.tutorial.trelloclone.R
import `in`.tutorial.trelloclone.databinding.ActivitySignupBinding
import `in`.tutorial.trelloclone.firebase.FirestoreClass
import `in`.tutorial.trelloclone.models.User

class Signup : BaseActivity() {
    var bindings:ActivitySignupBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(bindings?.root)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setupActionBar()
    }

    private fun setupActionBar(){
        setSupportActionBar(bindings?.toolbarSignUpActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        bindings?.toolbarSignUpActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() }
        bindings?.btnSignUp?.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser(){
        val name : String = bindings?.etName?.text.toString().trim{it <= ' '}
        val email : String = bindings?.etEmail?.text.toString().trim{it <= ' '}
        val password : String = bindings?.etPassword?.text.toString().trim{it <= ' '}

        if(validateForm(name, email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val regEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid, name, regEmail)
                        FirestoreClass().registerUser(this, user)
                    } else {
                        Toast.makeText(
                            this,
                            "User $name has registered with ${task.exception!!.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun validateForm(name:String, email:String, password:String): Boolean {
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter a email")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter a password")
                false
            }else->{ true }
        }
    }

    fun userRegisteredSuccess() {
        Toast.makeText(
            this,
            "User has registered success",
            Toast.LENGTH_SHORT
        ).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }
}