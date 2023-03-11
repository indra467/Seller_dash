package com.example.sellerdashboard.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.sellerdashboard.R
import com.example.sellerdashboard.databinding.FragmentProductBinding


class ProductFragment : Fragment() {

private lateinit var binding: FragmentProductBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        binding.floatingActionButton.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.action_productFragment_to_addproductFragment)
        }
        return binding.root
    }


}