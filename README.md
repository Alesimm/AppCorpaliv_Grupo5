# AppCorpaliv - Red Alba Lab

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Room Database](https://img.shields.io/badge/Room_SQLite-Local_Storage-47A248?logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![DataStore](https://img.shields.io/badge/Jetpack-DataStore_Preferences-F4511E?logo=android&logoColor=white)](https://developer.android.com/topic/libraries/architecture/datastore)
[![JUnit 4](https://img.shields.io/badge/Tests_Unitarios-6%2F6_Aprobados-brightgreen)](https://junit.org/)
[![Min SDK](https://img.shields.io/badge/Min_SDK-24_(Android_7.0)-informational)](https://developer.android.com/)
[![Target SDK](https://img.shields.io/badge/Target_SDK-34_(Android_14)-informational)](https://developer.android.com/)

Aplicacion movil nativa para Android desarrollada como proyecto estudiantil de caracter formativo, orientada a apoyar la labor comunitaria de la Fundacion CORPALIV (Corporacion de Padres y Amigos por el Limitado Visual) y su red de inclusion sociolaboral Alba Lab.

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
- Lenguaje: Kotlin 2.0
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
- Marcador telefonico: Uso de Intent(Intent.ACTION_DIAL) para iniciar comunicaciones directas con los encargados de area sin requerir permisos invasivos.
- Difusion de informacion: Uso de Intent(Intent.ACTION_SEND) con seleccionador nativo para compartir detalles de los programas a traves de aplicaciones instaladas en el dispositivo.

### 5. Icono de Aplicacion Adaptativo
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

| Prueba | Descripcion | Resultado |
| :--- | :--- | :---: |
| validarCorreo_formatoInvalido_retornaError | Rechaza correos sin arroba o con dominio no valido | Aprobado |
| validarCorreo_formatoValido_retornaNull | Acepta correos con formato estandar valido | Aprobado |
| validarClave_menosDe8Caracteres_retornaError | Exige contrasenas de al menos 8 caracteres | Aprobado |
| validarNombre_campoVacio_retornaError | Exige que el nombre contenga al menos 3 caracteres | Aprobado |
| validarRut_alianzaEmpresarial_validaFormatoChileno | Valida formato y digito verificador de RUT comercial | Aprobado |
| validarCredencialesLogin_credencialesPrueba | Valida las credenciales de prueba y rechaza accesos incorrectos | Aprobado |

---

## Requisitos y Compilacion

### Requisitos Previos
- Android Studio (Iguana, Jellyfish, Ladybug, Koala o superior)
- Java Development Kit (JDK) version 17
- Android SDK: Min SDK 24 (Android 7.0) / Target SDK 34 (Android 14)

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

*Proyecto estudiantil de desarrollo de software para plataformas moviles, dedicado a la labor formativa de la Fundacion CORPALIV.*
