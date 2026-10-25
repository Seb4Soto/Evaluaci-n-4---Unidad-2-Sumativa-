package cl.inacap.app1_firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class Perfil : AppCompatActivity() {

    private lateinit var tvusuperfil: TextView
    private lateinit var tvrutper: TextView
    private lateinit var tvnomper: TextView
    private lateinit var tvapeper: TextView
    private lateinit var tvcorper: TextView
    private lateinit var tvcarper: TextView
    private lateinit var tvconper: TextView
    private lateinit var btnvolper: Button

    private var usu: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        tvusuperfil = findViewById(R.id.tvusuperfil)
        tvrutper = findViewById(R.id.tvrutper)
        tvnomper = findViewById(R.id.tvnomper)
        tvapeper = findViewById(R.id.tvapeper)
        tvcorper = findViewById(R.id.tvcorper)
        tvcarper = findViewById(R.id.tvcarper)
        tvconper = findViewById(R.id.tvconper)
        btnvolper = findViewById(R.id.btnvolper)

        // Se recibe el objeto enviado desde la actividad anterior
        usu = intent.getSerializableExtra("usu", Usuario::class.java)

        // Se muestran los datos solo si NO son NULL
        tvusuperfil.text = "${usu?.nombre} ${usu?.apellido}"
        tvrutper.text = usu?.rut?.let { Utils.formatearRut(it) }
        tvnomper.text = usu?.nombre
        tvapeper.text = usu?.apellido
        tvcorper.text = usu?.correo
        tvcarper.text = usu?.cargo
        // La contraseña se guarda cifrada (hash), por seguridad solo se muestra enmascarada
        tvconper.text = "••••••••"

        volver()
        desactivarFlecha()
    } // Cierra el "onCreate".

    private fun volver() {
        btnvolper.setOnClickListener {
            val intent = Intent(this@Perfil, Menu::class.java)
            intent.putExtra("usu", usu)
            startActivity(intent)
            finish()
        }
    }

    private fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Si dejas vacío, NO hace nada
            }
        })
    }
} // Cierra la clase "Perfil".
