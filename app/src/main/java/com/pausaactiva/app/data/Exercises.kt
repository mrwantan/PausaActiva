package com.pausaactiva.app.data

data class Exercise(
    val id: String,
    val name: String,
    val zone: String,
    val posture: String, // "Sentado" o "De pie"
    val text: String,
    val tips: List<String>,
)

object Exercises {
    val all: List<Exercise> = listOf(
        Exercise("neck-tilt", "Inclinación lateral de cuello", "Cuello", "Sentado",
            "Lleva la oreja hacia el hombro sin subir el hombro. Alterna de lado despacio.",
            listOf("Espalda recta y hombros relajados", "Mantén unos 5 segundos en cada lado", "Respira profundo, sin rebotes")),
        Exercise("neck-turn", "Giro de cuello", "Cuello", "Sentado",
            "Gira la cabeza para mirar por encima de un hombro y vuelve al centro. Alterna lados.",
            listOf("Mueve solo la cabeza, el torso queda quieto", "Mantén la barbilla paralela al suelo", "Gira hasta donde sea cómodo")),
        Exercise("shrug", "Hombros arriba y abajo", "Hombros", "Sentado",
            "Sube los hombros hacia las orejas, sostén 2 segundos y suéltalos de golpe.",
            listOf("Inhala al subir, exhala al soltar", "Deja caer los brazos relajados", "Repite a ritmo tranquilo")),
        Exercise("roll", "Círculos de hombros", "Hombros", "Sentado",
            "Dibuja círculos grandes hacia atrás con los hombros. A la mitad del tiempo cambia de dirección.",
            listOf("Movimiento amplio y lento", "Mantén la cabeza quieta", "Suelta la tensión de la nuca")),
        Exercise("chest", "Apertura de pecho", "Pecho y hombros", "Sentado",
            "Lleva los brazos hacia atrás, junta los omóplatos y saca el pecho. Vuelve despacio.",
            listOf("Mira al frente, cuello largo", "No arquees la zona lumbar", "Sostén 2 segundos atrás")),
        Exercise("wrist", "Estiramiento de muñeca", "Muñecas", "Sentado",
            "Estira un brazo al frente y dobla la muñeca hacia abajo, ayudándote con la otra mano. Cambia de brazo a la mitad.",
            listOf("Codo estirado, hombro relajado", "Estira hasta sentir tensión suave, sin dolor", "Haz también hacia arriba")),
        Exercise("fingers", "Abrir y cerrar las manos", "Manos y dedos", "Sentado",
            "Cierra los puños con firmeza y luego abre los dedos al máximo. Repite sin prisa.",
            listOf("Abre los dedos bien separados", "Sacude las manos al terminar", "Ideal después de escribir mucho")),
        Exercise("twist", "Torsión de columna", "Espalda", "Sentado",
            "Gira el torso hacia un lado sujetando el respaldo de la silla, mantén y cambia de lado.",
            listOf("Pies firmes en el suelo", "Crece hacia arriba antes de girar", "Gira sin forzar la espalda baja")),
        Exercise("lateral", "Inclinación lateral de tronco", "Costados", "Sentado",
            "Sube un brazo sobre la cabeza e inclínate hacia el lado contrario. Cambia de brazo a la mitad.",
            listOf("No te eches hacia adelante", "Siente el estiramiento en el costado", "Respira profundo al inclinarte")),
        Exercise("heel", "Elevación de talones", "Piernas", "De pie",
            "Apóyate en tu escritorio, sube sobre las puntas de los pies, sostén 1 segundo y baja lento.",
            listOf("Sube y baja controlado", "Aprieta pantorrillas arriba", "Mantén el cuerpo alineado")),
        Exercise("march", "Marcha en el sitio", "Piernas y circulación", "De pie",
            "Levanta las rodillas de forma alterna a ritmo cómodo, moviendo los brazos.",
            listOf("Apóyate en el escritorio si lo necesitas", "Rodillas a media altura", "Respira con normalidad")),
        Exercise("knee", "Extensión de rodilla", "Piernas", "Sentado",
            "Estira una pierna hasta dejarla recta, sostén 2 segundos y baja. Alterna de pierna.",
            listOf("Espalda apoyada y recta", "Punta de pie hacia ti", "Mueve despacio, sin balanceo")),
        Exercise("eyes", "Descanso visual", "Ojos", "Sentado",
            "Aparta la vista de la pantalla: mira a lo lejos, mueve los ojos a los lados, arriba y abajo, y parpadea.",
            listOf("Regla 20-20-20: cada 20 min mira a 6 m por 20 s", "Parpadea de forma consciente", "Relaja la frente y la mandíbula")),
        Exercise("breath", "Respiración profunda", "Relajación", "Sentado",
            "Inhala por la nariz contando 4 y exhala suave por la boca contando 4.",
            listOf("Lleva el aire al abdomen", "Hombros sueltos", "Cierra los ojos si te sientes cómodo")),
        Exercise("stand-twist", "Giro de tronco de pie", "Espalda y cintura", "De pie",
            "Con los pies firmes, gira el torso a un lado y al otro dejando que los brazos acompañen.",
            listOf("Pies a la anchura de los hombros", "Gira desde la cintura, no desde el cuello", "Movimiento suave y continuo")),
        Exercise("fold", "Flexión del torso hacia adelante", "Espalda y piernas", "De pie",
            "Con las rodillas ligeramente flexionadas, deja caer el torso, brazos y cabeza. Sube despacio.",
            listOf("Rodillas suaves, nunca bloqueadas", "Apóyate en el escritorio si hace falta", "Sube despacio, vértebra por vértebra")),
        Exercise("reach", "Estírate hacia el techo", "Hombros y espalda", "De pie",
            "Sube los brazos por los lados hasta arriba, estírate bien alto y baja despacio.",
            listOf("Inhala al subir, exhala al bajar", "Alarga la columna, no arquees la espalda", "Hombros lejos de las orejas")),
        Exercise("squat", "Sentadilla suave", "Piernas y glúteos", "De pie",
            "Con los pies a la anchura de los hombros, baja como si fueras a sentarte y sube. Brazos al frente.",
            listOf("Rodillas en línea con los pies", "Espalda recta, pecho arriba", "Baja solo hasta donde sea cómodo")),
        Exercise("quad", "Estiramiento de muslo", "Muslo (cuádriceps)", "De pie",
            "Apóyate en el escritorio, lleva un talón hacia el glúteo y sujeta el pie. Cambia de pierna a la mitad.",
            listOf("Rodillas juntas y pelvis neutra", "Mira al frente para mantener el equilibrio", "Siente el estiramiento al frente del muslo")),
        Exercise("balance", "Equilibrio en un pie", "Equilibrio y core", "De pie",
            "Levanta una rodilla y mantén el equilibrio unos segundos. Cambia de pierna a la mitad.",
            listOf("Apóyate en el escritorio al inicio", "Fija la vista en un punto frente a ti", "Abdomen firme, hombros relajados")),
        Exercise("ankle", "Movilidad de tobillo", "Tobillos y circulación", "Sentado",
            "Levanta la punta del pie y bájala; luego dibuja círculos con el tobillo. Cambia de pie a la mitad.",
            listOf("Mueve solo el tobillo, la pierna queda quieta", "Círculos amplios y lentos", "Ayuda a la circulación de las piernas")),
    )

