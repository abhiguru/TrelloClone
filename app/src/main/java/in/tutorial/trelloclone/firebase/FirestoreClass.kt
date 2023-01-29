package `in`.tutorial.trelloclone.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import `in`.tutorial.trelloclone.activities.SignInActivity
import `in`.tutorial.trelloclone.activities.Signup
import `in`.tutorial.trelloclone.models.User
import `in`.tutorial.trelloclone.utils.Constants

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()
    fun registerUser(activity: Signup, userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener{
                e->
                Log.e(activity.javaClass.simpleName, "Error with registration creating document")
            }
    }
    fun getCurrentUserId():String{
        var currentUser = FirebaseAuth.getInstance().currentUser
        var curentUserID = ""
        currentUser?.let {
            curentUserID = currentUser.uid
        }
        return curentUserID
    }
    fun signInUser(activity: SignInActivity){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {document ->
                val loggedInUser = document.toObject(User::class.java)
                activity.signInSuccess(loggedInUser!!)
            }.addOnFailureListener{
                    e->
                Log.e(activity.javaClass.simpleName, "Error with getting data")
            }
    }
}