package `in`.tutorial.trelloclone.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import `in`.tutorial.trelloclone.databinding.ActivitySplashScreenBinding
import `in`.tutorial.trelloclone.firebase.FirestoreClass

class SplashScreen : AppCompatActivity() {
    var binding:ActivitySplashScreenBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
         window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        val typeFace: Typeface = Typeface.createFromAsset(assets, "carbon phyber.otf")
        binding?.tvAppName?.typeface = typeFace
        Handler(Looper.getMainLooper()).postDelayed({
            var currentID = FirestoreClass().getCurrentUserId()
            if(currentID.isNotEmpty()){
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                startActivity(Intent(this, IntroActivity::class.java))
            }
            finish()
        }, 100)
    }

}