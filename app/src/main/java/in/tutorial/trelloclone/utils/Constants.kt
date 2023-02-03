package `in`.tutorial.trelloclone.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher

object Constants {
    const val FCM_TOKEN_UPDATED: String = "fcmTokenUpdated"
    const val FCM_TOKEN: String = "fcmToken"
    const val EMAIL: String = "email"
    const val USERS: String = "users"
    const val BOARDS: String = "boards"
    const val IMAGE: String = "image"
    const val NAME : String = "name"
    const val MOBILE: String = "mobile"
    const val ASSIGNED_TO: String = "assignedTo"
    const val DOCUMENT_ID:String = "documentId"
    const val TASK_LIST:String = "taskList"
    const val BOARD_DETAIL:String = "board_detail"
    const val ID:String = "id"
//    const val CARD_DETAIL:String = "card_detail"
    const val TASK_LIST_ITEM_POSITION:String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION:String = "card_list_item_position"
    const val BOARD_MEMBERS_LIST:String = "board_members_list"
    const val SELECT:String = "Select"
    const val UN_SELECT:String = "UnSelect"
    const val PROJEMANAG_PREFERENCES = "ProjemanagePrefs"
    const val FCM_BASE_URL:String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION:String = "authorization"
    const val FCM_KEY:String = "key"
    const val FCM_SERVER_KEY:String = "AAAA370qdwc:APA91bGbXH81N3_p03LBbIJ1lJDQ0r2TvmcpElm7lhI5Z6mBosnywZ7b0MvERf03FvvitHOAIaCT6kBYsNhdY2tIL5FOJpWTBu2Z38S8LVYpQulS2Ncjqm04OWhd0UPeNFcSqkGWDSJd"
    const val FCM_KEY_TITLE:String = "title"
    const val FCM_KEY_MESSAGE:String = "message"
    const val FCM_KEY_DATA:String = "data"
    const val FCM_KEY_TO:String = "to"

    fun showImageChooser(resLauncher: ActivityResultLauncher<Intent>){
        val galIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resLauncher.launch(galIntent)
    }

    fun getFileExtension(activity: Activity, uri: Uri):String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri))
    }
}