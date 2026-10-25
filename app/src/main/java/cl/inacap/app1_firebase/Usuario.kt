package cl.inacap.app1_firebase

import java.io.Serializable

/**
 * data class: clase pensada para almacenar y transportar datos.
 * Serializable: permite enviar el objeto entre Activities (putExtra / getSerializableExtra).
 * ISO 27400: la contraseña NO se guarda aquí ni en la base de datos; la gestiona Firebase Authentication.
 */
data class Usuario(
    val uid: String = "",
    val rut: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val correo: String = "",
    val cargo: String = ""
) : Serializable
