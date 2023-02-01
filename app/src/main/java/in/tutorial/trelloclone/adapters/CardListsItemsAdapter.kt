package `in`.tutorial.trelloclone.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.tutorial.trelloclone.activities.TaskListActivity
import `in`.tutorial.trelloclone.databinding.ItemCardBinding
import `in`.tutorial.trelloclone.models.Board
import `in`.tutorial.trelloclone.models.Card
import `in`.tutorial.trelloclone.models.SelectedMembers

open class CardListsItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Card>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    class MyViewHolder(binding: ItemCardBinding):RecyclerView.ViewHolder(binding.root){
        val viewLabelColor = binding?.viewLabelColor
        val tvCardName = binding?.tvCardName
        val rvCardSelectedMembersList = binding?.rvCardSelectedMembersList
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
            if((context as TaskListActivity).mAssignedMemberDetailList.size > 0){
                val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()
                for(i in context.mAssignedMemberDetailList.indices){
                    for(j in model.assignedTo){
                        if(context.mAssignedMemberDetailList[i].id == j){
                            val selectedMembers = SelectedMembers(
                                context.mAssignedMemberDetailList[i].id,
                                context.mAssignedMemberDetailList[i].image
                            )
                            selectedMembersList.add(selectedMembers)
                        }
                    }
                }
                if(selectedMembersList.size>0){
                    // No display if the user himself is the only assigned to
                    if(selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy){
                        holder.rvCardSelectedMembersList!!.visibility = View.GONE
                    }else{
                        holder.rvCardSelectedMembersList!!.visibility = View.VISIBLE
                        // Max 4 icons
                        holder.rvCardSelectedMembersList!!.layoutManager = GridLayoutManager(context, 4)
                        val adapter = CardMemberListItemsAdapter(
                                    context, selectedMembersList, false)
                        holder.rvCardSelectedMembersList!!.adapter = adapter
                        adapter.setOnClickListener(object: CardMemberListItemsAdapter.OnClickListener{
                            override fun onClick() {
                             if(onClickListener!=null){
                                 onClickListener!!.onClick(position)
                             }
                            }
                        })
                    }
                }else{
                    holder.rvCardSelectedMembersList!!.visibility = View.GONE
                }

            }
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