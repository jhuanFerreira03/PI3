package br.edu.puc.sorriso24h.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.listener.ListListener

class MyAdapter(var context: Context,var arrayList: ArrayList<User>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private lateinit var listen : ListListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        val v : View = LayoutInflater.from(context).inflate(R.layout.item, parent, false)
        return MyViewHolder(v, listen)
    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {
        val user : User = arrayList.get(position)

        holder.nome.setText(user.nome)
        holder.telefone.setText(user.telefone)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
    fun attListener(lis : ListListener){
        this.listen = lis
    }


    class MyViewHolder(item: View, listen: ListListener) : RecyclerView.ViewHolder(item) {
        var nome : TextView
        var telefone : TextView
        init {
            nome = item.findViewById(R.id.text_nome_list)
            telefone = item.findViewById(R.id.text_telefone_list)

            item.setOnClickListener {
                listen.onClick(adapterPosition)
            }
        }
    }
}
class User{
    lateinit var nome : String
    lateinit var telefone : String
}