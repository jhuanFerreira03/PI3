package br.edu.puc.tenstadoessecarai.infra

import android.content.Context

class SecurityPreferences(context: Context) {
    private val security = context.getSharedPreferences("PI3", Context.MODE_PRIVATE)

    fun storeString (key: String, str : String){
        security.edit().putString(key, str).apply()
    }
    fun getString(key:String):String?{
        return security.getString(key, "")
    }
}