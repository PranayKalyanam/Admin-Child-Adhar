package com.example.myapplication.model

data class ChildDetailsModel(
    val firstName: String? = null,
    val lastName: String? = null,
    val fatherName: String? = null,
    val motherName: String? = null,
    val fatherPhone: Int?=null,
    val dateOfBirth: String? = null,
    val timeOfBirth: String? = null,
    val placeOfBirth: String? = null,
    val gender: String? = null,
    val disability: String? = null,
    val permanentAddressOfParents: String? = null,
    val fingerprint: String? = null // Fingerprint data (could be base64 string or image URL)
)
