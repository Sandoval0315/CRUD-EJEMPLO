package RecylerViewHelper

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.Conexion
import modelo.Productos
import sandoval.crudsandoval.R
import java.util.UUID


class Adaptador (private var Datos: List<Productos>) : RecyclerView.Adapter<ViewHolder>(){


     fun actualizarLista(nuevalista: List<Productos>)
     {
         Datos = nuevalista
         notifyDataSetChanged()
     }

    fun actualizarProductos(nombreProducto: String, uuid: String){
        GlobalScope.launch(Dispatchers.IO){
            val objconexion = Conexion().cadenaConexion()

            val updateProductos = objconexion?.prepareStatement("update tbproductos set nombreProducto = ? where uuid = ?")!!
            updateProductos.setString(1, nombreProducto)
            updateProductos.setString(2, uuid)
            updateProductos.executeUpdate()

        }
    }

    fun eliminarRegistro(nombreProducto: String, posicion: Int)
    {
        //crear el objeto


        //  quitar los elementos de la vista
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        //quitar de la base de datos
        GlobalScope.launch(Dispatchers.IO){
            val objConexion = Conexion().cadenaConexion()
            val delProductos = objConexion?.prepareStatement("delete tbproductos where nombreProducto = ?")!!
            delProductos.setString(1,nombreProducto)
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
    {        val productos = Datos[position]
        holder.textView.text = productos.nombreProducto

    val item = Datos[position]
    holder.imgBorrar.setOnClickListener{
        eliminarRegistro(item.nombreProducto, position )
    }
        holder.imgEditar.setOnClickListener{
            //crea una alerta

            val context = holder.itemView.context
            val editar = EditText(context)
            editar.setHint(item.nombreProducto)


            val builder = AlertDialog.Builder(context)

            builder.setView(editar)

            builder.setTitle("Actualizar")

            builder.setMessage("Esta seguro que quiere actualizar este campo?")



            builder.setPositiveButton("S1") {dialog, which-> actualizarProductos (editar.text.toString(), productos.uuid)}

            builder.setNegativeButton("No") {dialog, which-> dialog.dismiss()}

            val dialog =builder.create()
            dialog.show()

        }

    }
}

