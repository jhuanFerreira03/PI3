package br.edu.puc.sorriso24h.infra

import android.content.Context

class SecurityPreferences(context: Context) {
    private val security = context.getSharedPreferences("PI3", Context.MODE_PRIVATE)

    fun storeString (key: String, str : String){
        security.edit().putString(key, str).apply()
    }
    fun getString(key:String):String?{
        return security.getString(key, "")
    }
    fun storeInt (key: String, num : Int){
        security.edit().putInt(key, num).apply()
    }
    fun getInt(key:String):Int?{
        return security.getInt(key, 0)
    }
}