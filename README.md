# AppCorpaliv - Red Alba Lab

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Room Database](https://img.shields.io/badge/Room_SQLite-Local_Storage-47A248?logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![DataStore](https://img.shields.io/badge/Jetpack-DataStore_Preferences-F4511E?logo=android&logoColor=white)](https://developer.android.com/topic/libraries/architecture/datastore)
[![JUnit 4](https://img.shields.io/badge/Tests-78_Unit_%2B_12_UI-brightgreen)](https://junit.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-Microservicio_H2-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Min SDK](https://img.shields.io/badge/Min_SDK-24_(Android_7.0)-informational)](https://developer.android.com/)
[![Target SDK](https://img.shields.io/badge/Target_SDK-37_(Android_15)-informational)](https://developer.android.com/)

Aplicacion movil nativa para Android desarrollada como proyecto estudiantil de caracter formativo, orientada a apoyar la labor comunitaria de la Fundacion CORPALIV (Corporacion de Padres y Amigos por el Limitado Visual) y su red de inclusion sociolaboral Alba Lab.

---

## Responsables del Proyecto

- Alexander Simpertigue (`Alesimm`)
- Joseph Rivas (`josephR`)
- Tablero Trello: [AppCorpalivGrupo5](https://trello.com/b/5XeJlyhX/appcorpalivgrupo5)

---

## Descripcion del Proyecto

La Fundacion CORPALIV atiende a jovenes y adultos con discapacidad multiple y sordoceguera. A traves de su centro Alba Lab, imparte programas formativos de oficios, preparacion para el trabajo y desarrollo de autonomia.

Esta aplicacion movil fue creada con fines academicos para modelar una plataforma que permita:
- Presentar la informacion detallada de los programas sociales formativos (Taller de Oficios, Insercion Laboral y Transicion a la Vida Adulta).
- Registrar solicitudes de contacto y colaboracion ciudadana o empresarial (donaciones, voluntariado, alianzas de empleo, compra de productos inclusivos y capacitacion).
- Operar de forma resiliente tanto con conexion a internet como en situaciones sin red, almacenando localmente los registros en una base de datos SQLite antes de sincronizarlos.
- Ofrecer una experiencia visual accesible con soporte completo para modo claro y modo oscuro, ademas de marcadores de favoritos locales.

---

## Credenciales de Acceso para Pruebas

Para facilitar la revision y ejecucion de la aplicacion en entornos de prueba o emuladores, la pantalla de inicio de sesion cuenta con credenciales precargadas:

| Campo | Valor de Prueba |
| :--- | :--- |
| Correo electronico | `colaborador@ejemplo.cl` |
| Contrasena | `Alba2026!` |

Los valores pueden modificarse o vaciarse en cualquier momento para comprobar el comportamiento reactivo de los validadores de formato de correo, longitud minima de contrasena y mensajes de retroalimentacion en pantalla.

---

## Arquitectura y Tecnologias

El proyecto esta construido siguiendo el patron de arquitectura Model-View-ViewModel (MVVM) con Flujo Unidireccional de Datos (UDF), recomendado por las guias oficiales de Android.

```text
+------------------------------------------------------------+
|                    CAPA DE VISTA (UI)                      |
|  - Jetpack Compose con diseno declarativo                  |
|  - Material Design 3 con adaptabilidad clara y oscura      |
|  - Componentes graficos e ilustraciones Canvas             |
+-----------------------------+------------------------------+
                              | Eventos / Observacion
                              v
+------------------------------------------------------------+
|                    CAPA DE VIEWMODEL                       |
|  - UsuarioViewModel: Sesion, formulario y SQLite Room      |
|  - EstadoViewModel: Preferencias DataStore y tema visual   |
|  - MainViewModel: Eventos reactivos de navegacion          |
+-----------------------------+------------------------------+
                              | Corrutinas / StateFlow
                              v
+------------------------------------------------------------+
|               CAPA DE DATOS Y PERSISTENCIA                 |
|  - Base de datos local SQLite mediante Room                |
|  - Persistencia ligera clave-valor con Jetpack DataStore   |
|  - Validadores puros de negocio (desacoplados del SDK)     |
+------------------------------------------------------------+
```

### Tecnologias Principales
- Lenguaje: Kotlin 2.2.10
- Framework UI: Jetpack Compose con Material 3
- Asincronia: Corrutinas de Kotlin y flujos reactivos (StateFlow / SharedFlow)
- Base de datos local: Room Database (SQLite)
- Almacenamiento de preferencias: Jetpack DataStore Preferences
- Navegacion: Navigation Compose
- Pruebas unitarias: JUnit 4

---

## Caracteristicas Principales

### 1. Ilustraciones Vectoriales Basadas en el Logotipo (Canvas)
En lugar de depender de recursos graficos rasterizados (PNG) que aumentan el tamano del ejecutable y pierden nitidez segun la densidad de pantalla, las cabeceras graficas de los programas sociales se dibujan vectorialmente mediante Canvas de Jetpack Compose (`drawArc` y `Path`):
- Taller de Oficios: Tres arcos en U concentricos con terminacion redondeada en amarillo institucional (#FFC21A).
- Insercion Laboral: Onda sinusoidal continua de cinco semicirculos con segunda onda atenuada en verde (#2FCB7E).
- Transicion a la Vida Adulta: Tres arcos superiores concentricos con pastilla vertical en morado (#9A8DF5).

### 2. Formulario de Contacto y Persistencia Offline (Room SQLite)
- Registro de solicitudes de colaboracion con captura estructurada de datos.
- Generacion automatica de folio correlativo institucional (ej: AL-2026-0001).
- Deteccion reactiva del estado de red mediante ConnectivityManager: asigna el estado RECIBIDA si existe conexion o PENDIENTE SIN RED si el dispositivo se encuentra desconectado.
- Consulta, visualizacion de comprobante y sincronizacion diferida de solicitudes desde la pantalla de perfil.

### 3. Modo Claro y Modo Oscuro Dinamico
- Sistema de temas conmutable en tiempo de ejecucion desde el menu lateral y desde la pantalla de configuracion.
- Paletas de colores definidas para contraste legible tanto en modo claro institucional como en modo oscuro.
- Recursos de marca optimizados en formato WebP con transiciones suaves mediante Crossfade.

### 4. Integracion con Servicios Nativos del Dispositivo
- Marcador telefonico: Uso de Intent(Intent.ACTION_DIAL) con gestion de excepciones try/catch para iniciar comunicaciones directas con los encargados de area sin requerir permisos invasivos.
- Difusion de informacion: Uso de Intent(Intent.ACTION_SEND) con seleccionador nativo y bloque seguro try/catch para compartir detalles de los programas a traves de aplicaciones instaladas en el dispositivo.

### 5. Validacion Desacoplada y Proteccion de Datos
- Modulo de validacion independiente de la interfaz grafica (`Validadores.kt`) con funciones puras para correo, clave, nombre, mensaje y formato de RUT.
- Validacion de formato de RUT chileno con guion y digito verificador o letra K (`12345678-5` o `12345678-K`), alineada con la politica de uso estricto de datos ficticios y sinteticos.

### 6. Icono de Aplicacion Adaptativo
- Configuracion de icono adaptativo en mipmap-anydpi-v26 con el isotipo de las tres sonrisas institucionales sobre fondo oscuro (#141414), compatible con cualquier formato de icono del sistema operativo (circular, cuadrado redondeado o squircle).

---

## Estructura del Codigo Fuente

```text
app/src/main/java/com/duoc/corpaliv
├── data/
│   ├── AppDatabase.kt          # Configuracion de Room y patron Singleton
│   ├── EstadoDataStore.kt      # Persistencia de preferencias con DataStore
│   ├── SolicitudDao.kt         # Metodos de acceso a datos para SQLite
│   └── SolicitudEntity.kt      # Tabla de solicitudes en base de datos local
├── model/
│   ├── ProgramaSocial.kt       # Informacion y catalogo de programas sociales
│   ├── UsuarioErrores.kt       # Mensajes de error de validacion
│   └── UsuarioUiState.kt       # Estado inmutable de autenticacion y formulario
├── navigation/
│   ├── AppNavigation.kt        # Grafo de navegacion y gestion de rutas
│   ├── NavigationEvent.kt      # Eventos reactivos para desacoplar el NavHost
│   └── Screen.kt               # Definicion de destinos de la aplicacion
├── ui/
│   ├── components/
│   │   ├── AppComponents.kt    # Barras superior/inferior, botones y drawer
│   │   └── IlustracionPrograma.kt # Ilustraciones vectoriales en Canvas
│   ├── screens/                # Pantallas completas de la aplicacion
│   ├── theme/                  # Colores reactivos, tipografia y tema Material 3
│   └── utils/                  # Utilidades nativas (llamadas, compartir y responsive)
├── validation/
│   └── Validadores.kt          # Reglas de negocio puras (correo, clave, RUT)
└── viewmodel/
    ├── EstadoViewModel.kt      # Gestion de preferencias, tema y favoritos
    ├── MainViewModel.kt        # Canal compartido de eventos de navegacion
    └── UsuarioViewModel.kt     # Logica de login, formulario y operaciones Room
```

---

## Pruebas Unitarias

El proyecto cuenta con pruebas unitarias automaticas implementadas con JUnit 4 en `ValidadoresTest.kt` para asegurar la confiabilidad de las reglas de negocio principales:

| Metodo de Prueba | Descripcion | Resultado |
| :--- | :--- | :---: |
| `validarCorreo_conCorreoValido_retornaNull` | Acepta correos con formato estandar valido (ej: colaborador@ejemplo.cl) | Aprobado |
| `validarCorreo_conCorreoInvalido_retornaError` | Rechaza correos sin arroba o con estructura incorrecta | Aprobado |
| `validarClave_conClaveCorta_retornaError` | Exige contrasenas de al menos 8 caracteres con combinacion alfanumerica | Aprobado |
| `validarRut_conRutValidoFicticio_retornaNull` | Valida formato chileno de RUT comercial con guion y digito verificador o K ficticio | Aprobado |
| `validarRut_conRutInvalido_retornaError` | Rechaza ruts sin guion o con cantidad incorrecta de digitos | Aprobado |
| `validarCredencialesLogin_conDatosCorrectosEIncorrectos` | Valida credenciales oficiales de prueba y rechaza accesos con claves incorrectas | Aprobado |

---

## Requisitos y Compilacion

### Requisitos Previos
- Android Studio (Iguana, Jellyfish, Ladybug, Koala o superior)
- Java Development Kit (JDK) version 11 (o superior)
- Android SDK: Min SDK 24 (Android 7.0) / Target SDK 37 (Android 15)

### Comandos de Terminal (Gradle Wrapper)

Ejecutar la suite de pruebas unitarias:
```bash
./gradlew.bat testDebugUnitTest
```

Compilar y generar el paquete APK de depuracion:
```bash
./gradlew.bat assembleDebug
```

Limpiar temporales y recompilar el proyecto completo:
```bash
./gradlew.bat clean assembleDebug
```

El archivo instalable generado se ubica en:
```text
app/build/outputs/apk/debug/app-debug.apk
```

---

## Entrega Parcial 4: Microservicio, API Externa, Pruebas y APK Firmado

### 1. Endpoints Utilizados (Microservicio y API Externa)

#### Microservicio propio — Spring Boot (`http://10.0.2.2:8080/api/v1/solicitudes`)
Ubicado en `microservicio-corpaliv/` (Kotlin + Spring Boot + JPA + Bean Validation + H2 persistente en `./data/corpalivdb`). Sincroniza de forma hibrida con Room SQLite (`SolicitudRepository` con proteccion `Mutex`).

| Metodo | Ruta | Descripcion |
| :--- | :--- | :--- |
| `GET` | `/api/v1/solicitudes` | Lista todas las solicitudes registradas en H2 |
| `GET` | `/api/v1/solicitudes/{id}` | Obtiene el detalle de una solicitud por ID |
| `POST` | `/api/v1/solicitudes` | Crea una nueva solicitud y genera folio (`201 Created`) |
| `PUT` | `/api/v1/solicitudes/{id}` | Actualiza colaboracion, RUT comercial o mensaje (`200 OK`) |
| `DELETE` | `/api/v1/solicitudes/{id}` | Elimina una solicitud existente (`204 No Content`) |

#### API externa — Indicadores economicos (`https://mindicador.cl/`)

| Metodo | Ruta | Descripcion |
| :--- | :--- | :--- |
| `GET` | `/api` | Consulta valores diarios de UF, UTM, Dolar y fecha en `ContactScreen` |

---

### 2. Pasos para Ejecutar el Microservicio y Pruebas

#### Levantar el microservicio Spring Boot (Java 21 LTS)
```powershell
cd microservicio-corpaliv
.\mvnw.cmd spring-boot:run
```
- Puerto local: `http://localhost:8080` (desde el emulador Android: `http://10.0.2.2:8080/api/v1/`).
- Consola web H2: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/corpalivdb;AUTO_SERVER=TRUE`, usuario `sa`, contrasena vacia).

#### Suite completa de pruebas (90 pruebas en la app Android)
- `app/src/test/`: **78 pruebas unitarias** (JUnit 4, MockK, `kotlinx-coroutines-test`) para `Validadores`, `SolicitudDto`, `SolicitudRepository`, `MindicadorRepository`, `MainViewModel` e `IndicadoresViewModel`.
- `app/src/androidTest/`: **12 pruebas de interfaz Jetpack Compose** (`UiPantallasComposeTest`).

```powershell
# Pruebas unitarias Android (78 tests)
.\gradlew.bat :app:testDebugUnitTest

# Pruebas UI Compose en emulador (12 tests)
.\gradlew.bat :app:connectedDebugAndroidTest

# Pruebas del microservicio Spring Boot
cd microservicio-corpaliv; .\mvnw.cmd test

# Compilar APK firmado en modo release
.\gradlew.bat :app:assembleRelease
```

---

### 3. Captura del APK Firmado y `.jks`

- **Llave Keystore (`.jks`):** [`release/corpaliv-release.jks`](release/corpaliv-release.jks) (`alias: corpaliv_key`)
- **APK firmado (`release`):** [`release/app-release.apk`](release/app-release.apk)
- **Configuracion Gradle:** [`app/build.gradle.kts`](app/build.gradle.kts) (`signingConfigs.release` enlazado a `buildTypes.release`)

![Captura del APK firmado y archivo JKS](release/captura-apk-jks.png)

---

*Proyecto estudiantil de desarrollo de software para plataformas moviles, dedicado a la labor formativa de la Fundacion CORPALIV.*
