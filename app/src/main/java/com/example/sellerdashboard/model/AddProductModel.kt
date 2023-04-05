package com.example.sellerdashboard.model

data class AddProductModel(
    val productName: String?= "",
    val productDescription: String?= "",
    val productCoverImg: String?= "",
    val productCategory: String?= "",
    val productId: String?= "",
    val productMrp: String?= "",
    val productImage: ArrayList<String>?
)