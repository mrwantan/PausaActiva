/* Ejercicios de oficina con animaciones SVG propias (sin GIFs externos, funcionan sin internet). */
(() => {
  const svg = (s) => `<svg class="fig" viewBox="0 0 200 200" role="img" aria-hidden="true">${s}</svg>`;

  // Piezas reutilizables
  const CH_FRONT = '<path class="f" d="M66 92V140H134V92M66 140V180M134 140V180"/>';
  const CH_SIDE = '<path class="f" d="M66 80V142H130M72 142V180M124 142V180"/>';
  const GROUND = '<path class="f" d="M30 180H170"/>';
  const LEGS_FRONT = '<path class="b" d="M90 132V176M110 132V176"/>';
  const TORSO_FRONT = '<path class="b" d="M100 132V84"/>';
  const HEAD_FRONT = '<circle class="h" cx="100" cy="48" r="17"/>';
  const EYES = (cls) => `<g class="a ${cls} eyes"><circle cx="93" cy="46" r="2.8"/><circle cx="107" cy="46" r="2.8"/></g>`;
  const ARMS_FRONT = '<path class="b" d="M72 84H128M72 84V122M128 84V122"/>';

  const EXERCISES = [
    {
      id: 'neck-tilt', cat: 'cuello', zone: 'Cuello', posture: 'Sentado',
      name: 'Inclinación lateral de cuello',
      text: 'Lleva la oreja hacia el hombro sin subir el hombro. Alterna de lado despacio.',
      tips: ['Espalda recta y hombros relajados', 'Mantén unos 5 segundos en cada lado', 'Respira profundo, sin rebotes'],
      svg: svg(CH_FRONT + LEGS_FRONT + TORSO_FRONT + ARMS_FRONT +
        '<g class="a a-tilt"><path class="b" d="M100 84V66"/>' + HEAD_FRONT + '</g>'),
    },
    {
      id: 'neck-turn', cat: 'cuello', zone: 'Cuello', posture: 'Sentado',
      name: 'Giro de cuello',
      text: 'Gira la cabeza para mirar por encima de un hombro y vuelve al centro. Alterna lados.',
      tips: ['Mueve solo la cabeza, el torso queda quieto', 'Mantén la barbilla paralela al suelo', 'Gira hasta donde sea cómodo'],
      svg: svg(CH_FRONT + LEGS_FRONT + TORSO_FRONT + ARMS_FRONT + '<path class="b" d="M100 84V66"/>' + HEAD_FRONT + EYES('a-eyes')),
    },
    {
      id: 'shrug', cat: 'hombros', zone: 'Hombros', posture: 'Sentado',
      name: 'Hombros arriba y abajo',
      text: 'Sube los hombros hacia las orejas, sostén 2 segundos y suéltalos de golpe.',
      tips: ['Inhala al subir, exhala al soltar', 'Deja caer los brazos relajados', 'Repite a ritmo tranquilo'],
      svg: svg(CH_FRONT + LEGS_FRONT + TORSO_FRONT + '<path class="b" d="M100 84V66"/>' + HEAD_FRONT +
        '<g class="a a-shrug"><path class="b" d="M72 84H128M72 84V122M128 84V122"/></g>'),
    },
    {
      id: 'roll', cat: 'hombros', zone: 'Hombros', posture: 'Sentado',
      name: 'Círculos de hombros',
      text: 'Dibuja círculos grandes hacia atrás con los hombros. A la mitad del tiempo cambia de dirección.',
      tips: ['Movimiento amplio y lento', 'Mantén la cabeza quieta', 'Suelta la tensión de la nuca'],
      svg: svg(CH_FRONT + LEGS_FRONT + TORSO_FRONT + '<path class="b" d="M100 84V66"/>' + HEAD_FRONT +
        '<g class="a a-roll"><path class="b" d="M72 84H128M72 84V122M128 84V122"/></g>'),
    },
    {
      id: 'chest', cat: 'hombros', zone: 'Pecho y hombros', posture: 'Sentado',
      name: 'Apertura de pecho',
      text: 'Lleva los brazos hacia atrás, junta los omóplatos y saca el pecho. Vuelve despacio.',
      tips: ['Mira al frente, cuello largo', 'No arquees la zona lumbar', 'Sostén 2 segundos atrás'],
      svg: svg(CH_SIDE +
        '<path class="b" d="M92 130H140V176M140 176H154M92 130V82"/><circle class="h" cx="96" cy="60" r="17"/>' +
        '<g class="a a-chest"><path class="b" d="M92 88V126"/></g>'),
    },
    {
      id: 'wrist', cat: 'manos', zone: 'Muñecas', posture: 'Sentado',
      name: 'Estiramiento de muñeca',
      text: 'Estira un brazo al frente y dobla la muñeca hacia abajo, ayudándote con la otra mano. Cambia de brazo a la mitad.',
      tips: ['Codo estirado, hombro relajado', 'Estira hasta sentir tensión suave, sin dolor', 'Haz también hacia arriba'],
      svg: svg(CH_SIDE +
        '<path class="b" d="M92 130H140V176M140 176H154M92 130V82"/><circle class="h" cx="96" cy="60" r="17"/>' +
        '<path class="b" d="M92 90H150"/><g class="a a-wrist"><path class="b" d="M150 90H174"/></g>'),
    },
    {
      id: 'fingers', cat: 'manos', zone: 'Manos y dedos', posture: 'Sentado',
      name: 'Abrir y cerrar las manos',
      text: 'Cierra los puños con firmeza y luego abre los dedos al máximo. Repite sin prisa.',
      tips: ['Abre los dedos bien separados', 'Sacude las manos al terminar', 'Ideal después de escribir mucho'],
      svg: svg('<rect class="h" x="62" y="112" width="76" height="52" rx="22"/>' +
        '<path class="b a a-fing" style="transform-origin:76px 112px" d="M76 112V56"/>' +
        '<path class="b a a-fing" style="transform-origin:92px 112px" d="M92 112V46"/>' +
        '<path class="b a a-fing" style="transform-origin:108px 112px" d="M108 112V50"/>' +
        '<path class="b a a-fing" style="transform-origin:124px 112px" d="M124 112V62"/>' +
        '<path class="b a a-fing" style="transform-origin:50px 150px" d="M50 150V108"/>'),
    },
    {
      id: 'twist', cat: 'hombros', zone: 'Espalda', posture: 'Sentado',
      name: 'Torsión de columna',
      text: 'Gira el torso hacia un lado sujetando el respaldo de la silla, mantén y cambia de lado.',
      tips: ['Pies firmes en el suelo', 'Crece hacia arriba antes de girar', 'Gira sin forzar la espalda baja'],
      svg: svg(CH_FRONT + LEGS_FRONT + TORSO_FRONT + '<path class="b" d="M100 84V66"/>' + HEAD_FRONT + EYES('a-eyes-r') +
        '<g class="a a-twl"><path class="b" d="M72 84H100M72 84V122"/></g>' +
        '<g class="a a-twr"><path class="b" d="M100 84H128M128 84V122"/></g>'),
    },
    {
      id: 'lateral', cat: 'hombros', zone: 'Costados', posture: 'Sentado',
      name: 'Inclinación lateral de tronco',
      text: 'Sube un brazo sobre la cabeza e inclínate hacia el lado contrario. Cambia de brazo a la mitad.',
      tips: ['No te eches hacia adelante', 'Siente el estiramiento en el costado', 'Respira profundo al inclinarte'],
      svg: svg(CH_FRONT + LEGS_FRONT +
        '<g class="a a-lean"><path class="b" d="M100 132V84M72 84H128M72 84V122M128 84V50Q128 24 106 22M100 84V66"/>' + HEAD_FRONT + '</g>'),
    },
    {
      id: 'heel', cat: 'piernas-p', zone: 'Piernas', posture: 'De pie',
      name: 'Elevación de talones',
      text: 'Apóyate en tu escritorio, sube sobre las puntas de los pies, sostén 1 segundo y baja lento.',
      tips: ['Sube y baja controlado', 'Aprieta pantorrillas arriba', 'Mantén el cuerpo alineado'],
      svg: svg(GROUND + '<g class="a a-foot"><path class="b" d="M84 176H112"/></g>' +
        '<g class="a a-rise"><path class="b" d="M94 72V124M94 124V172M94 86L98 120"/><circle class="h" cx="96" cy="54" r="17"/></g>'),
    },
    {
      id: 'march', cat: 'activa-p', zone: 'Piernas y circulación', posture: 'De pie',
      name: 'Marcha en el sitio',
      text: 'Levanta las rodillas de forma alterna a ritmo cómodo, moviendo los brazos.',
      tips: ['Apóyate en el escritorio si lo necesitas', 'Rodillas a media altura', 'Respira con normalidad'],
      svg: svg(GROUND +
        '<g class="legB"><g class="a a-thigh" style="transform-origin:100px 122px"><path class="b" d="M100 122V150" opacity=".55"/>' +
        '<g class="a a-shin" style="transform-origin:100px 150px"><path class="b" d="M100 150V176" opacity=".55"/></g></g>' +
        '<g class="a a-armb" style="transform-origin:100px 84px"><path class="b" d="M100 84V116" opacity=".55"/></g></g>' +
        '<path class="b" d="M100 72V122"/><circle class="h" cx="102" cy="54" r="17"/>' +
        '<g class="a a-thigh" style="transform-origin:100px 122px"><path class="b" d="M100 122V150"/>' +
        '<g class="a a-shin" style="transform-origin:100px 150px"><path class="b" d="M100 150V176"/></g></g>' +
        '<g class="a a-arma" style="transform-origin:100px 84px"><path class="b" d="M100 84V116"/></g>'),
    },
    {
      id: 'knee', cat: 'relax', zone: 'Piernas', posture: 'Sentado',
      name: 'Extensión de rodilla',
      text: 'Estira una pierna hasta dejarla recta, sostén 2 segundos y baja. Alterna de pierna.',
      tips: ['Espalda apoyada y recta', 'Punta de pie hacia ti', 'Mueve despacio, sin balanceo'],
      svg: svg(CH_SIDE +
        '<path class="b" d="M92 130V82M92 90L110 114M92 130H140"/><circle class="h" cx="96" cy="60" r="17"/>' +
        '<g class="a a-knee"><path class="b" d="M140 130V176M140 176H156"/></g>'),
    },
    {
      id: 'eyes', cat: 'relax', zone: 'Ojos', posture: 'Sentado',
      name: 'Descanso visual',
      text: 'Aparta la vista de la pantalla: mira a lo lejos, mueve los ojos a los lados, arriba y abajo, y parpadea.',
      tips: ['Regla 20-20-20: cada 20 min mira a 6 m por 20 s', 'Parpadea de forma consciente', 'Relaja la frente y la mandíbula'],
      svg: svg('<g class="a a-blink"><ellipse class="ey" cx="100" cy="100" rx="78" ry="46"/>' +
        '<g class="a a-pupil"><circle class="h" cx="100" cy="100" r="22"/><circle class="sf" cx="108" cy="92" r="6"/></g></g>'),
    },
    {
      id: 'breath', cat: 'relax', zone: 'Relajación', posture: 'Sentado',
      name: 'Respiración profunda',
      text: 'Inhala por la nariz contando 4 y exhala suave por la boca contando 4.',
      tips: ['Lleva el aire al abdomen', 'Hombros sueltos', 'Cierra los ojos si te sientes cómodo'],
      svg: svg('<circle class="h a a-breath" style="opacity:.25" cx="100" cy="100" r="60"/><circle class="h" cx="100" cy="100" r="38"/>' +
        '<text class="tx a a-tin" x="100" y="106" text-anchor="middle">Inhala</text>' +
        '<text class="tx a a-tout" x="100" y="106" text-anchor="middle">Exhala</text>'),
    },
    {
      id: 'stand-twist', cat: 'espalda-p', zone: 'Espalda y cintura', posture: 'De pie',
      name: 'Giro de tronco de pie',
      text: 'Con los pies firmes, gira el torso a un lado y al otro dejando que los brazos acompañen.',
      tips: ['Pies a la anchura de los hombros', 'Gira desde la cintura, no desde el cuello', 'Movimiento suave y continuo'],
      svg: svg(GROUND + '<path class="b" d="M96 132L90 176M104 132L110 176"/>' + TORSO_FRONT + '<path class="b" d="M100 84V66"/>' + HEAD_FRONT + EYES('a-eyes-r') +
        '<g class="a a-twl"><path class="b" d="M72 84H100M72 84V122"/></g>' +
        '<g class="a a-twr"><path class="b" d="M100 84H128M128 84V122"/></g>'),
    },
    {
      id: 'fold', cat: 'espalda-p', zone: 'Espalda y piernas', posture: 'De pie',
      name: 'Flexión del torso hacia adelante',
      text: 'Con las rodillas ligeramente flexionadas, deja caer el torso, brazos y cabeza. Sube despacio.',
      tips: ['Rodillas suaves, nunca bloqueadas', 'Apóyate en el escritorio si hace falta', 'Sube despacio, vértebra por vértebra'],
      svg: svg(GROUND + '<path class="b" d="M100 124V176M100 176H118"/>' +
        '<g class="a a-fold"><path class="b" d="M100 124V76"/><circle class="h" cx="102" cy="56" r="17"/>' +
        '<g class="a a-foldarm"><path class="b" d="M100 88V122"/></g></g>'),
    },
    {
      id: 'reach', cat: 'activa-p', zone: 'Hombros y espalda', posture: 'De pie',
      name: 'Estírate hacia el techo',
      text: 'Sube los brazos por los lados hasta arriba, estírate bien alto y baja despacio.',
      tips: ['Inhala al subir, exhala al bajar', 'Alarga la columna, no arquees la espalda', 'Hombros lejos de las orejas'],
      svg: svg(GROUND + '<path class="b" d="M96 132L90 176M104 132L110 176"/>' + TORSO_FRONT + '<path class="b" d="M100 84V66"/>' + HEAD_FRONT +
        '<path class="b" d="M72 84H128"/>' +
        '<g class="a a-up"><path class="b" d="M72 84V124"/></g><g class="a a-upr"><path class="b" d="M128 84V124"/></g>'),
    },
    {
      id: 'squat', cat: 'piernas-p', zone: 'Piernas y glúteos', posture: 'De pie',
      name: 'Sentadilla suave',
      text: 'Con los pies a la anchura de los hombros, baja como si fueras a sentarte y sube. Brazos al frente.',
      tips: ['Rodillas en línea con los pies', 'Espalda recta, pecho arriba', 'Baja solo hasta donde sea cómodo'],
      svg: svg(GROUND +
        '<g class="a a-sqb">' +
        '<g class="a a-sqt" style="transform-origin:100px 122px"><path class="b" d="M100 122V150"/>' +
        '<g class="a a-sqs" style="transform-origin:100px 150px"><path class="b" d="M100 150V178"/></g></g>' +
        '<g class="a a-sqtor" style="transform-origin:100px 122px"><path class="b" d="M100 122V72"/><circle class="h" cx="102" cy="54" r="17"/>' +
        '<g class="a a-sqarm" style="transform-origin:100px 84px"><path class="b" d="M100 84V116"/></g></g></g>'),
    },
    {
      id: 'quad', cat: 'piernas-p', zone: 'Muslo (cuádriceps)', posture: 'De pie',
      name: 'Estiramiento de muslo',
      text: 'Apóyate en el escritorio, lleva un talón hacia el glúteo y sujeta el pie. Cambia de pierna a la mitad.',
      tips: ['Rodillas juntas y pelvis neutra', 'Mira al frente para mantener el equilibrio', 'Siente el estiramiento al frente del muslo'],
      svg: svg(GROUND + '<path class="b" d="M100 122V176M100 176H118"/><path class="b" d="M100 122V74"/><circle class="h" cx="102" cy="54" r="17"/>' +
        '<path class="b" d="M100 88V118"/>' +
        '<path class="b" d="M100 122V150" opacity=".7"/><g class="a a-quad" style="transform-origin:100px 150px"><path class="b" d="M100 150V178" opacity=".7"/></g>'),
    },
    {
      id: 'balance', cat: 'piernas-p', zone: 'Equilibrio y core', posture: 'De pie',
      name: 'Equilibrio en un pie',
      text: 'Levanta una rodilla y mantén el equilibrio unos segundos. Cambia de pierna a la mitad.',
      tips: ['Apóyate en el escritorio al inicio', 'Fija la vista en un punto frente a ti', 'Abdomen firme, hombros relajados'],
      svg: svg(GROUND + '<path class="b" d="M100 122V176M100 176H118"/><path class="b" d="M100 122V74"/><circle class="h" cx="102" cy="54" r="17"/>' +
        '<path class="b" d="M100 88H138"/>' +
        '<g class="a a-balt" style="transform-origin:100px 122px"><path class="b" d="M100 122V150" opacity=".7"/>' +
        '<g class="a a-bals" style="transform-origin:100px 150px"><path class="b" d="M100 150V178" opacity=".7"/></g></g>'),
    },
    {
      id: 'ankle', cat: 'relax', zone: 'Tobillos y circulación', posture: 'Sentado',
      name: 'Movilidad de tobillo',
      text: 'Levanta la punta del pie y bájala; luego dibuja círculos con el tobillo. Cambia de pie a la mitad.',
      tips: ['Mueve solo el tobillo, la pierna queda quieta', 'Círculos amplios y lentos', 'Ayuda a la circulación de las piernas'],
      svg: svg(CH_SIDE + '<path class="b" d="M92 130V82M92 90L110 114M92 130H140V176"/><circle class="h" cx="96" cy="60" r="17"/>' +
        '<g class="a a-ankle"><path class="b" d="M140 176H158"/></g>'),
    },
  ];

  window.EXERCISES = EXERCISES;
  // Rutina de 7 pasos: 3 sentado → 3 de pie → 1 sentado (solo 2 cambios de postura).
  // Cada bloque ofrece varias opciones; la app elige una distinta cada vez y evita repetir ejercicios en la misma rutina.
  window.ROUTINE_SLOTS = [
    ['neck-tilt', 'neck-turn', 'shrug', 'roll'],            // 1 sentado: cuello y hombros
    ['wrist', 'fingers', 'chest'],                          // 2 sentado: brazos y manos
    ['twist', 'lateral', 'chest', 'roll'],                  // 3 sentado: tronco
    ['fold', 'stand-twist', 'reach'],                       // 4 de pie: espalda
    ['heel', 'squat', 'quad', 'balance'],                   // 5 de pie: piernas
    ['march', 'reach', 'squat', 'balance'],                 // 6 de pie: activación
    ['eyes', 'breath', 'knee', 'ankle'],                    // 7 sentado: relajación
  ];
})();
