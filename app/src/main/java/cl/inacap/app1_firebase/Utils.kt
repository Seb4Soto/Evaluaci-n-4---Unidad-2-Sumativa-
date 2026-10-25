package cl.inacap.app1_firebase

object Utils {

    /** "11.111.111-1" -> "111111111" (sin puntos ni guion). */
    fun claveRut(rut: String): String =
        rut.replace(".", "").replace("-", "").replace(" ", "").uppercase()

    /**
     * Firebase Authentication necesita un correo. El login es con Rut, por lo que se usa
     * un correo interno derivado del Rut (el correo real queda en el perfil del usuario).
     */
    fun correoInterno(rut: String): String = claveRut(rut).lowercase() + "@circuito.app"

    /** "111111111" -> "11.111.111-1" */
    fun formatearRut(rut: String): String {
        val l = claveRut(rut)
        if (l.length < 2) return rut
        val cuerpo = l.dropLast(1).reversed().chunked(3).joinToString(".").reversed()
        return "$cuerpo-${l.last()}"
    }

    /** Valida el dígito verificador del Rut (módulo 11). */
    fun rutValido(rut: String): Boolean {
        val l = claveRut(rut)
        if (l.length < 2) return false
        val cuerpo = l.dropLast(1)
        val dv = l.last()
        if (!cuerpo.all { it.isDigit() }) return false
        var suma = 0
        var mul = 2
        for (c in cuerpo.reversed()) {
            suma += c.digitToInt() * mul
            mul = if (mul == 7) 2 else mul + 1
        }
        val r = 11 - (suma % 11)
        val esperado = when (r) {
            11 -> '0'
            10 -> 'K'
            else -> r.toString()[0]
        }
        return dv == esperado
    }
}
