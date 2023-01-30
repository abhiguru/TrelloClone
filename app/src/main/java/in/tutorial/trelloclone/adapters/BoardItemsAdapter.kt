package `in`.tutorial.trelloclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import `in`.tutorial.trelloclone.R
import `in`.tutorial.trelloclone.databinding.ItemBoardBinding
import `in`.tutorial.trelloclone.models.Board

open class BoardItemsAdapter(
    private val context: Context,
    private val list:ArrayList<Board>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var onClickListener: OnClickListener? = null
    private class MyViewHolder(binding: ItemBoardBinding):RecyclerView.ViewHolder(binding.root) {
        val ivBoardImage = binding?.ivBoardImage
        val tvName = binding?.tvName
        val tvCreatedBy = binding?.tvCreatedBy
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            ItemBoardBinding.inflate(LayoutInflater
                                        .from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.ivBoardImage!!);
            holder.tvName!!.text = model.name
            holder.tvCreatedBy!!.text = "Created by : ${model.createdBy}"
            holder.itemView.setOnClickListener {
                if(onClickListener!=null){
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    interface OnClickListener{
        fun onClick(position:Int, model: Board)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
}
