package br.edu.puc.sorriso24h.Adapter

import android.content.ClipData.Item
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.puc.sorriso24h.R
import org.w3c.dom.Text

class MyAdapter(var context: Context,var arrayList: ArrayList<User>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        val v : View = LayoutInflater.from(context).inflate(R.layout.item, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {
        val user : User = arrayList.get(position)

        holder.nome.setText(user.nome)
        holder.telefone.setText(user.telefone)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
    class MyViewHolder : RecyclerView.ViewHolder{
        lateinit var nome : TextView
        lateinit var telefone : TextView
        constructor(item : View) : super(item){
            nome = item.findViewById(R.id.text_nome)
            telefone = item.findViewById(R.id.text_telefone)
        }
    }
}
class User{
    lateinit var nome : String
    lateinit var telefone : String
}