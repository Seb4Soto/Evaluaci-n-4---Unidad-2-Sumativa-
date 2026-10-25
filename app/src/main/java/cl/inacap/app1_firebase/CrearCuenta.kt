package cl.inacap.app1_firebase

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase

class CrearCuenta : AppCompatActivity() {

    private lateinit var txtrut: EditText
    private lateinit var txtnombre: EditText
    private lateinit var txtapellido: EditText
    private lateinit var txtcorreo: EditText
    private lateinit var spcargo: Spinner
    private lateinit var txtcon: EditText
    private lateinit var txtcon2: EditText
    private lateinit var btnregistrar: Button
    private lateinit var btnvolcrear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)

        txtrut = findViewById(R.id.txtrut)
        txtnombre = findViewById(R.id.txtnombre)
        txtapellido = findViewById(R.id.txtapellido)
        txtcorreo = findViewById(R.id.txtcorreo)
        spcargo = findViewById(R.id.spcargo)
        txtcon = findViewById(R.id.txtcon)
        txtcon2 = findViewById(R.id.txtcon2)
        btnregistrar = findViewById(R.id.btnregistrar)
        btnvolcrear = findViewById(R.id.btnvolcrear)

        botonRegistrar()
        botonVolver()
    } // Cierra el "onCreate".

    private fun botonRegistrar() {
        btnregistrar.setOnClickListener {

            val rut = txtrut.text.toString().trim()
            val nombre = txtnombre.text.toString().trim()
            val apellido = txtapellido.text.toString().trim()
            val correo = txtcorreo.text.toString().trim()
            val con = txtcon.text.toString()
            val con2 = txtcon2.text.toString()

            if (rut.isEmpty() || nombre.isEmpty() || apellido.isEmpty() ||
                correo.isEmpty() || con.isEmpty() || con2.isEmpty()
            ) {
                mensaje("¡ Complete Los Campos Faltantes !")
            } else if (!Utils.rutValido(rut)) {
                mensaje("¡ El Rut ingresado no es válido !")
            } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                mensaje("¡ El correo electrónico no es válido !")
            } else if (spcargo.selectedItemPosition == 0) {
                mensaje("¡ Seleccione un cargo !")
            } else if (con.length < 8) {
                mensaje("¡ La contraseña debe tener al menos 8 caracteres !")
            } else if (con != con2) {
                mensaje("¡ Las contraseñas no coinciden !")
            } else {
                registrar(rut, nombre, apellido, correo, spcargo.selectedItem.toString(), con)
            }
        } // Cierra el evento onClick.
    } // Cierra la función "botonRegistrar".

    /** Crea el usuario en Firebase Authentication y guarda su perfil (sin contraseña) en la base de datos. */
    private fun registrar(rut: String, nombre: String, apellido: String, correo: String, cargo: String, con: String) {
        btnregistrar.isEnabled = false
        val auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(Utils.correoInterno(rut), con)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    btnregistrar.isEnabled = true
                    when (task.exception) {
                        is FirebaseAuthUserCollisionException -> mensaje("¡ Ese Rut ya está registrado !")
                        is FirebaseNetworkException -> mensaje("¡ Sin conexión a Internet !")
                        else -> mensaje("¡ No se pudo crear la cuenta !")
                    }
                } else {
                    val uid = auth.currentUser?.uid ?: ""
                    val perfil = hashMapOf<String, Any>(
                        "rut" to Utils.claveRut(rut),
                        "nombre" to nombre,
                        "apellido" to apellido,
                        "correo" to correo,
                        "cargo" to cargo
                    )
                    FirebaseDatabase.getInstance().reference
                        .child("usuarios").child(uid).setValue(perfil)
                        .addOnSuccessListener {
                            auth.signOut() // el usuario debe iniciar sesión desde el Login
                            mensaje("¡ Cuenta creada correctamente !")
                            finish() // vuelve al Login
                        }
                        .addOnFailureListener {
                            btnregistrar.isEnabled = true
                            mensaje("¡ No se pudo guardar el perfil !")
                        }
                }
            }
    }

    private fun botonVolver() {
        btnvolcrear.setOnClickListener { finish() }
    }

    private fun mensaje(texto: String) {
        Toast.makeText(this@CrearCuenta, texto, Toast.LENGTH_LONG).show()
    }
} // Cierra la clase "CrearCuenta".
