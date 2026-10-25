package cl.inacap.app1_firebase

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class ManipularCircuito : AppCompatActivity() {

    private lateinit var tvusumanipular: TextView
    private lateinit var tvestado: TextView
    private lateinit var btnon: Button
    private lateinit var btnoff: Button
    private lateinit var btnvolman: Button

    private var usu: Usuario? = null

    private lateinit var refCircuito: DatabaseReference
    private lateinit var refAcciones: DatabaseReference
    private lateinit var listenerEstado: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manipular_circuito)

        tvusumanipular = findViewById(R.id.tvusumanipular)
        tvestado = findViewById(R.id.tvestado)
        btnon = findViewById(R.id.btnon)
        btnoff = findViewById(R.id.btnoff)
        btnvolman = findViewById(R.id.btnvolman)

        usu = intent.getSerializableExtra("usu", Usuario::class.java)
        tvusumanipular.text = "${usu?.nombre} ${usu?.apellido}"

        val raiz = FirebaseDatabase.getInstance().reference
        refCircuito = raiz.child("circuito").child("estado")
        refAcciones = raiz.child("acciones")

        // Escucha en tiempo real: cualquier dispositivo que cambie el circuito actualiza esta pantalla
        listenerEstado = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mostrarEstado(snapshot.value as? Boolean ?: false)
            }

            override fun onCancelled(error: DatabaseError) {
                tvestado.text = "Estado: sin conexión"
                tvestado.setTextColor(Color.GRAY)
            }
        }

        botonOn()
        botonOff()
        volver()
        desactivarFlecha()
    } // Cierra el "onCreate".

    override fun onStart() {
        super.onStart()
        refCircuito.addValueEventListener(listenerEstado)
    }

    override fun onStop() {
        super.onStop()
        refCircuito.removeEventListener(listenerEstado)
    }

    private fun mostrarEstado(activo: Boolean) {
        if (activo) {
            tvestado.text = "Estado: ACTIVADO"
            tvestado.setTextColor(Color.parseColor("#2E7D32"))
        } else {
            tvestado.text = "Estado: DESACTIVADO"
            tvestado.setTextColor(Color.parseColor("#C62828"))
        }
    }

    private fun botonOn() {
        btnon.setOnClickListener { cambiarEstado(true) }
    }

    private fun botonOff() {
        btnoff.setOnClickListener { cambiarEstado(false) }
    }

    /** Cambia el estado del circuito y deja registrada la acción (historial). */
    private fun cambiarEstado(activo: Boolean) {
        val ahora = Date()
        val loc = Locale("es", "CL")
        val dist = Random.nextInt(5, 31) // lectura simulada del sensor de distancia (cm)

        val accion = hashMapOf<String, Any>(
            "uid" to (FirebaseAuth.getInstance().currentUser?.uid ?: ""),
            "fecha" to SimpleDateFormat("dd-MM-yyyy", loc).format(ahora),
            "hora" to SimpleDateFormat("HH:mm:ss", loc).format(ahora),
            "usuario" to "${usu?.nombre} ${usu?.apellido}",
            "accion" to (if (activo) "Activar" else "Desactivar"),
            "dist" to dist
        )

        refCircuito.setValue(activo)
            .addOnSuccessListener {
                refAcciones.push().setValue(accion)
                mensaje(if (activo) "Circuito activado" else "Circuito desactivado")
            }
            .addOnFailureListener {
                mensaje("No se pudo cambiar el circuito (revise la conexión)")
            }
    }

    private fun volver() {
        btnvolman.setOnClickListener {
            val intent = Intent(this@ManipularCircuito, Menu::class.java)
            intent.putExtra("usu", usu)
            startActivity(intent)
            finish()
        }
    }

    private fun mensaje(texto: String) {
        Toast.makeText(this@ManipularCircuito, texto, Toast.LENGTH_SHORT).show()
    }

    private fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Si dejas vacío, NO hace nada
            }
        })
    }
} // Cierra la clase "ManipularCircuito".
