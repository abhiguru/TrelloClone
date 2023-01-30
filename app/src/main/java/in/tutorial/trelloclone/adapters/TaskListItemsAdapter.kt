package `in`.tutorial.trelloclone.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import `in`.tutorial.trelloclone.R
import `in`.tutorial.trelloclone.databinding.ActivityTaskListBinding
import `in`.tutorial.trelloclone.databinding.ItemBoardBinding
import `in`.tutorial.trelloclone.databinding.ItemTaskBinding
import `in`.tutorial.trelloclone.models.Task

open class TaskListItemsAdapter(
    private val context: Context,
    private val list: ArrayList<Task>
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private class MyViewHolder(binding: ItemTaskBinding)
        :RecyclerView.ViewHolder(binding.root) {
        val tvAddTaskList = binding?.tvAddTaskList
        val tvTaskListTitle = binding?.tvTaskListTitle
        val tvAddCard = binding?.tvAddCard
        val etTaskListName = binding?.etTaskListName
        val etEditTaskListName = binding?.etEditTaskListName
        val etCardName = binding?.etCardName
        val llTaskItem = binding?.llTaskItem
        val llTitleView = binding?.llTitleView
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width*0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(
            (15.toDp().toPx()),0,(40.toDp()).toPx(), 0)
        view.layoutParams = layoutParams
        return MyViewHolder(ItemTaskBinding.bind(view))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if(position == list.size -1){
                holder.tvAddTaskList?.visibility = View.VISIBLE
                holder.llTaskItem?.visibility = View.GONE
            }else{
                holder.tvAddTaskList?.visibility = View.GONE
                holder.llTaskItem?.visibility = View.VISIBLE
            }
        }
    }

    private fun Int.toDp():Int= (this / Resources.getSystem().displayMetrics.density).toInt()
    private fun Int.toPx():Int= (this * Resources.getSystem().displayMetrics.density).toInt()
}