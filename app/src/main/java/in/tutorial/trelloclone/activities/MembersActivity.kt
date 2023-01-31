package `in`.tutorial.trelloclone.activities

import android.app.Activity
import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.tutorial.trelloclone.R
import `in`.tutorial.trelloclone.adapters.MemberListItemsAdapter
import `in`.tutorial.trelloclone.databinding.ActivityMembersBinding
import `in`.tutorial.trelloclone.firebase.FirestoreClass
import `in`.tutorial.trelloclone.models.Board
import `in`.tutorial.trelloclone.models.User
import `in`.tutorial.trelloclone.utils.Constants

class MembersActivity : BaseActivity() {
    private lateinit var mBoardDetails: Board
    private lateinit var mAssignedMembersList: ArrayList<User>
    private var anyChangesMade:Boolean = false
    var binding: ActivityMembersBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL, Board::class.java)!!

            }else{
                @Suppress("DEPRECATION")
                mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
            }
        }
        setupActionBar()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(
            this@MembersActivity, mBoardDetails.assignedTo)
    }
    fun setupMembersList(list:ArrayList<User>){
        mAssignedMembersList = list
        hideProgressDialog()
        binding?.rvMembersList?.layoutManager = LinearLayoutManager(this)
        binding?.rvMembersList?.setHasFixedSize(true)
        val adapter = MemberListItemsAdapter(this, list)
        binding?.rvMembersList?.adapter = adapter
    }
    fun memberDetails(user:User){
        mBoardDetails.assignedTo.add(user.id)
        FirestoreClass().assignMemberToBoard(this@MembersActivity, mBoardDetails, user)
    }
    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarMembersActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.members)
        }
        binding?.toolbarMembersActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member->{
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun dialogSearchMember(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        val tvAdd = dialog.findViewById<TextView>(R.id.tv_add)
        tvAdd.setOnClickListener {
            val etEmail = dialog.findViewById<EditText>(R.id.et_email_search_member)
            val email = etEmail.text.toString()
            if(email.isNotEmpty()){
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this@MembersActivity, email)
            }else{
                Toast.makeText(this@MembersActivity,
                    "Enter email", Toast.LENGTH_SHORT).show()
            }
        }
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    fun memberAssignSuccess(user: User){
        hideProgressDialog()
        mAssignedMembersList.add(user)
        anyChangesMade = true
        setupMembersList(mAssignedMembersList)
    }

    override fun onBackPressed() {
        Log.e("Cancelled2", "Cancelled2 $anyChangesMade")
        if(anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }
}