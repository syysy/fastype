package com.example.myapplication

import android.content.Context
import org.json.JSONObject

class EditRessources(private val context: Context) {

    fun loadRawJsonFile(ressource: Int): JSONObject {
        val jsonString = this.context.resources.openRawResource(ressource).bufferedReader().use { it.readText() }
        return JSONObject(jsonString)
    }

    fun loadEditableJsonFile(fileName: String): JSONObject {
        return try {
            val jsonString = this.context.openFileInput(fileName)
            JSONObject(jsonString.bufferedReader().use { it.readText() })
        } catch (e: Exception) {
            JSONObject()
        }
    }

    fun writeJsonFile(fileName: String, json: JSONObject) {
        val file = this.context.openFileOutput(fileName, Context.MODE_PRIVATE)
        file.write(json.toString().toByteArray())
        file.close()
    }

    fun loadTextFile(ressource: Int): String {
        return this.context.resources.openRawResource(ressource).bufferedReader().use { it.readText() }
    }
}