package com.example.sellerdashboard.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import com.example.sellerdashboard.R
import com.example.sellerdashboard.adapter.AddProductImageAdapter
import com.example.sellerdashboard.databinding.FragmentAddproductBinding
import com.example.sellerdashboard.model.AddProductModel
import com.example.sellerdashboard.model.CategoryModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList


class AddproductFragment : Fragment() {
   private lateinit var binding: FragmentAddproductBinding
   private lateinit var list : ArrayList<Uri>
    private lateinit var listImages : ArrayList<String>
    private lateinit var adapter : AddProductImageAdapter
    private var coverImage: Uri? = null
    private lateinit var dialog: Dialog
    private var coverImgUrl : String? = ""
    private lateinit var categoryList: ArrayList<String>

    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == Activity.RESULT_OK){
            coverImage = it.data!!.data
            binding.productCoverImg.setImageURI(coverImage)
            binding.productCoverImg.visibility = VISIBLE

        }
    }

    private var launchProductActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == Activity.RESULT_OK){
           val imageUrl = it.data!!.data
            list.add(imageUrl!!)
            adapter.notifyDataSetChanged()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddproductBinding.inflate(layoutInflater)
        list = ArrayList()
        listImages = ArrayList()
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        binding.selectCoverImg.setOnClickListener {
            var intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchGalleryActivity.launch(intent)
        }

        binding.productImgBtn.setOnClickListener {
            var intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchProductActivity.launch(intent)
        }
        setProductCategory()
        adapter = AddProductImageAdapter(list)
        binding.productImgRecyclerView.adapter = adapter

        binding.submitProductBtn.setOnClickListener {
            validateData()
        }

        return binding.root
    }

    private fun validateData(){
        if(binding.productNameEdt.text.toString().isEmpty()){
            binding.productNameEdt.requestFocus()
            binding.productNameEdt.error = "Empty"
        }else if(binding.productDescriptionEdt.text.toString().isEmpty()){
            binding.productDescriptionEdt.requestFocus()
            binding.productDescriptionEdt.error = "Empty"
        }else if(coverImage == null){
            Toast.makeText(requireContext(), "Please select cover image", Toast.LENGTH_SHORT).show()
        }else if(list.size < 1){
            Toast.makeText(requireContext(), "Please select product images", Toast.LENGTH_SHORT).show()
        }else{
            uploadImage()
        }
    }

    private fun uploadImage() {
        dialog.show()
        val fileName = UUID.randomUUID().toString()+".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("products/$fileName")
        refStorage.putFile(coverImage!!).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener { image ->
                coverImgUrl=image.toString()
                uploadProductImage()

            }
        }.addOnFailureListener{
            dialog.dismiss()
            Toast.makeText(requireContext(), "Something went Wrong", Toast.LENGTH_SHORT).show()
        }
    }


    private var i=0
    private fun uploadProductImage() {
        dialog.show()
        val fileName = UUID.randomUUID().toString()+".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("products/$fileName")
        refStorage.putFile(list[i]).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener { image ->
                listImages.add(image!!.toString())
                if(list.size == listImages.size){
                    storeData()
                }else{
                   i+=1
                   uploadProductImage()
                }

            }
        }.addOnFailureListener{
            dialog.dismiss()
            Toast.makeText(requireContext(), "Something went Wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun storeData() {
        val db = Firebase.firestore.collection("products")
        val key = db.document().id

        val data = AddProductModel(
            binding.productNameEdt.text.toString(),
            binding.productDescriptionEdt.text.toString(),
            coverImgUrl.toString(),
            categoryList[binding.productCategoryDropdown.selectedItemPosition],
            key,
            binding.productMrpEdt.text.toString(),
            listImages

        )
        db.document(key).set(data).addOnSuccessListener {
            dialog.dismiss()
            Toast.makeText(requireContext(), "Product Added", Toast.LENGTH_SHORT).show()
            binding.productNameEdt.text = null
        }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went Wrong", Toast.LENGTH_SHORT).show()
            }
    }


    private fun setProductCategory() {
        categoryList = ArrayList()
        Firebase.firestore.collection("categories").get().addOnSuccessListener {
            categoryList.clear()
            for (doc in it .documents){
                val data = doc.toObject(CategoryModel::class.java)
                categoryList.add(data!!.cat!!)
            }
            categoryList.add(0,"Select Category")
            val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, categoryList)
            binding.productCategoryDropdown.adapter =arrayAdapter
        }
    }


}