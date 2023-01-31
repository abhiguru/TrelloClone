package `in`.tutorial.trelloclone.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher

object Constants {
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

    fun showImageChooser(resLauncher: ActivityResultLauncher<Intent>){
        val galIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resLauncher.launch(galIntent)
    }

    fun getFileExtension(activity: Activity, uri: Uri):String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri))
    }
}