    /** Rutina de 7 pasos: 3 sentado → 3 de pie → 1 sentado. */
    val slots: List<List<String>> = listOf(
        listOf("neck-tilt", "neck-turn", "shrug", "roll"),
        listOf("wrist", "fingers", "chest"),
        listOf("twist", "lateral", "chest", "roll"),
        listOf("fold", "stand-twist", "reach"),
        listOf("heel", "squat", "quad", "balance"),
        listOf("march", "reach", "squat", "balance"),
        listOf("eyes", "breath", "knee", "ankle"),
    )

    fun indexOf(id: String): Int = all.indexOfFirst { it.id == id }.coerceAtLeast(0)

    /** Una rutina distinta cada vez: sin repetir ejercicios, evitando los de la sesión anterior y prefiriendo los menos usados. */
    fun buildRoutine(p: Prefs): List<Exercise> {
        val used = mutableSetOf<String>()
        val last = p.n
        val byId = all.associateBy { it.id }
        val routine = slots.map { ids ->
            var pool = ids.filter { it !in used }
            if (pool.isEmpty()) pool = ids
            val fresh = pool.filter { p.useOf(it) != last }
            if (fresh.isNotEmpty()) pool = fresh
            pool = pool.shuffled().sortedBy { p.useOf(it) }
            val top = pool.take(maxOf(2, (pool.size + 1) / 2))
            val id = top.random()
            used += id
            byId.getValue(id)
        }
        p.n = last + 1
        p.markUsed(routine.map { it.id }, p.n)
        return routine
    }
}
