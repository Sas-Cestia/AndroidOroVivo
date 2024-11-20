package fr.cestia.sinex_orvx

import android.content.Context
import android.os.Process
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.cestia.common_files.datawedge.DWConfig
import fr.cestia.common_files.datawedge.ScannerManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.exitProcess

@Singleton
class CrashHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scannerManager: ScannerManager,
    private val dwConfig: DWConfig
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        // Log de l'exception
        Log.e("CrashHandler", "Uncaught exception: $throwable")

        // Nettoyage des ressources critiques (exemple)
        cleanUpResources()

        Process.killProcess(Process.myPid())
        exitProcess(2)

    }

    private fun cleanUpResources() {
        // Exemple : Libérer des BroadcastReceivers ou connexions
        scannerManager.unregisterReceiver()
        Log.d("CrashHandler", "Resources cleaned up")

        dwConfig.disableDatawedgeConfig()
    }
}