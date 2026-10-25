package cl.inacap.app1_firebase

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Acciones : AppCompatActivity() {

    private lateinit var tvusuacciones: TextView
    private lateinit var tvvacio: TextView
    private lateinit var llacciones: LinearLayout
    private lateinit var btnvolacc: Button

    private var usu: Usuario? = null

    private lateinit var refAcciones: DatabaseReference
    private lateinit var listenerAcciones: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acciones)

        tvusuacciones = findViewById(R.id.tvusuacciones)
        tvvacio = findViewById(R.id.tvvacio)
        llacciones = findViewById(R.id.llacciones)
        btnvolacc = findViewById(R.id.btnvolacc)

        usu = intent.getSerializableExtra("usu", Usuario::class.java)
        tvusuacciones.text = "${usu?.nombre} ${usu?.apellido}"

        // Últimas 50 acciones registradas (se actualiza en tiempo real)
        refAcciones = FirebaseDatabase.getInstance().reference.child("acciones")

        listenerAcciones = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                llacciones.removeAllViews()
                val lista = snapshot.children.toList().takeLast(50).reversed() // más reciente primero
                tvvacio.visibility = if (lista.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE

                for (item in lista) {
                    val vista = LayoutInflater.from(this@Acciones)
                        .inflate(R.layout.item_accion, llacciones, false)
                    vista.findViewById<TextView>(R.id.tvfecha).text = item.child("fecha").value as? String ?: ""
                    vista.findViewById<TextView>(R.id.tvhora).text = item.child("hora").value as? String ?: ""
                    vista.findViewById<TextView>(R.id.tvusuario).text = item.child("usuario").value as? String ?: ""
                    vista.findViewById<TextView>(R.id.tvaccion).text = item.child("accion").value as? String ?: ""
                    val dist = item.child("dist").value as? Long ?: 0L
                    vista.findViewById<TextView>(R.id.tvdist).text = "$dist cm"

                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.bottomMargin = 12
                    vista.layoutParams = params
                    llacciones.addView(vista)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                tvvacio.text = "No se pudo cargar el historial"
                tvvacio.visibility = android.view.View.VISIBLE
            }
        }

        volver()
        desactivarFlecha()
    } // Cierra el "onCreate".

    override fun onStart() {
        super.onStart()
        refAcciones.addValueEventListener(listenerAcciones)
    }

    override fun onStop() {
        super.onStop()
        refAcciones.removeEventListener(listenerAcciones)
    }

    private fun volver() {
        btnvolacc.setOnClickListener {
            val intent = Intent(this@Acciones, Menu::class.java)
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
} // Cierra la clase "Acciones".
