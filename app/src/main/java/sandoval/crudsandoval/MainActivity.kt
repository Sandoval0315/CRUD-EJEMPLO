package sandoval.crudsandoval

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.Conexion

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


        btnAgregar.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val Conexion = Conexion().cadenaConexion()

                val addProductos =
                    Conexion?.prepareStatement("insert into tbProductos values (?, ?, ?)")!!

                addProductos.setString(1, txtName.text.toString())
                addProductos.setInt(2, txtPrice.text.toString().toInt())
                addProductos.setInt(3, txtCantidad.text.toString().toInt())
                addProductos.executeUpdate();


                txtName.setText("");
                txtCantidad.setText("");
                txtPrice.setText("");
            }
        }
    }
}