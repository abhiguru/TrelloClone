package `in`.tutorial.trelloclone.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import `in`.tutorial.trelloclone.R
import `in`.tutorial.trelloclone.databinding.ActivityCreateBoardBinding
import `in`.tutorial.trelloclone.firebase.FirestoreClass
import `in`.tutorial.trelloclone.models.Board
import `in`.tutorial.trelloclone.utils.Constants
import java.io.IOException

class CreateBoard : BaseActivity() {
    var binding : ActivityCreateBoardBinding? = null
    private var mSelImageFileUri : Uri? = null
    private lateinit var mUserName : String
    private var mBoardImageURL : String = ""
    companion object{
        private const val READ_EXT_STORE = 1
    }
    var resLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){
            val intent : Intent? = it.data
            mSelImageFileUri = intent?.data
            try {
                Glide
                    .with(this@CreateBoard)
                    .load(mSelImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding?.ivBoardImage!!);
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()
        if(intent.hasExtra(Constants.NAME)){
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }
        binding?.ivBoardImage?.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(resLauncher)
            }else{
                ActivityCompat.requestPermissions(this@CreateBoard,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXT_STORE)
            }
        }
        binding?.btnCreate?.setOnClickListener {
            if(mSelImageFileUri!=null) {
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }
    }
    private fun createBoard(){
        val assignedUserArrayList : ArrayList<String> = ArrayList()
        assignedUserArrayList.add(getCurrentUserID())
        var board = Board(
            name = binding?.etBoardName?.text.toString(),
            image = mBoardImageURL,
            createdBy = mUserName,
            assignedTo = assignedUserArrayList
        )
        FirestoreClass().createBoard(this@CreateBoard, board)
    }
    private fun uploadBoardImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        mSelImageFileUri?.let {
            val sRef : StorageReference =
                FirebaseStorage.getInstance().reference.child(
                    "BOARD_IMAGE"+System.currentTimeMillis()
                            +"."+Constants.getFileExtension(this@CreateBoard, it))
            Log.e("StorageException", "Data $it")
            sRef.putFile(it).addOnSuccessListener {
                    takeSnap ->
                hideProgressDialog()
                Log.e("Board Image URL",
                    takeSnap.metadata!!.reference!!.downloadUrl.toString())
                takeSnap.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri->
                    mBoardImageURL = uri.toString()
                    Log.e("Firebase Image URL2", uri.toString())
                    createBoard()
                }
            }.addOnFailureListener {
                    exception ->
                Toast.makeText(
                    this@CreateBoard,
                    "Error uploading image ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                hideProgressDialog()
            }
        }
    }
    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_EXT_STORE ) {
            if(grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
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

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarCreateBoardActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.create_board_title)
        }
        binding?.toolbarCreateBoardActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}