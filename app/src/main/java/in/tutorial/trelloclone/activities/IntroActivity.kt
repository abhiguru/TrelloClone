package `in`.tutorial.trelloclone.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import `in`.tutorial.trelloclone.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {
    var bindings: ActivityIntroBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = ActivityIntroBinding.inflate(layoutInflater)
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

        bindings?.btnSignUpIntro?.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }
        bindings?.btnSignInIntro?.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}