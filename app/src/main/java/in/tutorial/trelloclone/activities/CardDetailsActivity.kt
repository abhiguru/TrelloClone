package `in`.tutorial.trelloclone.activities

import android.app.Activity
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AlertDialog
import `in`.tutorial.trelloclone.R
import `in`.tutorial.trelloclone.databinding.ActivityCardDetailsBinding
import `in`.tutorial.trelloclone.dialogs.LabelColorListDialog
import `in`.tutorial.trelloclone.dialogs.MembersListDialog
import `in`.tutorial.trelloclone.firebase.FirestoreClass
import `in`.tutorial.trelloclone.models.Board
import `in`.tutorial.trelloclone.models.Card
import `in`.tutorial.trelloclone.models.Task
import `in`.tutorial.trelloclone.models.User
import `in`.tutorial.trelloclone.utils.Constants

class CardDetailsActivity : BaseActivity() {
    private var mSelColor: String = ""
    var binding: ActivityCardDetailsBinding? = null
    private lateinit var mBoardDetails: Board
    private var mTaskListPosition:Int = -1
    private var mCardPosition:Int = -1
    private lateinit var mMemberDetailList:ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        getIntentData()
        setupActionBar()
        binding?.etNameCardDetails?.setText(mBoardDetails.taskList[mTaskListPosition]
                                                .cards[mCardPosition].name)
        binding?.etNameCardDetails?.setSelection(binding?.etNameCardDetails!!.text.toString().length)
        binding?.btnUpdateCardDetails?.setOnClickListener {
            if(binding?.etNameCardDetails?.text.toString().isNotEmpty()) {
                this.updateCardDetails()
            }else{
                Toast.makeText(this, "Enter a card name",
                    Toast.LENGTH_SHORT).show()
            }
        }
        binding?.tvSelectLabelColor?.setOnClickListener {
            labelColorsListDialog()
        }
        mSelColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if(mSelColor.isNotEmpty()){
            setColor()
        }
        binding?.tvSelectMembers?.setOnClickListener {
            memberListDialog()
        }
    }
    private fun memberListDialog(){
        var cardAssignedMembersList =
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo
        if(cardAssignedMembersList.size> 0){
            for(i in mMemberDetailList.indices){
                for(j in cardAssignedMembersList){
                    if(mMemberDetailList[i].id == j){
                        mMemberDetailList[i].selected = true
                    }
                }
            }
        }else{
            for(i in mMemberDetailList.indices){
                mMemberDetailList[i].selected = false
            }
        }
        val dialog = object:MembersListDialog(
            this,
            mMemberDetailList,
            resources.getString(R.string.select_members) ){
            override fun onItemSelected(user: User, action: String) {
                // TODO implement select members
            }
        }.show()
    }
    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarCardDetailsActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name
        }
        binding?.toolbarCardDetailsActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }
    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL, Board::class.java)!!
            }else{
                @Suppress("DEPRECATION")
                mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
            }
            if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
                mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
            }
            if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
                mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
            }
            if(intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
                mMemberDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
            }
        }
    }
    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
    private fun colorsList():ArrayList<String>{
        val colorsList:ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")
        return colorsList
    }
    private fun setColor(){
        binding?.tvSelectLabelColor?.text = ""
        binding?.tvSelectLabelColor?.setBackgroundColor(Color.parseColor(mSelColor))
    }
    private fun labelColorsListDialog(){
        val colorsList: ArrayList<String> = colorsList()
        val listDialog = object: LabelColorListDialog(
            this,
            colorsList,
            resources.getString(R.string.str_select_label_color),
            mSelColor
        ){
            override fun onItemSelected(color: String) {
                mSelColor = color
                setColor()
            }
        }
        listDialog.show()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card->{
                alertDialogForDeleteCard(
                    mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun alertDialogForDeleteCard(cardName: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.alert))
        //set message for alert dialog
        builder.setMessage(
            resources.getString(
                R.string.confirmation_message_to_delete_card,
                cardName
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
            // START
            deleteCard()
            // END
        }
        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }
    private fun updateCardDetails(){
        val card = Card(
            binding?.etNameCardDetails?.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelColor
        )
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }
    private fun deleteCard(){
        val cardList:ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        cardList.removeAt(mCardPosition)
        val taskList:ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)
        taskList[mTaskListPosition].cards = cardList

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }
}