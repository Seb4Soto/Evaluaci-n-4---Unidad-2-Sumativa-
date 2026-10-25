package cl.inacap.app1_firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Menu : AppCompatActivity() {

    private lateinit var tvusumenu: TextView
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var btn5: Button

    private var usu: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        tvusumenu = findViewById(R.id.tvusumenu)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        btn4 = findViewById(R.id.btn4)
        btn5 = findViewById(R.id.btn5)

        // Se recibe el objeto enviado desde la actividad anterior
        usu = intent.getSerializableExtra("usu", Usuario::class.java)

        // Se muestra el dato solo si NO es NULL
        tvusumenu.text = "${usu?.nombre} ${usu?.apellido}"

        boton1()
        boton2()
        boton3()
        boton4()
        boton5()
        desactivarFlecha()
    } // Cierra el "onCreate".

    private fun boton1() {
        btn1.setOnClickListener {
            val intent = Intent(this@Menu, ManipularCircuito::class.java)
            intent.putExtra("usu", usu)
            startActivity(intent)
        }
    }

    private fun boton2() {
        btn2.setOnClickListener {
            val intent = Intent(this@Menu, Acciones::class.java)
            intent.putExtra("usu", usu)
            startActivity(intent)
        }
    }

    private fun boton3() {
        btn3.setOnClickListener {
            val intent = Intent(this@Menu, Estadisticas::class.java)
            intent.putExtra("usu", usu)
            startActivity(intent)
        }
    }

    private fun boton4() {
        btn4.setOnClickListener {
            val intent = Intent(this@Menu, Perfil::class.java)
            intent.putExtra("usu", usu)
            startActivity(intent)
        }
    }

    private fun boton5() {
        btn5.setOnClickListener {
            // AlertDialog para confirmar o no el cierre de sesión
            var a = AlertDialog.Builder(this@Menu)
            a.setTitle("Pregunta")
            a.setMessage("¿Desea Cerrar La Sesión?")

            a.setNegativeButton("Cancelar") { dialog, wich ->
                dialog.dismiss()
            }

            a.setPositiveButton("Aceptar") { dialog, wich ->
                FirebaseAuth.getInstance().signOut() // cierra la sesión en Firebase
                var intent = Intent(this@Menu, Principal::class.java)
                startActivity(intent)
                finish()
            }

            a.setIcon(android.R.drawable.ic_dialog_alert)

            a.setCancelable(false)
            a.show()
        }
    }

    private fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Si dejas vacío, NO hace nada
            }
        })
    }
} // Cierra la clase "Menu".
