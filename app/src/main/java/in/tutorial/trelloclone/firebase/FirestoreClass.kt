package `in`.tutorial.trelloclone.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import `in`.tutorial.trelloclone.activities.*
import `in`.tutorial.trelloclone.models.Board
import `in`.tutorial.trelloclone.models.User
import `in`.tutorial.trelloclone.utils.Constants

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()
    fun registerUser(activity: Signup, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error with registration creating document")
            }
    }
    fun getCurrentUserId(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var curentUserID = ""
        currentUser?.let {
            curentUserID = currentUser.uid
        }
        return curentUserID
    }
    fun loadUserData(activity: Activity, readBoardsList: Boolean = false) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)
                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser!!)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser!!, readBoardsList)
                    }
                    is MyProfileActivity->{
                        activity.setUserDataInUI(loggedInUser!!)
                    }
                }

            }.addOnFailureListener { e ->
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error with getting data")
            }
    }
    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Profile Data updated succesfully")
                Toast.makeText(activity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
                activity.hideProgressDialog()
            }.addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error in profile update")
                Toast.makeText(activity, "Error in Profile updated", Toast.LENGTH_SHORT).show()
            }
    }
    fun createBoard(activity: CreateBoard, board: Board){
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Board created success!")
                Toast.makeText(activity, "Board created success!", Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Board created failed! ${it.message}")
                Toast.makeText(activity, "Board created failed! ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
    fun getBoardsList(activity: MainActivity){
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document ->
                Log.e("Firestore getBoards", document.documents.toString())
                val boardList: ArrayList<Board> = ArrayList()
                for(i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardList.add(board)
                }
                activity.populateBoardsListToUI(boardList)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e("Firestore getBoards", it.message.toString())
            }
    }
    fun getBoardsDetails(taskListActivity: TaskListActivity, boardDocumentId: String) {
        mFireStore.collection(Constants.BOARDS)
            .document(boardDocumentId)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.e("Firestore getBoards", document.toString())
                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id
                taskListActivity.boardDetails(board)
            }
            .addOnFailureListener {
                taskListActivity.hideProgressDialog()
                Log.e("Firestore getBoards", it.message.toString())
            }
    }
}