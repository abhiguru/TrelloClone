package `in`.tutorial.trelloclone.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.tutorial.trelloclone.R
import `in`.tutorial.trelloclone.adapters.TaskListItemsAdapter
import `in`.tutorial.trelloclone.databinding.ActivityTaskListBinding
import `in`.tutorial.trelloclone.firebase.FirestoreClass
import `in`.tutorial.trelloclone.models.Board
import `in`.tutorial.trelloclone.models.Card
import `in`.tutorial.trelloclone.models.Task
import `in`.tutorial.trelloclone.models.User
import `in`.tutorial.trelloclone.utils.Constants

class TaskListActivity : BaseActivity() {
    var binding:ActivityTaskListBinding? = null
    companion object{
        const val MEMBERS_REQUEST_CODE : Int = 13
    }
    private lateinit var mAssignedMemberDetailList: ArrayList<User>
    private lateinit var mBoardDetails : Board
    private lateinit var boardDocumentId:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        boardDocumentId = ""
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardsDetails(this, boardDocumentId)
    }
    fun boardDetails(board: Board){
        mBoardDetails = board
        hideProgressDialog()
        setupActionBar()
        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)
        binding?.rvTaskList?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding?.rvTaskList?.setHasFixedSize(true)
        val adapter = TaskListItemsAdapter(this, board.taskList)
        binding?.rvTaskList?.adapter = adapter
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(
            this@TaskListActivity, mBoardDetails.assignedTo)
    }
    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarTaskListActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.name
        }
        binding?.toolbarTaskListActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardsDetails(this, mBoardDetails.documentId)
    }
    fun createTaskList(tasklistName: String){
        val task = Task(tasklistName, FirestoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0, task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1 )
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }
    fun updateTaskList(position:Int, tasklistName: String, model:Task){
        val task = Task(tasklistName, model.createdBy)
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1 )
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }
    fun deleteTaskList(position:Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1 )
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }
    fun addCardToTaskList(position: Int, cardName:String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1 )
        val cardAssignedUserList: ArrayList<String> = ArrayList()
        cardAssignedUserList.add(FirestoreClass().getCurrentUserId())
        val card = Card(cardName, FirestoreClass().getCurrentUserId(), cardAssignedUserList)
        val cardsList = mBoardDetails.taskList[position].cards
        cardsList.add(card)
        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList
        )
        mBoardDetails.taskList[position] = task
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }
    var cardDetailsResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){
            showProgressDialog(resources.getString(R.string.please_wait))
            Log.e("Cancelled", "Cancelled Not")
            FirestoreClass().getBoardsDetails(this, boardDocumentId)
        }else{
            Log.e("Cancelled", "Cancelled")
        }
    }
    fun cardDetails(taskListPosition: Int, cardPosition:Int){
        val intent = Intent(this@TaskListActivity, CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION, taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST, mAssignedMemberDetailList)
        cardDetailsResultLauncher.launch(intent)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }
    var resLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsDetails(this, boardDocumentId)
        }else{
            Log.e("Cancelled", "Cancelled")
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members-> {
                val intent = Intent(this, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
                resLauncher.launch(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun boardMembersDetailsList(list:ArrayList<User>){
        mAssignedMemberDetailList = list
        hideProgressDialog()
    }
}