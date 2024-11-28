package com.example.myapplication.model

import android.os.Parcel
import android.os.Parcelable

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
) : Parcelable {
    // Constructor to read from Parcel
    constructor(parcel: Parcel) : this(
        firstName = parcel.readString(),
        lastName = parcel.readString(),
        fatherName = parcel.readString(),
        motherName = parcel.readString(),
        fatherPhone = parcel.readValue(Int::class.java.classLoader) as? Int,
        dateOfBirth = parcel.readString(),
        timeOfBirth = parcel.readString(),
        placeOfBirth = parcel.readString(),
        gender = parcel.readString(),
        disability = parcel.readString(),
        permanentAddressOfParents = parcel.readString(),
        fingerprint = parcel.readString()
    )
    // Write the object data to a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(fatherName)
        parcel.writeString(motherName)
        parcel.writeValue(fatherPhone)
        parcel.writeString(dateOfBirth)
        parcel.writeString(timeOfBirth)
        parcel.writeString(placeOfBirth)
        parcel.writeString(gender)
        parcel.writeString(disability)
        parcel.writeString(permanentAddressOfParents)
        parcel.writeString(fingerprint)
    }

    // Describe contents for Parcelable (usually 0)
    override fun describeContents(): Int = 0

    // Parcelable.Creator to recreate the object
    companion object CREATOR : Parcelable.Creator<ChildDetailsModel> {
        override fun createFromParcel(parcel: Parcel): ChildDetailsModel {
            return ChildDetailsModel(parcel)
        }

        override fun newArray(size: Int): Array<ChildDetailsModel?> {
            return arrayOfNulls(size)
        }
    }
}

