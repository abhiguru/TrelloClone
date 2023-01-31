package `in`.tutorial.trelloclone.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.tutorial.trelloclone.databinding.ItemCardBinding
import `in`.tutorial.trelloclone.models.Board
import `in`.tutorial.trelloclone.models.Card

open class CardListsItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Card>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    class MyViewHolder(binding: ItemCardBinding):RecyclerView.ViewHolder(binding.root){
        val viewLabelColor = binding?.viewLabelColor
        val tvCardName = binding?.tvCardName
        val tvMembersName = binding?.tvMembersName
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun getItemCount(): Int {
        return list.size
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            holder.tvCardName?.text = model.name
            if(model.labelColor.isNotEmpty()){
                holder.viewLabelColor?.visibility = View.VISIBLE
                holder.viewLabelColor?.setBackgroundColor(Color.parseColor(model.labelColor))
            }else{
                holder.viewLabelColor?.visibility = View.GONE
            }
            holder.itemView.setOnClickListener {
                if(onClickListener!=null){
                    onClickListener!!.onClick(position)
                }
            }
        }
    }
    fun setOnClickListener(onClickListener:OnClickListener){
        this.onClickListener = onClickListener
    }
    interface OnClickListener{
        fun onClick(position:Int)
    }
}