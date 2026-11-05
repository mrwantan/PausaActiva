# Pausa Activa (app nativa Android)

Versión nativa (Kotlin + Jetpack Compose) de tu PWA. Los avisos usan **alarmas exactas del sistema** (`AlarmManager`),
así que no dependen de Chrome, de internet ni de un servidor push.

## Cómo obtener el APK (GitHub Actions)

1. Crea un repositorio nuevo en GitHub (puede ser privado) y sube **todo el contenido de esta carpeta**
   (incluida la carpeta oculta `.github`).
2. Entra a la pestaña **Actions** → «Build APK». Se ejecuta solo al subir; también puedes darle **Run workflow**.
3. Cuando termine (unos 5–8 min), abre la ejecución y descarga el artefacto **PausaActiva-apk** (zip con `app-release.apk`).
4. Pasa el APK al celular, ábrelo y permite «instalar apps de origen desconocido» para tu navegador o gestor de archivos.

## Después de instalar: comprobar que los avisos funcionan

1. Abre la app y sigue la introducción. En el último paso concede los **3 permisos** (notificaciones, alarmas exactas, batería sin restricciones).
2. En **Ajustes** toca **«Probar con la app cerrada (10 s)»**, cierra la app (deslízala fuera de recientes) y espera: debe sonar el aviso.
3. Activa los recordatorios en Inicio.

Si tu marca es Xiaomi, Huawei, Oppo, Vivo o Samsung, puede hacer falta además activar el «inicio automático»:
https://dontkillmyapp.com/

## Qué hace cada pieza

| Archivo | Función |
|---|---|
| `notify/AlarmScheduler.kt` | Programa la próxima alarma exacta y la rearma (arranque, cambio de hora, abrir la app) |
| `notify/Receivers.kt` | `AlarmReceiver` (muestra el aviso y programa el siguiente), `ActionReceiver` (Posponer), `BootReceiver` |
| `notify/Notifier.kt` | Canal de importancia alta, sonido, vibración y botones «Empezar» / «Posponer 5 min» |
| `data/Schedule.kt` | Horario laboral y días (misma lógica que la web) |
| `assets/figure.html` + `exercises.js` + `figures.css` | Animaciones SVG de los ejercicios, tal cual las de tu web, dentro de un WebView local |

## Abrirlo en Android Studio (opcional)

Abre la carpeta en Android Studio; descargará Gradle y las dependencias. Requiere JDK 17 (el que trae Android Studio sirve).

## Notas

- El APK se firma con la clave de depuración: sirve para instalarlo directamente, **no** para publicarlo en Google Play.
- Se declara `USE_EXACT_ALARM`; Google Play lo restringe a apps de alarma/calendario, pero para instalar el APK a mano no hay problema.
- El sonido y la vibración del **aviso** los controla Android (Ajustes → botón «Sonido e importancia del aviso»); los interruptores de la app afectan solo a la pausa.
