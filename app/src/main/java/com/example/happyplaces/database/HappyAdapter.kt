package com.example.happyplaces.database

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.activities.HappyPlaceDetail
import com.example.happyplaces.databinding.HappyRecycleBinding
import kotlinx.android.synthetic.main.happy_recycle.view.*

class HappyAdapter(private val items: ArrayList<HappyEntity>,

                   private val deleteListener:(id:Int)->Unit)


    :RecyclerView.Adapter<HappyAdapter.ViewHolder>(){

        inner class ViewHolder(binding: HappyRecycleBinding)
            :RecyclerView.ViewHolder(binding.root){

                val llMain = binding!!.llMain
                var ivRecycleImage = binding!!.ivRecycleImage
                val tvRecycle1 = binding!!.tvRecycle1
                val tvRecycle2 = binding!!.tvRecycle2
                val ivDelete = binding!!.ivDelete


            init {
              itemView.setOnClickListener{

                  val intent = Intent(itemView.context,HappyPlaceDetail::class.java)
                  intent.putExtra("happy_place",items[adapterPosition])
                  itemView.context.startActivity(intent)


              }
            }




        }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(HappyRecycleBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return  items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvRecycle1.text = item.title
        holder.tvRecycle2.text = item.description
        holder.itemView.ivRecycleImage.setImageURI(Uri.parse(item.image))





        holder.ivDelete.setOnClickListener {

            deleteListener.invoke(item.id)
        }








    }

}




