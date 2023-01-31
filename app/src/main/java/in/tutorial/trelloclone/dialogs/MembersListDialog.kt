package `in`.tutorial.trelloclone.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.tutorial.trelloclone.R
import `in`.tutorial.trelloclone.adapters.MemberListItemsAdapter
import `in`.tutorial.trelloclone.databinding.DialogListBinding
import `in`.tutorial.trelloclone.models.User

abstract class MembersListDialog(
    context: Context,
    private var list: ArrayList<User>,
    private val title: String = ""
) : Dialog(context) {

    private var adapter: MemberListItemsAdapter? = null
    private var bindings: DialogListBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null)
        bindings = DialogListBinding.inflate(layoutInflater)
        setContentView(bindings?.root!!)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        bindings?.tvTitle?.text = title
        if (list.size > 0) {
            bindings?.rvList?.layoutManager = LinearLayoutManager(context)
            adapter = MemberListItemsAdapter(context, list)
            bindings?.rvList?.adapter = adapter
            adapter!!.setOnClickListener(object :
                MemberListItemsAdapter.OnClickListener {
                override fun onClick(position: Int, user: User, action:String) {
                    dismiss()
                    onItemSelected(user, action)
                }
            })
        }
    }

    protected abstract fun onItemSelected(user: User, action:String)
}
// END