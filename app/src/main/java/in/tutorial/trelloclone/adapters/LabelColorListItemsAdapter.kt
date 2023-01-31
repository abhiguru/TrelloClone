package `in`.tutorial.trelloclone.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.tutorial.trelloclone.databinding.ItemLabelColorBinding

class LabelColorListItemsAdapter(
    private val context:Context,
    private var list: ArrayList<String>,
    private val mSelectedColor:String
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    class MyViewHolder(binding: ItemLabelColorBinding):RecyclerView.ViewHolder(binding.root){
        val ivSelectedColor = binding?.ivSelectedColor
        val flMain = binding?.flMain
        val viewMain = binding?.viewMain
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LabelColorListItemsAdapter.MyViewHolder(
            ItemLabelColorBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            holder.viewMain?.setBackgroundColor(Color.parseColor(model))
            if(model == mSelectedColor){
                holder.ivSelectedColor?.visibility = View.VISIBLE
            }else{
                holder.ivSelectedColor?.visibility = View.GONE
            }
            holder.itemView.setOnClickListener {
                if(onClickListener!=null){
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    fun setOnClickListener(onClickListener:OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position:Int, color:String)
    }
}