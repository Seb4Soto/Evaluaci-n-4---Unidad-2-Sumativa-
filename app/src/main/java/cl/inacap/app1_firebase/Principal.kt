package cl.inacap.app1_firebase

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Principal : AppCompatActivity() {

    private lateinit var txtrut: EditText
    private lateinit var txtcon: EditText
    private lateinit var btnini: Button
    private lateinit var btncrear: Button

    private lateinit var auth: FirebaseAuth

    // Control de acceso: bloqueo temporal tras varios intentos fallidos
    private var intentosFallidos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        txtrut = findViewById(R.id.txtrut)
        txtcon = findViewById(R.id.txtcon)
        btnini = findViewById(R.id.btnini)
        btncrear = findViewById(R.id.btncrear)

        auth = FirebaseAuth.getInstance()
        // Siempre se exige iniciar sesión al abrir la app
        auth.signOut()

        botonLogin()
        botonCrear()
        desactivarFlecha()
    } // Cierra el "onCreate".

    private fun botonLogin() {
        btnini.setOnClickListener {

            val rut = txtrut.text.toString().trim()
            val con = txtcon.text.toString()

            if (rut.isEmpty() || con.isEmpty()) {
                mensaje("¡ Complete Los Campos Faltantes !")
            } else if (!Utils.rutValido(rut)) {
                mensaje("¡ El Rut ingresado no es válido !")
            } else {
                ocultarTeclado()
                btnini.isEnabled = false

                auth.signInWithEmailAndPassword(Utils.correoInterno(rut), con)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            intentosFallidos = 0
                            cargarPerfil()
                        } else {
                            btnini.isEnabled = true
                            if (task.exception is FirebaseNetworkException) {
                                mensaje("¡ Sin conexión a Internet !")
                            } else {
                                intentosFallidos++
                                mensaje("¡ Rut o contraseña incorrectos !")
                                if (intentosFallidos >= 5) bloquearTemporalmente()
                            }
                        }
                    }
            } // Cierra if/else.
        } // Cierra el evento onClick.
    } // Cierra la función "botonLogin".

    /** Lee desde Firebase los datos del usuario autenticado y abre el menú. */
    private fun cargarPerfil() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            btnini.isEnabled = true
            mensaje("¡ No se pudo iniciar la sesión !")
            return
        }

        FirebaseDatabase.getInstance().reference
            .child("usuarios").child(uid).get()
            .addOnSuccessListener { snap ->
                if (!snap.exists()) {
                    auth.signOut()
                    btnini.isEnabled = true
                    mensaje("¡ El usuario no tiene perfil registrado !")
                } else {
                    val usu = Usuario(
                        uid = uid,
                        rut = snap.child("rut").value as? String ?: "",
                        nombre = snap.child("nombre").value as? String ?: "",
                        apellido = snap.child("apellido").value as? String ?: "",
                        correo = snap.child("correo").value as? String ?: "",
                        cargo = snap.child("cargo").value as? String ?: ""
                    )
                    txtcon.setText("")
                    btnini.isEnabled = true
                    val intent = Intent(this@Principal, Menu::class.java)
                    intent.putExtra("usu", usu)
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                auth.signOut()
                btnini.isEnabled = true
                mensaje("¡ No se pudo leer el perfil del usuario !")
            }
    } // Cierra la función "cargarPerfil".

    private fun bloquearTemporalmente() {
        btnini.isEnabled = false
        mensaje("Demasiados intentos. Espere 30 segundos.")
        Handler(Looper.getMainLooper()).postDelayed({
            intentosFallidos = 0
            btnini.isEnabled = true
        }, 30000)
    }

    private fun botonCrear() {
        btncrear.setOnClickListener {
            startActivity(Intent(this@Principal, CrearCuenta::class.java))
        }
    }

    private fun mensaje(texto: String) {
        Toast.makeText(this@Principal, texto, Toast.LENGTH_LONG).show()
    }

    private fun ocultarTeclado() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Si dejas vacío, NO hace nada
            }
        })
    }
} // Cierra la clase "Principal".
