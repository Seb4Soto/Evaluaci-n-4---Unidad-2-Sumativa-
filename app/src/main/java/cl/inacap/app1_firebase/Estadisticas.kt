package cl.inacap.app1_firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Estadisticas : AppCompatActivity() {

    private lateinit var tvusuestadisticas: TextView
    private lateinit var tvact: TextView
    private lateinit var tvdes: TextView
    private lateinit var tvobs: TextView
    private lateinit var tvdisttotal: TextView
    private lateinit var btnvolest: Button

    private var usu: Usuario? = null

    private lateinit var refAcciones: DatabaseReference
    private lateinit var listenerEst: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        tvusuestadisticas = findViewById(R.id.tvusuestadisticas)
        tvact = findViewById(R.id.tvact)
        tvdes = findViewById(R.id.tvdes)
        tvobs = findViewById(R.id.tvobs)
        tvdisttotal = findViewById(R.id.tvdisttotal)
        btnvolest = findViewById(R.id.btnvolest)

        usu = intent.getSerializableExtra("usu", Usuario::class.java)
        tvusuestadisticas.text = "${usu?.nombre} ${usu?.apellido}"

        // Las estadísticas se calculan en tiempo real desde el historial guardado en Firebase
        refAcciones = FirebaseDatabase.getInstance().reference.child("acciones")

        listenerEst = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var activaciones = 0
                var desactivaciones = 0
                var total = 0
                var distancia = 0L

                for (item in snapshot.children) {
                    total++
                    if (item.child("accion").value as? String == "Activar") activaciones++
                    else desactivaciones++
                    distancia += item.child("dist").value as? Long ?: 0L
                }

                tvact.text = activaciones.toString()
                tvdes.text = desactivaciones.toString()
                tvobs.text = total.toString()
                tvdisttotal.text = "$distancia cm"
            }

            override fun onCancelled(error: DatabaseError) {
                tvobs.text = "-"
            }
        }

        volver()
        desactivarFlecha()
    } // Cierra el "onCreate".

    override fun onStart() {
        super.onStart()
        refAcciones.addValueEventListener(listenerEst)
    }

    override fun onStop() {
        super.onStop()
        refAcciones.removeEventListener(listenerEst)
    }

    private fun volver() {
        btnvolest.setOnClickListener {
            val intent = Intent(this@Estadisticas, Menu::class.java)
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
} // Cierra la clase "Estadisticas".
