package `in`.tutorial.trelloclone.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.tutorial.trelloclone.R
import `in`.tutorial.trelloclone.activities.TaskListActivity
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
        val cvAddCard = binding?.cvAddCard
        val cvAddTaskListName = binding?.cvAddTaskListName
        val cvEditTaskListName = binding?.cvEditTaskListName
        val ibEditListName = binding?.ibEditListName
        val ibCloseListName = binding?.ibCloseListName
        val ibDoneListName = binding?.ibDoneListName
        val ibDoneEditListName = binding?.ibDoneEditListName
        val ibDoneCardName = binding?.ibDoneCardName
        val ibCloseCardName = binding?.ibCloseCardName
        val ibCloseEditableView = binding?.ibCloseEditableView
        val ibDeleteList = binding?.ibDeleteList
        val rvCardList = binding?.rvCardList

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
            holder.tvTaskListTitle?.text = model.title
            holder.tvAddTaskList?.setOnClickListener {
                holder.tvAddTaskList?.visibility = View.GONE
                holder.cvAddTaskListName?.visibility = View.VISIBLE
            }
            holder.ibCloseListName?.setOnClickListener {
                holder.tvAddTaskList?.visibility = View.VISIBLE
                holder.cvAddTaskListName?.visibility = View.GONE
            }
            holder.ibDoneListName?.setOnClickListener {
                val listName = holder.etTaskListName?.text.toString()
                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.createTaskList(listName)
                    }
                }else{
                    Toast.makeText(context, "Please enter list name",
                        Toast.LENGTH_SHORT).show()
                }
            }
            holder.ibEditListName?.setOnClickListener {
                holder.etEditTaskListName?.setText(model.title)
                holder.llTitleView?.visibility = View.GONE
                holder.cvEditTaskListName?.visibility = View.VISIBLE
            }
            holder.ibCloseEditableView?.setOnClickListener {
                holder.llTitleView?.visibility = View.VISIBLE
                holder.cvEditTaskListName?.visibility = View.GONE
            }
            holder.ibDoneEditListName?.setOnClickListener {
                val listName = holder.etEditTaskListName?.text.toString()
                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.updateTaskList(position, listName, model)
                    }
                }else{
                    Toast.makeText(context, "Please enter a list name",
                        Toast.LENGTH_SHORT).show()
                }
            }

            holder.ibDeleteList?.setOnClickListener {
                alertDialogForDeleteList(position, model.title)
            }

            holder.tvAddCard?.setOnClickListener {
                holder.tvAddCard?.visibility = View.GONE
                holder.cvAddCard?.visibility = View.VISIBLE
            }
            holder.ibCloseCardName?.setOnClickListener {
                holder.tvAddCard?.visibility = View.VISIBLE
                holder.cvAddCard?.visibility = View.GONE
            }

            holder.ibDoneCardName?.setOnClickListener {
                val cardName = holder.etCardName?.text.toString()
                if(cardName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.addCardToTaskList(position, cardName)
                    }
                }else{
                    Toast.makeText(context, "Please enter a card name",
                        Toast.LENGTH_SHORT).show()
                }
            }

            holder.rvCardList?.layoutManager =
                LinearLayoutManager(context)
            holder.rvCardList?.setHasFixedSize(true)
            val cardAdapter = CardListsItemsAdapter(context, model.cards)
            holder.rvCardList?.adapter = cardAdapter
            cardAdapter.setOnClickListener(object: CardListsItemsAdapter.OnClickListener{
                override fun onClick(cardPosition: Int) {
                    if(context is TaskListActivity){
                        context.cardDetails(position, cardPosition)
                    }
                }
            })
        }
    }
    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }
        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    private fun Int.toDp():Int= (this / Resources.getSystem().displayMetrics.density).toInt()
    private fun Int.toPx():Int= (this * Resources.getSystem().displayMetrics.density).toInt()
}