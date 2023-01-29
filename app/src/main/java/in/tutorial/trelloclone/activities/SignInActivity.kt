package `in`.tutorial.trelloclone.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedDispatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import `in`.tutorial.trelloclone.R
import `in`.tutorial.trelloclone.databinding.ActivitySignInBinding
import `in`.tutorial.trelloclone.firebase.FirestoreClass
import `in`.tutorial.trelloclone.models.User

class SignInActivity : BaseActivity() {
    var bindings:ActivitySignInBinding? = null;
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(bindings?.root)
        auth = FirebaseAuth.getInstance()
        setupToolbar()
        bindings?.btnSignIn?.setOnClickListener {
            signInCurrentUser()
        }
    }
    private fun signInCurrentUser(){
        val email:String = bindings?.etEmailSignin?.text.toString().trim(){it <= ' '}
        val password:String = bindings?.etPasswordSignin?.text.toString().trim(){it <= ' '}

        if(validateForm(email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        FirestoreClass().signInUser(this)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Authentication failed. ${task.exception}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }
    private fun validateForm( email:String, password:String): Boolean {
        return when{
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

    private fun setupToolbar(){
        setSupportActionBar(bindings?.toolbarSignInActivity)
        var actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        bindings?.toolbarSignInActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun signInSuccess(loggedInUser: User) {
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}