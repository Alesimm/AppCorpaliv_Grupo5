package com.duoc.corpaliv.model

// modelo de datos para los programas de capacitacion e inclusion de corpaliv
// contiene la informacion que se muestra en el catalogo y en el detalle

// datos que describen cada programa social como titulo resumen e impacto
data class ProgramaSocial(
    val id: String,
    val categoria: String,
    val titulo: String,
    val resumen: String,
    val descripcion: String,
    val impactoTitulo: String,
    val testimonio: String,
    val autorTestimonio: String,
    val destinoAporte: String,
    val telefonoContacto: String
)

// lista con los tres programas oficiales que se muestran en la app
val listaProgramasAlbaLab = listOf(
    ProgramaSocial(
        id = "taller_oficios",
        categoria = "CAPACITACIÓN",
        titulo = "Taller de Oficios",
        resumen = "Formación práctica en carpintería, oficios y elaboración de productos en Alba Lab.",
        descripcion = "Espacio formativo donde jóvenes y adultos con discapacidad múltiple desarrollan habilidades prácticas y preparación para el trabajo.",
        impactoTitulo = "48 participantes al año",
        testimonio = "Programa demostrativo de formación práctica.",
        autorTestimonio = "Dato ficticio de ejemplo.",
        destinoAporte = "Materiales de taller, herramientas adaptadas y monitores.",
        telefonoContacto = "+56900000000"
    ),
    ProgramaSocial(
        id = "insercion_laboral",
        categoria = "EMPLEO",
        titulo = "Inserción Laboral",
        resumen = "Vinculación con empresas aliadas y acompañamiento en inclusión laboral.",
        descripcion = "Conectamos a jóvenes preparados en Alba Lab con organizaciones comprometidas con la inclusión, brindando apoyo continuo al entorno laboral.",
        impactoTitulo = "35 alianzas sociolaborales",
        testimonio = "Programa demostrativo de inclusión laboral.",
        autorTestimonio = "Dato ficticio de ejemplo.",
        destinoAporte = "Preparación para el empleo, tutorías y seguimiento.",
        telefonoContacto = "+56900000000"
    ),
    ProgramaSocial(
        id = "autonomia_transicion",
        categoria = "EDUCACIÓN",
        titulo = "Transición a la Vida Adulta",
        resumen = "Formación en habilidades cotidianas, comunicación y autonomía personal.",
        descripcion = "Programa educativo enfocado en el desarrollo de la comunicación, la autonomía y la participación comunitaria de jóvenes con discapacidad múltiple.",
        impactoTitulo = "45 estudiantes apoyados",
        testimonio = "Programa demostrativo de educación especial.",
        autorTestimonio = "Dato ficticio de ejemplo.",
        destinoAporte = "Implementos sensoriales, ayudas técnicas y apoyo pedagógico.",
        telefonoContacto = "+56200000000"
    )
)
