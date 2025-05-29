package com.example.koalaappm13

import android.app.Application
import com.example.koalaappm13.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class KoalaApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    lateinit var userRepository: UserRepository
    lateinit var sessionRepository: SessionRepository
    lateinit var consentFormRepository: ConsentFormRepository
    lateinit var programaTvRepository: ProgramaTvRepository
    lateinit var productionRepository: ProductionRepository  // ✅ Añadido

    override fun onCreate() {
        super.onCreate()

        val database = AppDatabase.getDatabase(this, applicationScope)

        userRepository = UserRepository(database.userDao(), database.sessionDao())
        sessionRepository = SessionRepository(database.sessionDao())
        consentFormRepository = ConsentFormRepository(
            consentFormDao = database.consentFormDao(),
            productionDao = database.productionDao()
        )
        programaTvRepository = ProgramaTvRepository(database.programaTvDao())
        productionRepository = ProductionRepository(database.productionDao()) // ✅ Inicializado correctamente
    }
}
