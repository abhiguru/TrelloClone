package `in`.tutorial.trelloclone.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import `in`.tutorial.trelloclone.R
import `in`.tutorial.trelloclone.databinding.ActivityMyProfileBinding
import `in`.tutorial.trelloclone.firebase.FirestoreClass
import `in`.tutorial.trelloclone.models.User
import `in`.tutorial.trelloclone.utils.Constants
import java.io.IOException

class MyProfileActivity : BaseActivity() {
    var binding:ActivityMyProfileBinding? = null
    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }
    private var mSelImageFileUri : Uri? = null
    private var mProfileImageURL : String = ""
    private lateinit var mUserDetails:User

    private val resLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == Activity.RESULT_OK){
            val intent : Intent? = result.data
            mSelImageFileUri = intent?.data
            try {
                Glide
                    .with(this@MyProfileActivity)
                    .load(mSelImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding?.ivUserProfileImage!!);
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()
        FirestoreClass().loadUserData(this)
        binding?.ivUserProfileImage?.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(resLauncher)
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE)
            }
        }
        binding?.btnUpdate?.setOnClickListener {
            if(mSelImageFileUri!=null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(resLauncher)
            }
        }else{
            Toast.makeText(
                this,
                "You denied permissions required",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        mSelImageFileUri?.let {
            val sRef : StorageReference =
                FirebaseStorage.getInstance().reference.child(
                    "USER_IMAGE"+System.currentTimeMillis()
                            +"."+Constants.getFileExtension(this@MyProfileActivity, it))
            Log.e("StorageException", "Data $it")
            sRef.putFile(it).addOnSuccessListener {
                takeSnap ->
                hideProgressDialog()
                Log.e("Firebase Image URL",
                    takeSnap.metadata!!.reference!!.downloadUrl.toString())
                takeSnap.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    mProfileImageURL = uri.toString()
                    Log.e("Firebase Image URL2", uri.toString())
                    updateUserProfileData()
                }
            }.addOnFailureListener {
                exception ->
                Toast.makeText(
                    this@MyProfileActivity,
                    "Error uploading image ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                hideProgressDialog()
            }
        }

    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarMyProfileActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)
        }
        binding?.toolbarMyProfileActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    fun setUserDataInUI(user: User){
        mUserDetails = user
        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding?.ivUserProfileImage!!);
        binding?.etName?.setText(user.name)
        binding?.etEmail?.setText(user.email)
        if(user.mobile != 0L){
            binding?.etMobile?.setText(user.mobile.toString())
        }
    }

    private fun updateUserProfileData(){
        val userHashMap = HashMap<String, Any>()

        if(mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image){
            userHashMap[Constants.IMAGE] = mProfileImageURL
        }
        if(binding?.etName?.text.toString() != mUserDetails.name){
            userHashMap[Constants.NAME] = binding?.etName?.text.toString()
        }
        if(binding?.etMobile?.text.toString() != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = binding?.etMobile?.text.toString().toLong()
        }
        FirestoreClass().updateUserProfileData(this, userHashMap)
    }
}