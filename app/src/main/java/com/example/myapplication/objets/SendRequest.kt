package com.example.myapplication.objets

import android.content.Context
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class SendRequest {

    public fun post(url: String, jsonObject: JSONObject, context: Context): JSONObject {
        val queue = Volley.newRequestQueue(context)
        var json: JSONObject = JSONObject()
        val jsonRequest = JsonObjectRequest(Request.Method.POST, url, jsonObject, {
                response ->
            run {
                json = response
            }
        }, {
            // Error in request
            Toast.makeText(context, this.codeToText(it.networkResponse.statusCode), Toast.LENGTH_SHORT).show()
        })
        queue.add(jsonRequest)
        return json
    }

    public fun get(url: String, context: Context): JSONObject {
        val queue = Volley.newRequestQueue(context)
        var json: JSONObject = JSONObject()
        val jsonRequest = JsonObjectRequest(Request.Method.GET, url, null, {
               response ->
            run {
                json = response
            }
        }, {
            // Error in request
            Toast.makeText(context, this.codeToText(it.networkResponse.statusCode), Toast.LENGTH_SHORT).show()
        })
        queue.add(jsonRequest)
        return json
    }

    private fun codeToText(code: Int): String {
        return "Request Error: " + when (code) {
            204 -> "No data to send you back"
            400 -> "Bad request"
            401 -> "Unauthorized"
            403 -> "Forbidden"
            404 -> "Not Found"
            405 -> "Method Not Allowed"
            406 -> "Not Acceptable"
            407 -> "Proxy Authentication Required"
            413 -> "Request Entity Too Large"
            500 -> "Server Error"
            501 -> "Not Implemented"
            502 -> "Bad Gateway"
            503 -> "Out of Resources"
            else -> "Error Code - $code"
        }
    }
}