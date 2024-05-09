package RecylerViewHelper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.Conexion
import modelo.Productos
import sandoval.crudsandoval.R

class Adaptador (private var Datos: List<Productos>) : RecyclerView.Adapter<ViewHolder>(){


     fun actualizarLista(nuevalista: List<Productos>)
     {
         Datos = nuevalista
         notifyDataSetChanged()
     }

    fun eliminarRegistro(nombreProductos: String, posicion: Int)
    {
        //crear el objeto


        //  quitar los elementos de la vista
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        //quitar de la base de datos
        GlobalScope.launch(Dispatchers.IO){
            val objConexion = Conexion().cadenaConexion()
            val delProductos = objConexion?.prepareStatement("delete tbproductos where nombreProducto = ?")!!
            delProductos.setString(1,nombreProductos)
            delProductos.executeUpdate()

            val commit = objConexion.prepareStatement("commit")!!
            commit.executeUpdate()
        }

        Datos = listaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()

    }
      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val vista =
        LayoutInflater.from(parent.context).inflate(R.layout.tem_acr, parent, false)
    return ViewHolder(vista)
}
    override fun getItemCount() = Datos.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {        val producto = Datos[position]
        holder.textView.text = producto.nombreProducto

    val item = Datos[position]
    holder.imgBorrar.setOnClickListener{
        eliminarRegistro(item.nombreProducto, position )
    }

    }
}

