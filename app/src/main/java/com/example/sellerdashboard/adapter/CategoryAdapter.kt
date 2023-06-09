package com.example.sellerdashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sellerdashboard.R

import com.example.sellerdashboard.databinding.ItemCategoryLayoutBinding
import com.example.sellerdashboard.model.CategoryModel

class CategoryAdapter(var context : Context, val list : ArrayList<CategoryModel>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>(){
    inner class CategoryViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var binding = ItemCategoryLayoutBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category_layout, parent, false))
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
       holder.binding.textView9.text = list[position].cat
        Glide.with(context).load(list[position].img).into(holder.binding.imageView3)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}