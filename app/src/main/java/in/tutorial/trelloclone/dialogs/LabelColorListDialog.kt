package `in`.tutorial.trelloclone.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.tutorial.trelloclone.R
import `in`.tutorial.trelloclone.adapters.LabelColorListItemsAdapter
import `in`.tutorial.trelloclone.databinding.DialogListBinding

abstract class LabelColorListDialog(
    context: Context,
    private var list: ArrayList<String>,
    private val title: String = "",
    private val mSelectedColor:String = ""
) :Dialog(context){
    private var adapter: LabelColorListItemsAdapter? = null
    private var bindings: DialogListBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null)
        bindings = DialogListBinding.inflate(layoutInflater)
        setContentView(bindings?.root!!)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setupRecyclerView()
    }
    private fun setupRecyclerView(){
        bindings?.tvTitle?.text = title
        bindings?.rvList?.layoutManager = LinearLayoutManager(context)
        adapter = LabelColorListItemsAdapter(context, list, mSelectedColor)
        bindings?.rvList?.adapter = adapter
        adapter!!.setOnClickListener(object :LabelColorListItemsAdapter.OnClickListener{
            override fun onClick(position: Int, color: String) {
                dismiss()
                onItemSelected(color)
            }
        })
    }
    protected abstract fun onItemSelected(color:String)
}