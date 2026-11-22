package com.example.offensiveandroid.ui.theme

import android.annotation.SuppressLint
import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import org.jetbrains.annotations.Contract

class ContactsRetriever(private val context: Context) {
      @SuppressLint("Range")
      fun getAllContacts(): List<Contact>{
          val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
          val projection = arrayOf(
              ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
              ContactsContract.CommonDataKinds.Phone.NUMBER,
          )

          val sortOrder = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
          val cursor = context.contentResolver.query(uri,projection,null,null,sortOrder)

          val contacts = mutableListOf<Contact>()

          //cursor is used as it is a pointer to rows of data returned by db query on android
          cursor?.let {
              while (it.moveToNext()){
                  val contactName = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                  val contactPhoneNumber = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                  contacts.add(Contact(contactName,contactPhoneNumber))

                  Log.d("Contacts","Name: $contactName, Phone: $contactPhoneNumber")
              }
              it.close()
          } ?: run {
              Log.d("Contacts","No contacts found")
          }
          return contacts
      }
}

data class Contact(
    val name : String,
    val phoneNumber: String
)