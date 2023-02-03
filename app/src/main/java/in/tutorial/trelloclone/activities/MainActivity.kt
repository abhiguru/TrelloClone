package `in`.tutorial.trelloclone.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import `in`.tutorial.trelloclone.R
import `in`.tutorial.trelloclone.adapters.BoardItemsAdapter
import `in`.tutorial.trelloclone.databinding.ActivityMainBinding
import `in`.tutorial.trelloclone.databinding.NavHeaderMainBinding
import `in`.tutorial.trelloclone.firebase.FirestoreClass
import `in`.tutorial.trelloclone.models.Board
import `in`.tutorial.trelloclone.models.User
import `in`.tutorial.trelloclone.utils.Constants
import java.io.IOException

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    var binding : ActivityMainBinding? = null
    private lateinit var mUsername:String
    private lateinit var mSharedPreferences: SharedPreferences
    companion object{
        const val MY_PROFILE_REQ_CODE: Int = 11
    }
    var createBoardResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){
            val intent : Intent? = it.data
            FirestoreClass().getBoardsList(this@MainActivity)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()
        binding?.navView?.setNavigationItemSelectedListener(this)
        mSharedPreferences = this.getSharedPreferences(Constants.PROJEMANAG_PREFERENCES,
            Context.MODE_PRIVATE)
        FirestoreClass().loadUserData(this, true)
        val tokenUpdated =
            mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)
        if(tokenUpdated){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().loadUserData(this, true)
        }else{
            FirebaseMessaging.getInstance().token.addOnSuccessListener(this@MainActivity){
                updateFCMToken(it)
            }
        }
        binding?.appBarMain?.fabCreateBoard?.setOnClickListener {
            var intent = Intent(this, CreateBoard::class.java)
            intent.putExtra(Constants.NAME, mUsername)
            createBoardResultLauncher.launch(intent)
        }
    }
    private fun setupActionBar(){
        setSupportActionBar(binding?.appBarMain?.toolbarMainActivity)
        binding?.appBarMain?.toolbarMainActivity?.setNavigationIcon(R.drawable.ic_action_nav_menu)
        binding?.appBarMain?.toolbarMainActivity?.setNavigationOnClickListener {
            toggleDrawer()
        }
    }
    private fun toggleDrawer(){
        if(binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true){
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }else{
            binding?.drawerLayout?.openDrawer(GravityCompat.START)
        }
    }
    override fun onBackPressed() {
        if(binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true){
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile->{
                val intent = Intent(this, MyProfileActivity::class.java)
                resLauncher.launch(intent)
            }
            R.id.nav_sign_out->{
                FirebaseAuth.getInstance().signOut()
                mSharedPreferences.edit().clear().apply()
                val intent = Intent(this, IntroActivity::class.java)
                // Clear activity stack
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }
    var resLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode == Activity.RESULT_OK){
            val intent :Intent? = result.data
            FirestoreClass().loadUserData(this)
        }else{
            Log.e("Main activity ", "cancelled")
        }
    }
    fun updateNavigationUserDetails(loggedInUser: User, readBoardsList: Boolean) {
        hideProgressDialog()
        var headerMainBinding:NavHeaderMainBinding? = null
        headerMainBinding = NavHeaderMainBinding.bind(binding?.navView!!.getHeaderView(0))
        Glide
            .with(this)
            .load(loggedInUser.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(headerMainBinding?.navUserImage!!);
        mUsername = loggedInUser.name
        headerMainBinding?.tvUsername?.text = loggedInUser.name
        if(readBoardsList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)
        }
    }
    fun populateBoardsListToUI(boardList: ArrayList<Board>){
        hideProgressDialog()
        if(boardList.isNotEmpty()){
            binding?.appBarMain?.contentMainInclude!!.rvBoardsList.visibility = View.VISIBLE
            binding?.appBarMain?.contentMainInclude!!.tvNoBoardsAvailable.visibility = View.GONE
            binding?.appBarMain?.contentMainInclude!!.rvBoardsList.layoutManager =
                LinearLayoutManager(this)
            binding?.appBarMain?.contentMainInclude!!.rvBoardsList.setHasFixedSize(true)
            val adapter = BoardItemsAdapter(this, boardList)
            binding?.appBarMain?.contentMainInclude!!.rvBoardsList.adapter = adapter
            adapter.setOnClickListener(object: BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })
        }else{
            binding?.appBarMain?.contentMainInclude!!.rvBoardsList.visibility = View.GONE
            binding?.appBarMain?.contentMainInclude!!.tvNoBoardsAvailable.visibility = View.VISIBLE
        }
    }
    fun tokenUpdateSuccess() {
        hideProgressDialog()
        val editor:SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)
        editor.apply()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this, true)
    }
    private fun updateFCMToken(token:String){
        val userHashMap = HashMap<String, Any>()
        userHashMap[Constants.FCM_TOKEN] = token
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

}