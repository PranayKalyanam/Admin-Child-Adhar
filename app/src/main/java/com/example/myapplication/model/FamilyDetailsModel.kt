package com.example.myapplication.model

data class FamilyDetailsModel(
    val familyName:String?=null,
    val familyIncome:String?=null,
    val familyAddress:String?=null,
    val children: List<ChildDetailsModel>? = null // List of child details linked to this family
)
