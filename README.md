# Circuito IoT – App Android (Kotlin + Firebase)

Aplicación móvil individual – Aplicaciones Móviles para IoT (TI3042), Unidad 2. Inacap.

## 1. Problemática y funcionalidades
**Problema:** controlar y monitorear de forma remota un circuito (dispositivo IoT) desde más de un teléfono, dejando registro de quién hizo cada acción.

**Dispositivos / sistemas a conectar:** 2 o más teléfonos Android + un circuito con sensor de distancia (lectura simulada en esta versión) + la nube (Firebase).

| Pantalla | Función |
|---|---|
| Login | Inicio de sesión con Rut y contraseña (Firebase Authentication) |
| Crear cuenta | Registro de usuario (Rut, nombre, apellido, correo, cargo, contraseña) |
| Menú | Navegación y cierre de sesión |
| Manipular circuito | Activar / desactivar el circuito. El estado se sincroniza **en tiempo real** entre dispositivos |
| Ver acciones | Historial de acciones (fecha, hora, usuario, acción, distancia) |
| Estadísticas | Nº de activaciones, desactivaciones, total de acciones y distancia acumulada |
| Ver perfil | Datos del usuario (contraseña enmascarada) |

## 2. Herramientas y justificación
- **Android Studio + Kotlin** (lenguaje oficial para Android), minSdk 33.
- **Firebase Authentication:** autenticación segura administrada por Google (contraseñas con hash, protección contra fuerza bruta), escalable y sin servidor propio.
- **Firebase Realtime Database:** base de datos en la nube con sincronización en tiempo real, ideal para estado de dispositivos IoT. Se prefirió sobre SQLite (solo local, no permite que dos teléfonos compartan datos) y sobre MySQL + API (requiere montar y asegurar un backend propio).

## 3. Método de comunicación inalámbrica
**Wi-Fi / Internet (HTTPS/TLS) a través de Firebase.**
- Distancia: sin límite práctico (los teléfonos solo necesitan Internet), a diferencia de Bluetooth (~10 m).
- Estabilidad: reconexión automática del SDK de Firebase.
- Consumo: conexión única persistente, eficiente en batería.
- Interacción entre dos dispositivos: al activar/desactivar el circuito en el teléfono A, el teléfono B muestra el cambio al instante.

## 4. Seguridad (ISO 27400 – buenas prácticas IoT)
| Medida | Implementación |
|---|---|
| Cifrado de credenciales | La contraseña nunca se guarda en la app ni en la base de datos; Firebase Authentication la almacena con hash |
| Comunicación segura | Todo el tráfico va por HTTPS/TLS; `usesCleartextTraffic="false"` en el Manifest |
| Control de acceso | Reglas de la base de datos (`database.rules.json`): solo usuarios autenticados leen/escriben; cada usuario solo accede a su propio perfil |
| Integridad del historial | El historial es de solo anexar (no se puede modificar ni borrar) y valida que la acción pertenezca al usuario autenticado |
| Validación de datos | Validación del Rut (dígito verificador), correo, contraseña mínima de 8 caracteres y validación de esquema en la base de datos |
| Mínimo privilegio | Solo permisos `INTERNET` y `ACCESS_NETWORK_STATE`; `allowBackup="false"` |
| Protección contra fuerza bruta | Bloqueo de 30 s tras 5 intentos fallidos de login |
| Gestión de sesión | La app siempre pide login al abrirse y cierra la sesión en Firebase al salir |

## 5. Pruebas de funcionamiento
| # | Prueba | Resultado esperado |
|---|---|---|
| 1 | Crear cuenta con datos válidos | Cuenta creada, vuelve al login |
| 2 | Crear cuenta con Rut inválido / contraseña corta / correo inválido | Mensaje de error, no se crea |
| 3 | Crear cuenta con Rut ya registrado | "Ese Rut ya está registrado" |
| 4 | Login correcto | Ingresa al menú con nombre del usuario |
| 5 | Login con contraseña incorrecta | "Rut o contraseña incorrectos" |
| 6 | 5 intentos fallidos | Botón bloqueado 30 segundos |
| 7 | Activar circuito en teléfono A | Teléfono B muestra "Estado: ACTIVADO" al instante |
| 8 | Desactivar circuito en teléfono B | Teléfono A muestra "Estado: DESACTIVADO" |
| 9 | Ver acciones | Aparece cada acción con fecha, hora, usuario y distancia |
| 10 | Estadísticas | Los contadores coinciden con el historial |
| 11 | Sin Internet | Mensaje de "Sin conexión", la app no se cierra |
| 12 | Acceso a la base sin sesión (regla de seguridad) | Rechazado: `Permission denied` |

## 6. Cómo ejecutar
1. Abrir el proyecto en Android Studio (JDK 17).
2. Colocar `google-services.json` de tu proyecto Firebase dentro de la carpeta `app/` (paquete `cl.inacap.app1_firebase`).
3. En Firebase: activar **Authentication → Correo electrónico/Contraseña** y crear **Realtime Database**; pegar `database.rules.json` en la pestaña *Reglas*.
4. Ejecutar en dos dispositivos (o emulador + teléfono).

> La lectura de distancia es **simulada** (valor aleatorio 5–30 cm) porque no se usa hardware real; el mecanismo de registro y estadísticas es el mismo que se usaría con un sensor ultrasónico.
