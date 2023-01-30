package `in`.tutorial.trelloclone.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.tutorial.trelloclone.R
import `in`.tutorial.trelloclone.adapters.TaskListItemsAdapter
import `in`.tutorial.trelloclone.databinding.ActivityTaskListBinding
import `in`.tutorial.trelloclone.firebase.FirestoreClass
import `in`.tutorial.trelloclone.models.Board
import `in`.tutorial.trelloclone.models.Task
import `in`.tutorial.trelloclone.utils.Constants

class TaskListActivity : BaseActivity() {
    var binding:ActivityTaskListBinding? = null
    private lateinit var mBoardDetails : Board
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        var boardDocumentId = ""
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardsDetails(this, boardDocumentId)
    }
    fun boardDetails(board: Board){
        hideProgressDialog()
        setupActionBar(board.name)
        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)
        binding?.rvTaskList?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding?.rvTaskList?.setHasFixedSize(true)
        val adapter = TaskListItemsAdapter(this, board.taskList)
        binding?.rvTaskList?.adapter = adapter
    }
    private fun setupActionBar(title:String){
        setSupportActionBar(binding?.toolbarTaskListActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = title
        }
        binding?.toolbarTaskListActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}