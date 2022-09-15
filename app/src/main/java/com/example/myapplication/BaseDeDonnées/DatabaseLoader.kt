package com.example.myapplication.BaseDeDonnées

import android.util.Log
import com.example.myapplication.objets.ProfilModel
import com.google.firebase.database.*

class DatabaseLoader {

    private lateinit var databaseRef : DatabaseReference
    private var userModel = ProfilModel("","",0.0,0,"")

    fun loadUser(callback:() -> Unit) {
        databaseRef = FirebaseDatabase.getInstance().getReference("users")
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(ProfilModel::class.java)
                    if (user != null) {
                        userModel = user
                        callback()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

}