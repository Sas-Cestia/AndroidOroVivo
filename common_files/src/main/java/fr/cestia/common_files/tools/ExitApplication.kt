package fr.cestia.common_files.tools

import android.app.Activity
import android.content.Context
import android.widget.Toast

fun exitApplication(context: Context) {
    // Fermer l'appli
    Toast.makeText(context, "Fermeture de l'application", Toast.LENGTH_SHORT).show()
    (context as Activity).finishAffinity()
}
