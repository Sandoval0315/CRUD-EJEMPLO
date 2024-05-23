package sandoval.crudsandoval

import RecylerViewHelper.Adaptador
import android.os.Bundle
import android.widget.Adapter
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.Conexion
import modelo.Productos
import java.util.UUID

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val txtName = findViewById<EditText>(R.id.txtname)
        val txtPrice = findViewById<EditText>(R.id.txtprice)
        val txtCantidad = findViewById<EditText>(R.id.txtcantidad)
        val btnAgregar = findViewById<Button>(R.id.btnagregar)


        //CREAMOS UN OBJETO
        val rcbProductos = findViewById<RecyclerView>(R.id.rcvProductos)
        rcbProductos.layoutManager = LinearLayoutManager(this)
        //MANDAMOS A LLAMAR EL OBJETO
        fun obtenerdatos(): List<Productos> {
            val objConexion = Conexion().cadenaConexion()
            val statement = objConexion?.createStatement()
            val resultSet =statement?.executeQuery("select * from tbproductos")!!

//MOSTRAMOS EL OBJETO
            val productos = mutableListOf<Productos>()
            while ( resultSet.next()){
                val uuid = resultSet.getString("uuid")
                val nombre = resultSet.getString("nombreProducto")
                val precio = resultSet.getInt("precio")
                val cantidad = resultSet.getInt("cantidad")
                val producto =  Productos(uuid,nombre,precio,cantidad)
                productos.add(producto)
            }
            return productos
        }

//ASIGNAR ADAPTADOR
        CoroutineScope(Dispatchers.IO).launch {
            val productosDB = obtenerdatos()
            withContext(Dispatchers.Main){
                val miAdapter = Adaptador(productosDB)
                rcbProductos.adapter = miAdapter
            }
        }

        btnAgregar.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val Conexion = Conexion().cadenaConexion()

                val addProductos =
                    Conexion?.prepareStatement("insert into tbproductos(uuid,nombreProducto,precio,cantidad) values (?,?, ?, ?)")!!

                addProductos.setString(1,UUID.randomUUID().toString())
                addProductos.setString(2, txtName.text.toString())
                addProductos.setInt(3, txtPrice.text.toString().toInt())
                addProductos.setInt(4, txtCantidad.text.toString().toInt())
                addProductos.executeUpdate();

                val nuevosProductos = obtenerdatos()
                withContext(Dispatchers.Main)
                {
                    (rcbProductos.adapter as? Adaptador)?.actualizarLista(nuevosProductos)
                }


                txtName.setText("");
                txtCantidad.setText("");
                txtPrice.setText("");
            }
        }

    }
}